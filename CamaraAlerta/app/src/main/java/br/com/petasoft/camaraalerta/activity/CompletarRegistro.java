package br.com.petasoft.camaraalerta.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import br.com.petasoft.camaraalerta.R;
import dto.FacebookDTO;
import model.Cidadao;
import model.Configuration;

public class CompletarRegistro extends AppCompatActivity {


    private EditText editTelefone;
    private boolean termosAceitos = false;
    private Intent intent;
    private Configuration configuration;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_completar_registro);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        editTelefone = (EditText) findViewById(R.id.registerTelefone);

        progressDialog = new ProgressDialog(this);

    }

    public void completarRegistro(View view){

        if(termosAceitos == true){
            progressDialog.setTitle("Carregando");
            progressDialog.setMessage("Espere enquanto está sendo realizado o cadastro");
            progressDialog.setCancelable(false); // disable dismiss by tapping outside of the dialog
            progressDialog.show();
            //Realiza conexão com o webservice para realizar o login
            Log.i("Facebook Login", "Chamada da funcao");
            intent = new Intent(CompletarRegistro.this, MainActivity.class);
            RequestQueue queue = Volley.newRequestQueue(this);
            String url = configuration.base_url + "user/facebookTelefone";
            StringRequest getRequest = new StringRequest(Request.Method.POST, url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {
                                Log.i("Response", response.substring(0,30));

                                configuration.usuario = configuration.gson.fromJson(response, Cidadao.class);
                                progressDialog.dismiss();
                                startActivity(intent);
                                finish();

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
                    params.put("id", String.valueOf(configuration.usuario.getId()));
                    params.put("telefone",editTelefone.getText().toString());

                    return params;
                }
            };
            queue.add(getRequest);

        }else{
            Toast toast = Toast.makeText(getApplicationContext(), "Para prosseguir, é necessário aceitar os termos de compromisso", Toast.LENGTH_LONG);
            toast.show();
        }

    }

    public void onCheckboxClicked(View view){
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

    public void termosCondicoes(View view){

        Intent intent = new Intent(this, TermoCompromisso.class);
        startActivity(intent);
    }

}
