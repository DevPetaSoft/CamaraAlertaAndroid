package br.com.petasoft.camaraalerta.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import br.com.petasoft.camaraalerta.R;
import model.Cidadao;
import model.Configuration;

public class Registrar extends AppCompatActivity {
    private static Configuration configuration;
    private EditText editTextNome;
    //private EditText editTextLogin;
    private EditText editTextEmail;
    private EditText editTextPassword;
    private EditText editTextConfirmPassword;
    private Intent intent;
    private String email;
    private String pass;
    private String nome;
    private boolean termosAceitos = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registrar);
        editTextNome = (EditText) findViewById(R.id.registerName);
       //editTextLogin = (EditText) findViewById(R.id.registerLogin);
        editTextEmail = (EditText) findViewById(R.id.registerEmail);
        editTextPassword = (EditText) findViewById(R.id.registerPassword);
        editTextConfirmPassword = (EditText) findViewById(R.id.registerConfirmPassword);
    }

    public void onCheckboxClicked(View view) {
        // Is the view now checked?
        boolean checked = ((CheckBox) view).isChecked();

        // Check which checkbox was clicked
        switch(view.getId()) {
            case R.id.checkbox_termos:
                if (checked) {
                    termosAceitos = true;
                }else {
                    termosAceitos = false;
                }
                break;
        }
    }

    public void novoRegistro(View v){
        RequestQueue queue = Volley.newRequestQueue(this);
        nome = editTextNome.getText().toString();
        email = editTextEmail.getText().toString();
        pass = editTextPassword.getText().toString();
        String url = configuration.base_url+"user/novoCidadao";
        intent = new Intent(this, MainActivity.class);
        if(!nome.equals("") && !email.equals("") && !pass.equals("")){
            StringRequest getRequest = new StringRequest(Request.Method.POST, url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            Log.d("Response", response);

                            Toast toast = Toast.makeText(getApplicationContext(), "Conta criada com sucesso!", Toast.LENGTH_LONG);
                            toast.show();
                            try {
                                //Realiza o parser do JSON vindo do WebService
                                //JSONObject json = new JSONObject(response);
                                //JsonParser parser = new JsonParser();
                                //JsonElement mJson = parser.parse(json.getString("data"));
                                /* Transforma o JSON em um objeto Cidadao
                                 * Grava o cidadao no objeto estático de configurações para ser acessado
                                 * por qualquer arquivo.*/
                                configuration.usuario = configuration.gson.fromJson(response, Cidadao.class);
                                if (configuration.usuario == null) {
                                    Log.i("Error", "não foi possível realizar o login");
                                } else {
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
                            if (error.networkResponse != null && error.networkResponse.data != null) {
                                String result = new String(error.networkResponse.data);
                                try {
                                    // Caso a requisição não possua sucesso, ela envia uma mensagem com o retorno do WS
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
                    params.put("nome", editTextNome.getText().toString());
                    //params.put("login", editTextLogin.getText().toString());
                    params.put("email", editTextEmail.getText().toString());
                    params.put("senha", editTextPassword.getText().toString());


                    return params;
                }
            };

            //Verifica se as senhas digitadas são iguais, e aciona o request para o servidor
            if (editTextPassword.getText().toString().equals(editTextConfirmPassword.getText().toString())) {
                if(termosAceitos) {
                    queue.add(getRequest);
                } else {
                    Toast toast = Toast.makeText(getApplicationContext(), "Você precisa aceitar os Termos e Condições", Toast.LENGTH_LONG);
                    toast.show();
                }
            } else {
                Toast toast = Toast.makeText(getApplicationContext(), "As senhas digitadas não são iguais.", Toast.LENGTH_LONG);
                toast.show();
            }
        } else {
            Toast toast = Toast.makeText(getApplicationContext(), "Preencha todos os campos", Toast.LENGTH_LONG);
            toast.show();
        }
    }

    public void termosCondicoes(View v){
        Intent intent = new Intent(this, TermoCompromisso.class);
        startActivity(intent);
    }
}
