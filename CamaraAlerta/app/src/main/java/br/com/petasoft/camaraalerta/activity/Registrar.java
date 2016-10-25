package br.com.petasoft.camaraalerta.activity;

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
    private EditText editTextLogin;
    private EditText editTextEmail;
    private EditText editTextPassword;
    private EditText editTextConfirmPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registrar);
        editTextNome = (EditText) findViewById(R.id.registerName);
        editTextLogin = (EditText) findViewById(R.id.registerLogin);
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

                }else {

                }
                break;
        }
    }

    public void novoRegistro(View v){
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = configuration.base_url+"user/novoCidadao";
        StringRequest getRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response) {
                        Log.d("Response", response);

                        Toast toast = Toast.makeText(getApplicationContext(), "Conta criada com sucesso!", Toast.LENGTH_LONG);
                        toast.show();

                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if(error.networkResponse != null && error.networkResponse.data != null) {
                            String result = new String(error.networkResponse.data);
                            try {
                                // Caso a requisição não possua sucesso, ela envia uma mensagem com o retorno do WS
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
                params.put("nome",  editTextNome.getText().toString());
                params.put("login", editTextLogin.getText().toString());
                params.put("email", editTextEmail.getText().toString());
                params.put("senha", editTextPassword.getText().toString());


                return params;
            }
        };

        //Verifica se as senhas digitadas são iguais, e aciona o request para o servidor
        if(editTextPassword.getText().toString().equals(editTextConfirmPassword.getText().toString())){
            queue.add(getRequest);
        }else{
            Toast toast = Toast.makeText(getApplicationContext(), "As senhas digitadas não são iguais.", Toast.LENGTH_LONG);
            toast.show();
        }


    }
}
