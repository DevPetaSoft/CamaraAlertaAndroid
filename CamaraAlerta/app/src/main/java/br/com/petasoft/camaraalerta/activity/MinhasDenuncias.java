package br.com.petasoft.camaraalerta.activity;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
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
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
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
import static android.widget.ImageView.ScaleType.FIT_XY;

public class MinhasDenuncias extends Fragment{
    View myView;
    private ProgressDialog progress;
    private ProgressDialog progress2;

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
        progress.setCancelable(false);
        progress.setIndeterminate(true);
        progress.setProgress(0);
        progress.show();


        RequestQueue queue = Volley.newRequestQueue(getActivity());
        String url = Configuration.base_url + "denuncia/minhasDenunciasUsu/" + Configuration.usuario.getId();
        StringRequest getRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("Solicitacoes", response);
                        try {
                            //Type collectionType = new TypeToken<Collection<Denuncia>>(){}.getType();
                            //Collection<Denuncia> denuncias = configuration.gson.fromJson(response, collectionType);
                            MinhasDenunciasDTO denuncias = Configuration.gson.fromJson(response, MinhasDenunciasDTO.class);
                            final ArrayList<Denuncia> minhasDenuncias = denuncias.getMinhasDenuncias();
                            if (minhasDenuncias == null) {
                                Log.i("Error", "Usuário nao possui denuncias!");
                                Toast toast = Toast.makeText(getActivity().getApplicationContext(), "Usuário nao possui denuncias!", Toast.LENGTH_LONG);
                                toast.show();
                            } else {
                                //ArrayList<Denuncia> minhasDenuncias = denuncias.getMinhasDenuncias();
                                LinearLayout layout = (LinearLayout) getActivity().findViewById(R.id.layoutMinhasDenuncias);
                                for(int i = 0; i<minhasDenuncias.size();i++)
                                {
                                    CardView card = new CardView(getActivity().getApplicationContext());
                                    card.setId(i);
                                    CardView.LayoutParams layoutParams = new CardView.LayoutParams(CardView.LayoutParams.MATCH_PARENT,
                                            CardView.LayoutParams.WRAP_CONTENT);
                                    card.setLayoutParams(layoutParams);
                                    //card.setBackgroundColor(Color.WHITE);
                                    card.setMaxCardElevation(15);
                                    //card.setUseCompatPadding(true);
                                    ImageView image = new ImageView(getActivity().getApplicationContext());
                                    image.setId(i + 42);
                                    image.setLayoutParams(new android.view.ViewGroup.LayoutParams(260, 260));

                                    image.setScaleType(FIT_XY);


                                    File arquivo = new File(minhasDenuncias.get(i).getFotos().get(0));
                                    if (!arquivo.exists()) {
                                        String pathAbsoluto = arquivo.getAbsolutePath();
                                        int pos = pathAbsoluto.lastIndexOf("/");
                                        String pathDir = pathAbsoluto.substring(0, pos + 1);
                                        File directory = new File(pathDir);
                                        if (!directory.exists()) {
                                            directory.mkdir();
                                        }
                                        String url = "http://191.252.0.77/public/denounce_image/" + minhasDenuncias.get(i).getId() + "_0.png";
                                        new DownloadFilesTask().execute(url, ("" + image.getId()), arquivo.getAbsolutePath());

                                    } else {
                                        Bitmap myBitmapGrande = BitmapFactory.decodeFile(arquivo.getPath());
                                        Bitmap myBitmap = Bitmap.
                                                createScaledBitmap(myBitmapGrande, myBitmapGrande.getWidth() / 6, myBitmapGrande.getHeight() / 6, false);
                                        image.setImageBitmap(myBitmap);
                                    }

                                    // Adds the view to the layout
                                    card.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            Log.d("Id denuncia", "" + v.getId());
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
                                    textoCartao.setTextSize(16);
                                    textoCartao.setId((image.getId() + 35));
                                    textoCartao.setTextColor(Color.DKGRAY);

                                    RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
                                            RelativeLayout.LayoutParams.WRAP_CONTENT,
                                            RelativeLayout.LayoutParams.WRAP_CONTENT);
                                    lp.addRule(RelativeLayout.RIGHT_OF, image.getId());
                                    lp.setMargins(20, 10, 0, 0);

                                    relative.addView(textoCartao, lp);

                                    TextView status = new TextView(getActivity().getApplicationContext());
                                    status.setText("Status: ");
                                    status.setTextSize(12);
                                    status.setId((textoCartao.getId() + 35));
                                    status.setTextColor(Color.DKGRAY);

                                    RelativeLayout.LayoutParams lp2 = new RelativeLayout.LayoutParams(
                                            RelativeLayout.LayoutParams.WRAP_CONTENT,
                                            RelativeLayout.LayoutParams.WRAP_CONTENT);
                                    lp2.addRule(RelativeLayout.RIGHT_OF, image.getId());
                                    lp2.addRule(RelativeLayout.BELOW, textoCartao.getId());
                                    lp2.setMargins(20, 20, 0, 0);

                                    relative.addView(status, lp2);

                                    TextView textoStatus = new TextView(getActivity().getApplicationContext());
                                    switch (minhasDenuncias.get(i).getStatus()) {
                                        case 0:
                                            textoStatus.setText("Pendente");
                                            textoStatus.setTextColor(Color.LTGRAY);
                                            break;
                                        case 1:
                                            textoStatus.setText("Em andamento");
                                            textoStatus.setTextColor(ContextCompat.getColor(getActivity().getApplicationContext(), R.color.blue));
                                            break;
                                        case 2:
                                            textoStatus.setText("Finalizado com sucesso");
                                            textoStatus.setTextColor(ContextCompat.getColor(getActivity().getApplicationContext(), R.color.light_green));
                                            break;
                                        case 3:
                                            textoStatus.setText("Recusado");
                                            textoStatus.setTextColor(Color.RED);
                                            break;
                                    }
                                    textoStatus.setTextSize(12);

                                    RelativeLayout.LayoutParams lp3 = new RelativeLayout.LayoutParams(
                                            RelativeLayout.LayoutParams.WRAP_CONTENT,
                                            RelativeLayout.LayoutParams.WRAP_CONTENT);
                                    lp3.addRule(RelativeLayout.RIGHT_OF, status.getId());
                                    lp3.addRule(RelativeLayout.BELOW, textoCartao.getId());
                                    lp3.setMargins(0, 20, 0, 0);

                                    relative.addView(textoStatus, lp3);

                                    card.addView(relative);
                                    card.setBackgroundColor(Color.WHITE);
                                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                                            LinearLayout.LayoutParams.MATCH_PARENT,
                                            LinearLayout.LayoutParams.WRAP_CONTENT);
                                    params.setMargins(0, 0, 0, 20);
                                    card.setLayoutParams(params);
                                    layout.addView(card);
                                }
                            }
                            progress.dismiss();
                        } catch (Exception e) {
                            e.printStackTrace();
                            Toast toast = Toast.makeText(getActivity(), response, Toast.LENGTH_LONG);
                            toast.show();
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
        );
        queue.add(getRequest);
        return myView;
    }
    private class DownloadFilesTask extends AsyncTask<String, Void, Bitmap> {
        String idImage;
        String path;
        protected Bitmap doInBackground(String... strings) {
            String url = strings[0];
            idImage = strings[1];
            path = strings[2];
            try{
                URL urlToGet = new URL(url);
                Bitmap bitmap = BitmapFactory.decodeStream(urlToGet.openConnection().getInputStream());
                return bitmap;
            } catch (Exception e){
                return null;
            }
        }

        protected void onPostExecute(Bitmap result) {
            if(result!=null) {
                File arquivo = new File(path);
                FileOutputStream out;
                try {
                    Log.d("Imagem path", arquivo.getPath());
                    out = new FileOutputStream(arquivo);
                    result.compress(Bitmap.CompressFormat.JPEG, 100, out); // bmp is your Bitmap instance
                    out.flush();
                    out.close();
                } catch (FileNotFoundException e) {
                    Log.d("Imagem", "Erro File not found");
                    e.printStackTrace();
                } catch (IOException e) {
                    Log.d("Imagem", "Erro IO");
                    e.printStackTrace();
                }
                int id = Integer.parseInt(idImage);
                ImageView imageToUpdate = (ImageView) getActivity().findViewById(id);
                imageToUpdate.setImageBitmap(Bitmap.createScaledBitmap(result, result.getWidth() / 5, result.getHeight() / 5, false));
            }
        }
    }
}
