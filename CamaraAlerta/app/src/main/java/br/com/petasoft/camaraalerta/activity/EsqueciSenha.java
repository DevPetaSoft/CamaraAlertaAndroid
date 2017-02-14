package br.com.petasoft.camaraalerta.activity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
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
import model.Configuration;

public class EsqueciSenha extends AppCompatActivity {

    private static Configuration configuration;
    private EditText emailRecuperar;

    private ProgressDialog progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_esqueci_senha);
        emailRecuperar = (EditText) findViewById(R.id.emailRecuperar);
    }

    public void recuperarSenha(View v){
        progress = new ProgressDialog(this);
        progress.setMessage("Enviando e-mail");
        progress.setCancelable(false);
        progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progress.setIndeterminate(true);
        progress.setProgress(0);
        progress.show();
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = configuration.base_url+"cidadao/gerarToken";
        StringRequest getRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response) {
                        Log.d("Response", response);

                        progress.dismiss();
                        Toast toast = Toast.makeText(getApplicationContext(), response, Toast.LENGTH_LONG);
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

                                progress.dismiss();
                                // Caso a requisição não possua sucesso, ela envia uma mensagem com o retorno do WS
                                JSONObject json = new JSONObject(result);

                                Toast toast = Toast.makeText(getApplicationContext(), json.getString("message"), Toast.LENGTH_LONG);
                                toast.show();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }else{

                            progress.dismiss();
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
                params.put("email", emailRecuperar.getText().toString());
                return params;
            }
        };
        getRequest.setRetryPolicy(new DefaultRetryPolicy(
                0,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(getRequest);

        /* Dialogo para envio de recuperação
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Enviaremos um email com instruções para recuperar sua senha!")
                .setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //do things
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
        */
    }
}
