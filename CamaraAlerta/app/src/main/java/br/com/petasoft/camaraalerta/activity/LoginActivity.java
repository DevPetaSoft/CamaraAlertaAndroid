package br.com.petasoft.camaraalerta.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.TextViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.Profile;
import com.facebook.ProfileTracker;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.auth.api.Auth;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import Utils.HashUtils;
import br.com.petasoft.camaraalerta.R;
import dto.FacebookDTO;
import model.Cidadao;
import model.Configuration;

/**
 * Created by Lucas on 9/18/2016.
 */

public class LoginActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener/*, View.OnClickListener*/ {
    private static Configuration configuration;
    private LoginButton loginButton;
    private CheckBox lembrar;
    private EditText editLogin, editPass;
    private String senhaSalva;
    private CallbackManager callbackManager;
    private Profile profile;
    private LoginResult loginResul;
    private ProgressDialog progressDialog;
    public static final String MY_PREFS_NAME = "package br.com.petasoft.camaraalerta.FB_LOGIN_FILE";


    private Intent intent;
    private Intent completarRegistro;

    private SignInButton signInButton;
    private GoogleApiClient mGoogleApiClient;
    private static final String TAG = "TAG";
    private static final int RC_SIGN_IN = 10;

    private AccessTokenTracker accessTokenTracker;
    private boolean facebookButtonPressed = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        //Facebook facebook = new Facebook("@string/facebook_app_id");
        setContentView(R.layout.activity_login);
        callbackManager = CallbackManager.Factory.create();
        //SharedPreferences sharedPref = this.getSharedPreferences(MY_PREFS_NAME, Context.MODE_PRIVATE);
        progressDialog = new ProgressDialog(this);


        // Buscando os componentes da tela
        loginButton = (LoginButton) findViewById(R.id.login_button);
        editLogin = (EditText) findViewById(R.id.editLogin);
        editPass = (EditText) findViewById(R.id.editPassword);
        lembrar = (CheckBox) findViewById(R.id.checkboxLembrar);
        accessTokenTracker = new AccessTokenTracker() {
            @Override
            protected void onCurrentAccessTokenChanged(AccessToken oldAccessToken, AccessToken newAccessToken) {
                updateWithToken(newAccessToken);
            }
        };

        loginButton.setReadPermissions("public_profile", "email");
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                loginResul = loginResult;
                facebookButtonPressed = true;
                final Profile profile = Profile.getCurrentProfile();
                String accessToken = loginResult.getAccessToken().getToken();
                Log.i("accessToken", accessToken);
                progressDialog.setTitle("Carregando");
                progressDialog.setMessage("Espere enquanto está sendo realizado o login...");
                progressDialog.setCancelable(false); // disable dismiss by tapping outside of the dialog
                progressDialog.show();

                GraphRequest request = GraphRequest.newMeRequest(loginResult.getAccessToken(), new GraphRequest.GraphJSONObjectCallback() {

                    @Override
                    public void onCompleted(JSONObject object, GraphResponse response) {
                        Log.i("LoginActivity", response.toString());
                        // Get facebook data from login
                        Bundle bFacebookData = getFacebookData(object);
                        try {
                            String email = object.getString("email");
                            Log.i("Email", email);
                            String name =  object.getString("first_name")+" " +object.getString("last_name");
                            Log.i("Name", name);
                            saveNameAndEmail(name, email);
                            //TODO: Passar a foto para o banco de dados
                            if (object.has("picture")) {
                                String profilePicUrl = object.getJSONObject("picture").getJSONObject("data").getString("url");
                                // set profile image to imageview using Picasso or Native methods
                            }
                            facebookLogin(loginResul,object.getString("email"),name,object.getString("id"));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
                Bundle parameters = new Bundle();
                parameters.putString("fields", "id, first_name, last_name, email, picture.type(large)"); // Parámetros que pedimos a facebook
                //parameters.putString("fields", "id, first_name, last_name, email,gender, birthday, location, picture.type(large)"); // Parámetros que pedimos a facebook
                request.setParameters(parameters);
                request.executeAsync();
            }


            @Override
            public void onCancel() {
                System.out.println("onCancel");
            }

            @Override
            public void onError(FacebookException exception) {
                Log.v("LoginActivity", exception.getCause().toString());
            }
        });
        /**
         * Google+
        findViewById(R.id.sign_in_button).setOnClickListener(this);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .requestProfile()
                .build();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this , this )
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
        SignInButton signInButton = (SignInButton) findViewById(R.id.sign_in_button);
        signInButton.setSize(SignInButton.SIZE_STANDARD);
        signInButton.setScopes(gso.getScopeArray());
         */

        if(getIntent().getStringExtra("email")!=null){
            String emailDeRegistro = getIntent().getStringExtra("email");
            String passDeRegistro = getIntent().getStringExtra("senha");
            editLogin.setText(emailDeRegistro);
            editPass.setText(passDeRegistro);
        } else {
            updateWithToken(AccessToken.getCurrentAccessToken());
            normalLoginAlreadyLogged();
        }
    }

