package br.com.petasoft.camaraalerta.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.TextViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
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
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
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

import java.util.HashMap;
import java.util.Map;

import br.com.petasoft.camaraalerta.R;
import model.Cidadao;
import model.Configuration;

/**
 * Created by Lucas on 9/18/2016.
 */

public class LoginActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener, View.OnClickListener{
    private static Configuration configuration;
    private LoginButton loginButton;
    private EditText editLogin, editPass;
    private CallbackManager callbackManager;

    private Intent intent;

    private SignInButton signInButton;
    private GoogleApiClient mGoogleApiClient;
    private static final String TAG = "TAG";
    private static final int RC_SIGN_IN = 10;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        //Facebook facebook = new Facebook("@string/facebook_app_id");
        setContentView(R.layout.activity_login);
        callbackManager = CallbackManager.Factory.create();

        // Buscando os componentes da tela
        loginButton = (LoginButton) findViewById(R.id.login_button);
        editLogin = (EditText) findViewById(R.id.editLogin);
        editPass = (EditText) findViewById(R.id.editPassword);

        loginButton.setReadPermissions("email");
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
                System.out.println("onSuccess");
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

        findViewById(R.id.sign_in_button).setOnClickListener(this);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .requestProfile()
                .build();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
        SignInButton signInButton = (SignInButton) findViewById(R.id.sign_in_button);
        signInButton.setSize(SignInButton.SIZE_STANDARD);
        signInButton.setScopes(gso.getScopeArray());


    }


    private void signIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
        } else {
            callbackManager.onActivityResult(requestCode, resultCode, data);
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.sign_in_button:
                signIn();
                break;
        }
    }

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
        Log.e(TAG,"onConnectionFailed "+connectionResult.getErrorMessage());
    }

    // Função para realizar login com e-mail/login e senha
    public void normalLogin(View view) {
        intent = new Intent(this, MainActivity.class);
        //Realiza conexão com o webservice para realizar o login
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = configuration.base_url+"user/login";
        StringRequest getRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response) {
                        // response
                        Log.d("Response", response);
                        //Toast.makeText()

                        try {
                            //Realiza o parser do JSON vindo do WebService
                            JSONObject json = new JSONObject(response);
                            JsonParser parser = new JsonParser();
                            JsonElement mJson = parser.parse(json.getString("data"));
                            /* Transforma o JSON em um objeto Cidadao
                             * Grava o cidadao no objeto estático de configurações para ser acessado
                             * por qualquer arquivo.*/
                            configuration.usuario = configuration.gson.fromJson(mJson,Cidadao.class);
                            if(configuration.usuario == null){
                                Log.i("Error", "não foi possível realizar o login");
                            }else{
                                Toast toast = Toast.makeText(getApplicationContext(), "Login efetuado com sucesso", Toast.LENGTH_LONG );
                                toast.show();
                                //Redireciona a aplicação para a tela principal
                                startActivity(intent);
                                finish();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // error
                        if(error.networkResponse != null && error.networkResponse.data != null) {
                            String result = new String(error.networkResponse.data);
                            try {

                                JSONObject json = new JSONObject(result);

                                Toast toast = Toast.makeText(getApplicationContext(), json.getString("message"), Toast.LENGTH_LONG);
                                toast.show();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }else{
                            Toast toast = Toast.makeText(getApplicationContext(), "Não foi possível se conectar com o servidor", Toast.LENGTH_LONG);
                            toast.show();
                        }
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams()
            {
                Map<String, String>  params = new HashMap<String, String>();
                params.put("login", editLogin.getText().toString());
                params.put("senha", editPass.getText().toString());

                return params;
            }
        };
        queue.add(getRequest);
        startActivity(intent);
        finish();
    }

    public void registrarConta(View view){
        Intent intent = new Intent(this, Registrar.class);
        startActivity(intent);
    }

    public void esqueciSenha(View view){
        Intent intent = new Intent(this, EsqueciSenha.class);
        startActivity(intent);
    }


}
