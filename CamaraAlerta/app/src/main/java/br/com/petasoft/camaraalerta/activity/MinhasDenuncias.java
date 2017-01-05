package br.com.petasoft.camaraalerta.activity;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import br.com.petasoft.camaraalerta.R;
import dto.MinhasDenunciasDTO;
import model.Configuration;
import model.Denuncia;

import static android.widget.ImageView.ScaleType.CENTER_CROP;

public class MinhasDenuncias extends Fragment{
    View myView;
    private ProgressDialog progress;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        myView = inflater.inflate(R.layout.activity_minhas_denuncias,container,false);
        //Toolbar toolbar = (Toolbar) getActivity().findViewById(R.id.toolbar);
        //((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);

        /*
        CollapsingToolbarLayout collapsingToolbar = (CollapsingToolbarLayout) myView.findViewById(R.id.toolbar_layout);
        collapsingToolbar.setTitleEnabled(false);

        toolbar.setTitle("Minhas Solicitações");

        FloatingActionButton fab = (FloatingActionButton) myView.findViewById(R.id.fabNovaDenuncia);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        */

        progress=new ProgressDialog(getActivity());
        progress.setMessage("Recebendo Solicitações");
        progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progress.setIndeterminate(true);
        progress.setProgress(0);
        progress.show();

        RequestQueue queue = Volley.newRequestQueue(getActivity());
        String url = Configuration.base_url + "denuncia/minhasDenuncias";
        StringRequest getRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("Solicitacoes", response);
                        //Type collectionType = new TypeToken<Collection<Denuncia>>(){}.getType();
                        //Collection<Denuncia> denuncias = configuration.gson.fromJson(response, collectionType);
                        MinhasDenunciasDTO denuncias = Configuration.gson.fromJson(response, MinhasDenunciasDTO.class);
                        final ArrayList<Denuncia> minhasDenuncias = denuncias.getMinhasDenuncias();
                        if(minhasDenuncias == null){
                            Log.i("Error", "Usuário nao possui denuncias!");
                            Toast toast = Toast.makeText(getActivity().getApplicationContext(), "Usuário nao possui denuncias!", Toast.LENGTH_LONG);
                            toast.show();
                        } else {
                            //ArrayList<Denuncia> minhasDenuncias = denuncias.getMinhasDenuncias();
                            LinearLayout layout = (LinearLayout)getActivity().findViewById(R.id.layoutMinhasDenuncias);
                            for (int i = 0; i < minhasDenuncias.size(); i++) {
                                CardView card = new CardView(getActivity().getApplicationContext());
                                card.setId(i);
                                CardView.LayoutParams layoutParams = new CardView.LayoutParams(CardView.LayoutParams.MATCH_PARENT,
                                        CardView.LayoutParams.WRAP_CONTENT);
                                card.setLayoutParams(layoutParams);
                                //card.setBackgroundColor(Color.WHITE);
                                card.setMaxCardElevation(15);
                                card.setUseCompatPadding(true);
                                ImageView image = new ImageView(getActivity().getApplicationContext());
                                image.setId(i+42);
                                image.setLayoutParams(new android.view.ViewGroup.LayoutParams(360, 360));

                                image.setScaleType(CENTER_CROP);


                                File arquivo = new File(minhasDenuncias.get(i).getFotos().get(0));

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


                                    image.setImageBitmap(myBitmap);


                                    // Adds the view to the layout
                                    card.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            Log.d("Id denuncia", ""+v.getId());
                                            int id = v.getId();
                                            Intent intent = new Intent(getActivity(), MostrarDenuncia.class);
                                            intent.putExtra("Denuncia", minhasDenuncias.get(id));
                                            startActivity(intent);
                                        }
                                    });
                                    RelativeLayout relative = new RelativeLayout(getActivity().getApplicationContext());
                                    relative.addView(image);

                                    TextView textoCartao = new TextView(getActivity().getApplicationContext());
                                    textoCartao.setText(minhasDenuncias.get(i).getTitulo());
                                    textoCartao.setTextSize(24);
                                    textoCartao.setTextColor(Color.DKGRAY);

                                    RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
                                            RelativeLayout.LayoutParams.WRAP_CONTENT,
                                            RelativeLayout.LayoutParams.WRAP_CONTENT);
                                    lp.addRule(RelativeLayout.RIGHT_OF, image.getId());
                                    lp.setMargins(20,0,0,0);
                                    relative.addView(textoCartao, lp);

                                    card.addView(relative);
                                    card.setBackgroundColor(Color.WHITE);
                                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                                            LinearLayout.LayoutParams.MATCH_PARENT,
                                            LinearLayout.LayoutParams.WRAP_CONTENT);
                                    params.setMargins(0,0,0,20);
                                    card.setLayoutParams(params);
                                    layout.addView(card);
                                }

                            }
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

                                Toast toast = Toast.makeText(getActivity().getApplicationContext(), json.getString("message"), Toast.LENGTH_LONG);
                                toast.show();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        } else {
                            Toast toast = Toast.makeText(getActivity().getApplicationContext(), "Não foi possível se conectar com o servidor", Toast.LENGTH_LONG);
                            toast.show();
                        }
                        progress.dismiss();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("idUsuario", "" + Configuration.usuario.getId());

                return params;
            }
        };
        queue.add(getRequest);
        return myView;
    }
}