    private void saveNameAndEmail(String name, String email){
        SharedPreferences.Editor editor = getApplication().getApplicationContext().getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).edit();
        Log.d("Facebook before n", name);
        Log.d("Facebook before e", email);
        editor.putString("fbName", name);
        editor.putString("fbEmail", email);
        boolean voltou = editor.commit();
        if(voltou){
            Log.d("Facebook", "deu commit");
        } else {
            Log.d("Facebook", "nao deu commit");
        }
    }

    private void normalLoginAlreadyLogged(){
        SharedPreferences prefs = getApplication().getApplicationContext().getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
        int alreadyLogged = prefs.getInt("nlFeito", -1);
        if(alreadyLogged!=1){
            int lembrarLogPass = prefs.getInt("nlLembrar", -1);
            if(lembrarLogPass==1){
                String emailLembrado = prefs.getString("nlEmailLembrar", "");
                String passLembrado = prefs.getString("nlPassLembrar", "");
                senhaSalva = HashUtils.criptografiaDeSenha(passLembrado);
                editLogin.setText(emailLembrado);
                editPass.setText(passLembrado);
                lembrar.setChecked(true);
            }
        } else {
            Log.d("alreadyLogged", "logando");
            final String email = prefs.getString("nlEmail", "No email found");
            final String pass = prefs.getString("nlPass", "No pass found");
            senhaSalva = HashUtils.criptografiaDeSenha(pass);
            if(!email.equals("No email found")){
                editLogin.setText(email);
                editPass.setText(pass);
                intent = new Intent(this, MainActivity.class);
                RequestQueue queue = Volley.newRequestQueue(this);
                String url = configuration.base_url + "user/login";
                Log.i("URL", configuration.base_url + "user/login");
                StringRequest getRequest = new StringRequest(Request.Method.POST, url,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                // response
                                Log.d("Response", response);
                                //Toast.makeText()

                                try {
                                    //Realiza o parser do JSON vindo do WebService
                            /* Transforma o JSON em um objeto Cidadao
                             * Grava o cidadao no objeto estático de configurações para ser acessado
                             * por qualquer arquivo.*/
                                    configuration.usuario = configuration.gson.fromJson(response, Cidadao.class);
                                    if (configuration.usuario == null) {
                                        Log.i("Error", "não foi possível realizar o login");
                                    } else {
                                        Toast toast = Toast.makeText(getApplicationContext(), "Login efetuado com sucesso", Toast.LENGTH_LONG);
                                        toast.show();
                                        Log.i("Nome", configuration.usuario.getNome());
                                        //Redireciona a aplicação para a tela principal

                                        Configuration.loginNormal = true;
                                        startActivity(intent);
                                        finish();
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }


                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                // error
                                if (error.networkResponse != null && error.networkResponse.data != null) {
                                    String result = new String(error.networkResponse.data);
                                    try {

                                        JSONObject json = new JSONObject(result);

                                        Toast toast = Toast.makeText(getApplicationContext(), json.getString("message"), Toast.LENGTH_LONG);
                                        toast.show();
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                } else {
                                    Toast toast = Toast.makeText(getApplicationContext(), "Não foi possível se conectar com o servidor", Toast.LENGTH_LONG);
                                    toast.show();
                                }
                            }
                        }
                ) {
                    @Override
                    protected Map<String, String> getParams() {
                        Map<String, String> params = new HashMap<String, String>();
                        params.put("login", email);
                        params.put("senha", senhaSalva);

                        return params;
                    }
                };
                queue.add(getRequest);
            }
        }

    }

    private void updateWithToken(final AccessToken currentAccessToken){
        if(currentAccessToken != null && !facebookButtonPressed) {
            Log.d("Facebook", "ja logado");
            SharedPreferences prefs = getApplication().getApplicationContext().getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
            final String name = prefs.getString("fbName", "No name found");
            final String email = prefs.getString("fbEmail", "No email found");
            /*
            Log.d("Facebook token", currentAccessToken.getToken());

            Log.d("Facebook name rec", name);
            Log.d("Facebook email rec", email);
            Log.d("Facebook", "reached");
            */
            intent = new Intent(LoginActivity.this, MainActivity.class);
            RequestQueue queue = Volley.newRequestQueue(this);
            String url = configuration.base_url + "user/facebookJaLogado";
            StringRequest getRequest = new StringRequest(Request.Method.POST, url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {
                                Log.i("Response", response.substring(0,30));

                                configuration.usuario = configuration.gson.fromJson(response, Cidadao.class);
                                Log.i("Facebook Login", "Envio do rest");
                                if (configuration.usuario == null) {
                                    Log.i("Error", "não foi possível realizar o login");
                                } else {
                                    Log.i("Name", configuration.usuario.getNome());
                                    Toast toast = Toast.makeText(getApplicationContext(), "Login com Facebook efetuado com sucesso", Toast.LENGTH_LONG);
                                    toast.show();
                                    //Redireciona a aplicação para a tela principal
                                    Configuration.loginFacebook = true;
                                    startActivity(intent);

                                    progressDialog.dismiss();
                                    finish();
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                                Toast toast = Toast.makeText(getApplicationContext(), response, Toast.LENGTH_LONG);
                                toast.show();
                                progressDialog.dismiss();
                            }


                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Log.i("Error", "Error response");

                            progressDialog.dismiss();
                            // error
                            if (error.networkResponse != null && error.networkResponse.data != null) {
                                String result = new String(error.networkResponse.data);
                                try {

                                    JSONObject json = new JSONObject(result);

                                    Toast toast = Toast.makeText(getApplicationContext(), json.getString("message"), Toast.LENGTH_LONG);
                                    toast.show();
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            } else {
                                Toast toast = Toast.makeText(getApplicationContext(), "Não foi possível se conectar com o servidor", Toast.LENGTH_LONG);
                                toast.show();
                                LoginManager.getInstance().logOut();
                                SharedPreferences.Editor editor = getApplication().getApplicationContext().getSharedPreferences(LoginActivity.MY_PREFS_NAME, MODE_PRIVATE).edit();
                                editor.putString("fbName", "");
                                editor.putString("fbEmail", "");
                                boolean voltou = editor.commit();
                                if(voltou){
                                    Log.d("Facebook", "deu commit logout");
                                } else {
                                    Log.d("Facebook", "nao deu  logout");
                                }
                            }
                        }
                    }
            ) {
                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("email",email);

                    return params;
                }
            };
            queue.add(getRequest);
        } else {
            Log.d("Facebook", "nao logado");
        }
    }


    private void signIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
        } else {
            callbackManager.onActivityResult(requestCode, resultCode, data);
        }

    }
    /*
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.sign_in_button:
                signIn();
                break;
        }
    }
    */

    private void handleSignInResult(GoogleSignInResult result) {
        Log.d(TAG, "handleSignInResult:" + result.isSuccess());
        if (result.isSuccess()) {
            // Signed in successfully, show authenticated UI.
            //GoogleSignInAccount acct = result.getSignInAccount();
            System.out.println("onSuccess");
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        } else {
            // Signed out, show unauthenticated UI.
            System.out.println("onFalse");
        }
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.e(TAG, "onConnectionFailed " + connectionResult.getErrorMessage());
    }

    // Função para realizar login com e-mail/login e senha
    public void normalLogin(View view) {
        intent = new Intent(this, MainActivity.class);

        //Realiza conexão com o webservice para realizar o login
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = configuration.base_url + "user/login";

        StringRequest getRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {

                            /* Transforma o JSON em um objeto Cidadao
                             * Grava o cidadao no objeto estático de configurações para ser acessado
                             * por qualquer arquivo.*/
                            configuration.usuario = configuration.gson.fromJson(response, Cidadao.class);
                            if (configuration.usuario == null) {
                                Toast toast = Toast.makeText(getApplicationContext(), "Não foi possível realizar o login", Toast.LENGTH_LONG);
                                toast.show();
                            } else {
                                Toast toast = Toast.makeText(getApplicationContext(), "Login efetuado com sucesso", Toast.LENGTH_LONG);
                                toast.show();

                                //Redireciona a aplicação para a tela principal

                                SharedPreferences.Editor editor = getApplication().getApplicationContext().getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).edit();
                                editor.putString("nlEmail", editLogin.getText().toString());
                                editor.putString("nlPass", editPass.getText().toString());
                                if(lembrar.isChecked()){
                                    editor.putString("nlEmailLembrar", editLogin.getText().toString());
                                    editor.putString("nlPassLembrar", editPass.getText().toString());
                                    editor.putInt("nlLembrar", 1);
                                } else {
                                    editor.putString("nlEmailLembrar", "");
                                    editor.putString("nlPassLembrar", "");
                                    editor.putInt("nlLembrar", -1);
                                }
                                editor.putInt("nlFeito", 1); //1- login feito e nao teve logout , 0 - logout
                                boolean voltou = editor.commit();

                                Configuration.loginNormal = true;
                                startActivity(intent);
                                finish();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            Toast toast = Toast.makeText(getApplicationContext(), response, Toast.LENGTH_LONG);
                            toast.show();
                        }


                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // error
                        if (error.networkResponse != null && error.networkResponse.data != null) {
                            String result = new String(error.networkResponse.data);
                            try {

                                JSONObject json = new JSONObject(result);



                                Toast toast = Toast.makeText(getApplicationContext(), json.getString("message"), Toast.LENGTH_LONG);
                                toast.show();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        } else {
                            Toast toast = Toast.makeText(getApplicationContext(), "Não foi possível se conectar com o servidor", Toast.LENGTH_LONG);
                            toast.show();
                        }
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                String senha = HashUtils.criptografiaDeSenha(editPass.getText().toString());
                Map<String, String> params = new HashMap<String, String>();
                params.put("login", editLogin.getText().toString());
                Log.i("Hash", senha);
                params.put("senha", senha);

                return params;
            }
        };
        queue.add(getRequest);
        //startActivity(intent);
        //finish();
    }

    public void facebookLogin(final LoginResult loginResult, final String email,final String name, final String token) {
        //Realiza conexão com o webservice para realizar o login
        Log.i("Facebook Login", "Chamada da funcao");
        intent = new Intent(LoginActivity.this, MainActivity.class);
        completarRegistro = new Intent(LoginActivity.this, CompletarRegistro.class);
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = configuration.base_url + "user/facebookLogin";
        StringRequest getRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            Log.i("Response", response.substring(0,30));
                            //Realiza o parser do JSON vindo do WebService
                            /* Transforma o JSON em um objeto Cidadao
                             * Grava o cidadao no objeto estático de configurações para ser acessado
                             * por qualquer arquivo.*/
                            FacebookDTO dto = configuration.gson.fromJson(response,FacebookDTO.class);
                            configuration.usuario = dto.getCidadao();
                            Log.i("Facebook Login", "Envio do rest");
                            if (configuration.usuario == null) {
                                Log.i("Error", "não foi possível realizar o login");
                            } else {
                                Log.i("Name", configuration.usuario.getNome());
                                Toast toast = Toast.makeText(getApplicationContext(), "Login com Facebook efetuado com sucesso", Toast.LENGTH_LONG);
                                toast.show();
                                if(dto.getNovo() || dto.getCidadao().getTelefone() == null || dto.getCidadao().getTelefone().isEmpty()){
                                    Configuration.loginFacebook = true;
                                    startActivity(completarRegistro);
                                }else{

                                    //Redireciona a aplicação para a tela principal
                                    Configuration.loginFacebook = true;
                                    startActivity(intent);
                                }

                                progressDialog.dismiss();
                                finish();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();

                            progressDialog.dismiss();
                        }


                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.i("Error", "Error response");

                        progressDialog.dismiss();
                        // error
                        if (error.networkResponse != null && error.networkResponse.data != null) {
                            String result = new String(error.networkResponse.data);
                            try {

                                JSONObject json = new JSONObject(result);

                                Toast toast = Toast.makeText(getApplicationContext(), json.getString("message"), Toast.LENGTH_LONG);
                                toast.show();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        } else {
                            Toast toast = Toast.makeText(getApplicationContext(), "Não foi possível se conectar com o servidor", Toast.LENGTH_LONG);
                            toast.show();
                        }
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("token", token);
                params.put("email",email);
                params.put("nome", name);

                return params;
            }
        };
        queue.add(getRequest);

    }

    public void registrarConta(View view) {
        Intent intent = new Intent(this, Registrar.class);
        startActivity(intent);
    }

    public void esqueciSenha(View view) {
        Intent intent = new Intent(this, EsqueciSenha.class);
        startActivity(intent);
    }


    private Bundle getFacebookData(JSONObject object) {

        try {
            Bundle bundle = new Bundle();
            String id = object.getString("id");


            URL profile_pic = new URL("https://graph.facebook.com/" + id + "/picture?width=200&height=150");
            bundle.putString("profile_pic", profile_pic.toString());


            bundle.putString("idFacebook", id);
            if (object.has("first_name"))
                bundle.putString("first_name", object.getString("first_name"));
            if (object.has("last_name"))
                bundle.putString("last_name", object.getString("last_name"));
            if (object.has("email"))
                bundle.putString("email", object.getString("email"));
            if (object.has("gender"))
                bundle.putString("gender", object.getString("gender"));
            if (object.has("birthday"))
                bundle.putString("birthday", object.getString("birthday"));
            if (object.has("location"))
                bundle.putString("location", object.getJSONObject("location").getString("name"));

            return bundle;
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return null;
    }
}
