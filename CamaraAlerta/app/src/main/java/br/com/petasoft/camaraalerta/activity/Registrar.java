package br.com.petasoft.camaraalerta.activity;

import android.app.ProgressDialog;
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

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

import Utils.HashUtils;
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
    private EditText editTextTelefone;
    //private Intent intent;
    private String telefone;
    private String email;
    private String pass;
    private String nome;
    private boolean termosAceitos = false;

    private ProgressDialog progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registrar);
        editTextNome = (EditText) findViewById(R.id.registerName);
        editTextTelefone = (EditText) findViewById(R.id.registerTelefone);
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

        // Criando janela de progresso
        progress = new ProgressDialog(this);
        progress.setMessage("Enviando novo cadastro");
        progress.setCancelable(false);
        progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progress.setIndeterminate(true);
        progress.setProgress(0);

        nome = editTextNome.getText().toString();
        email = editTextEmail.getText().toString();
        pass = editTextPassword.getText().toString();
        telefone = editTextTelefone.getText().toString();
        String url = configuration.base_url+"user/novoCidadao";
        //intent = new Intent(this, MainActivity.class);
        if(!nome.equals("") && !email.equals("") && !pass.equals("") && !telefone.equals("")){
            StringRequest getRequest = new StringRequest(Request.Method.POST, url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            Log.d("Response", response);

                            try {
                                //Realiza o parser do JSON vindo do WebService
                                /* Transforma o JSON em um objeto Cidadao
                                 * Grava o cidadao no objeto estático de configurações para ser acessado
                                 * por qualquer arquivo.*/
                                progress.dismiss();
                                configuration.usuario = configuration.gson.fromJson(response, Cidadao.class);
                                Toast toast = Toast.makeText(getApplicationContext(), "Conta criada com sucesso!", Toast.LENGTH_LONG);
                                toast.show();
                                if (configuration.usuario == null) {
                                    Log.i("Error", "não foi possível realizar o login");
                                } else {
                                    Log.i("Nome", configuration.usuario.getNome());
                                    //Redireciona a aplicação para a tela principal
                                    Configuration.loginNormal = true;
                                    Intent intent = new Intent(getBaseContext(), LoginActivity.class);
                                    intent.putExtra("email", editTextEmail.getText().toString());
                                    intent.putExtra("senha", editTextPassword.getText().toString());
                                    startActivity(intent);
                                    finish();
                                    //startActivity(intent);
                                    //finish();
                                }
                            } catch (Exception e) {
                                progress.dismiss();
                                Toast toast = Toast.makeText(getApplicationContext(), response, Toast.LENGTH_LONG);
                                toast.show();
                                e.printStackTrace();
                            }

                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            progress.dismiss();
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
                    String senha = editTextPassword.getText().toString();
                    senha = HashUtils.criptografiaDeSenha(senha);
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("nome", editTextNome.getText().toString());
                    params.put("telefone", telefone);
                    params.put("email", editTextEmail.getText().toString());
                    params.put("senha",senha);


                    return params;
                }
            };

            //Verifica se as senhas digitadas são iguais, e aciona o request para o servidor
            if (editTextPassword.getText().toString().equals(editTextConfirmPassword.getText().toString())) {
                if(termosAceitos) {
                    progress.show();
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
