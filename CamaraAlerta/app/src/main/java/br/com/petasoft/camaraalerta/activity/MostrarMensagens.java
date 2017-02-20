package br.com.petasoft.camaraalerta.activity;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.v4.app.NavUtils;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
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
import java.util.List;
import java.util.Map;

import br.com.petasoft.camaraalerta.R;
import dto.MensagensDTO;
import model.Configuration;
import model.MensagemChat;


public class MostrarMensagens extends AppCompatActivity{
    EditText editTextMensagem;
    ImageView botaoEnviar;
    MensagensDTO mensagensDTO;
    LinearLayout layoutMensagens;
    ScrollView scrollMensagens;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mostrar_mensagens);
        editTextMensagem = (EditText)findViewById(R.id.editTextMensagem);
        botaoEnviar = (ImageView)findViewById(R.id.buttonEnviarMensagem);
        layoutMensagens = (LinearLayout)findViewById(R.id.layoutMensagens);
        scrollMensagens = (ScrollView)findViewById(R.id.scrollMensagens);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarMensagens);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        getSupportActionBar().setHomeButtonEnabled(true);
        mensagensDTO = (MensagensDTO)intent.getSerializableExtra("MensagensDTO");
        List<MensagemChat> mensagens = mensagensDTO.getList();
        for(int i = 0 ; i<mensagens.size() ; i++){
            TextView texto = new TextView(this);
            texto.setText(mensagens.get(i).getMensagem());
            if(mensagens.get(i).getEnviadoPor()==0){
                Drawable drawable = ContextCompat.getDrawable(this, R.drawable.bubbleleft);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    texto.setBackground(drawable);
                } else {
                    texto.setBackgroundDrawable(drawable);
                }
                RelativeLayout layoutLeft = new RelativeLayout(layoutMensagens.getContext());
                RelativeLayout.LayoutParams rlp = new RelativeLayout.LayoutParams(
                        RelativeLayout.LayoutParams.WRAP_CONTENT,
                        RelativeLayout.LayoutParams.WRAP_CONTENT);
                rlp.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
                layoutLeft.addView(texto, rlp);
                layoutMensagens.addView(layoutLeft);
            } else {
                Drawable drawable = ContextCompat.getDrawable(this, R.drawable.bubbleright);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    texto.setBackground(drawable);
                } else {
                    texto.setBackgroundDrawable(drawable);
                }
                RelativeLayout layoutRight = new RelativeLayout(layoutMensagens.getContext());
                RelativeLayout.LayoutParams rlp = new RelativeLayout.LayoutParams(
                        RelativeLayout.LayoutParams.WRAP_CONTENT,
                        RelativeLayout.LayoutParams.WRAP_CONTENT);
                rlp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
                layoutRight.addView(texto, rlp);
                layoutMensagens.addView(layoutRight);
            }
            if(i+1 == mensagens.size()){
                texto.setFocusable(true);
                texto.setFocusableInTouchMode(true);
                texto.requestFocus();
            }
        }
        //Envia uma requisição para alterar o estado das mensagens para lida
        this.enviarRequisicaoDeLeituraMensagens();
        botaoEnviar.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                if(!editTextMensagem.getText().toString().equals("")){
                    final String mensagemASerEnviada = editTextMensagem.getText().toString();
                    final String canal = "" + mensagensDTO.getCanal().getId();

                    RequestQueue queue = Volley.newRequestQueue(MostrarMensagens.this);
                    String url = Configuration.base_url + "canalComunicacao/novaMensagemCidadao";
                    StringRequest getRequest = new StringRequest(Request.Method.POST, url,
                            new Response.Listener<String>() {
                                @Override
                                public void onResponse(String response) {
                                    Log.d("Resposta", response);
                                    if(response.equals("S")){
                                        Log.i("Sucesso", "Mensagem enviada");
                                        incluirMensagem(mensagemASerEnviada);
                                    } else {
                                        Log.i("Erro", response);
                                    }
                                }
                            },
                            new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    Log.i("Error", "Error response");
                                    if (error.networkResponse != null && error.networkResponse.data != null) {
                                        String result = new String(error.networkResponse.data);
                                        try {

                                            JSONObject json = new JSONObject(result);

                                            Toast toast = Toast.makeText(MostrarMensagens.this, json.getString("message"), Toast.LENGTH_LONG);
                                            toast.show();
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                    } else {
                                        Toast toast = Toast.makeText(MostrarMensagens.this, "Não foi possível se conectar com o servidor", Toast.LENGTH_LONG);
                                        toast.show();
                                    }
                                }
                            }
                    ) {
                        @Override
                        protected Map<String, String> getParams() {
                            Map<String, String> params = new HashMap<String, String>();
                            params.put("mensagem", mensagemASerEnviada);
                            params.put("canal", canal);

                            return params;
                        }
                    };
                    queue.add(getRequest);

                } else {
                    Toast.makeText(MostrarMensagens.this, "Você precisa escrever uma mensagem", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void incluirMensagem(String mensagemASerEnviada){
        TextView texto = new TextView(this);
        texto.setText(mensagemASerEnviada);
        Drawable drawable = ContextCompat.getDrawable(this, R.drawable.bubbleright);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            texto.setBackground(drawable);
        } else {
            texto.setBackgroundDrawable(drawable);
        }
        editTextMensagem.setText("");
        RelativeLayout layoutRight = new RelativeLayout(layoutMensagens.getContext());
        RelativeLayout.LayoutParams rlp = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);
        rlp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        layoutRight.addView(texto, rlp);
        layoutMensagens.addView(layoutRight);
    }

    public void enviarRequisicaoDeLeituraMensagens(){
        RequestQueue queue = Volley.newRequestQueue(MostrarMensagens.this);
        String url = Configuration.base_url + "canalComunicacao/leituraMensagensCidadao/"+mensagensDTO.getCanal().getId();
        StringRequest getRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("Resposta", "Mensagens lidas com sucesso");
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.i("Error", "Erro na leitura das mensagens");

                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                return params;
            }
        };
        queue.add(getRequest);
    }
}
