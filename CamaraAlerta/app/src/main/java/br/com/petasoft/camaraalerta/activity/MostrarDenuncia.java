package br.com.petasoft.camaraalerta.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
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

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import br.com.petasoft.camaraalerta.R;
import dto.MensagensDTO;
import dto.MinhasDenunciasDTO;
import model.Configuration;
import model.Denuncia;
import model.MensagemChat;

import static android.widget.ImageView.ScaleType.CENTER_CROP;

public class MostrarDenuncia extends AppCompatActivity {

    private ImageView imagem;
    private TextView titulo;
    private TextView descricao;
    private Button botao;
    private ProgressDialog progress;
    private MensagensDTO mensagensDTO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mostrar_denuncia);
        imagem = (ImageView)findViewById(R.id.fotoDenuncia);
        titulo = (TextView)findViewById(R.id.tituloDenuncia);
        descricao = (TextView)findViewById(R.id.descricaoDenuncia);
        botao = (Button)findViewById(R.id.buttonMensagens);

        Intent intent = getIntent();
        Denuncia denuncia = (Denuncia)intent.getSerializableExtra("Denuncia");
        if(denuncia.isComunicacaoPermitida()==true){

            /**
             * Checando mensagens
             */
            progress=new ProgressDialog(this);
            progress.setMessage("Checando mensagens");
            progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progress.setIndeterminate(true);
            progress.setProgress(0);
            progress.show();

            RequestQueue queue = Volley.newRequestQueue(this);
            String url = Configuration.base_url + "canalComunicacao/mensPorD/" + denuncia.getId();
            StringRequest getRequest = new StringRequest(Request.Method.GET, url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            Log.d("Mensagens", response);
                            mensagensDTO = Configuration.gson.fromJson(response, MensagensDTO.class);
                            final List<MensagemChat> mensagens = mensagensDTO.getList();
                            if(mensagens == null){
                                Log.i("Error", "Usuário nao possui mensagens!");
                                Toast toast = Toast.makeText(MostrarDenuncia.this, "Usuário nao possui mensagens!", Toast.LENGTH_LONG);
                                toast.show();
                            } else {
                                int numeroMensagens = mensagens.size();
                                mudarBotao(numeroMensagens);
                            }
                            progress.dismiss();
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

                                    Toast toast = Toast.makeText(MostrarDenuncia.this, json.getString("message"), Toast.LENGTH_LONG);
                                    toast.show();
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            } else {
                                Toast toast = Toast.makeText(MostrarDenuncia.this, "Não foi possível se conectar com o servidor", Toast.LENGTH_LONG);
                                toast.show();
                            }
                            progress.dismiss();
                        }
                    }
            );
            queue.add(getRequest);
        }

        imagem.setScaleType(CENTER_CROP);


        File arquivo = new File(denuncia.getFotos().get(0));

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        //options.inInputShareable = true;
        //options.inPurgeable = true;

        BitmapFactory.decodeFile(arquivo.getPath(), options);
        if ((options.outWidth == -1) || (options.outHeight == -1)) {
            Log.d("Erro", "Foto inexistente");
        } else {

            int originalSize = (options.outHeight > options.outWidth) ? options.outHeight
                    : options.outWidth;

            BitmapFactory.Options opts = new BitmapFactory.Options();
            opts.inSampleSize = originalSize / 275;

            Bitmap myBitmap = BitmapFactory.decodeFile(arquivo.getPath(), opts);


            imagem.setImageBitmap(myBitmap);
        }

        titulo.setText(denuncia.getTitulo());
        descricao.setText(denuncia.getDescricao());
    }

    public void mudarBotao(int numeroMensagens){
        botao.setText("MENSAGENS (" + numeroMensagens + ")");
        botao.setBackgroundColor(Color.parseColor("#A5CA6F"));
        botao.setTextColor(Color.WHITE);
        botao.setEnabled(true);
        botao.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MostrarDenuncia.this, MostrarMensagens.class);
                intent.putExtra("MensagensDTO", mensagensDTO);
                startActivity(intent);
            }
        });
    }
}
