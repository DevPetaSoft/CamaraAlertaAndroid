package br.com.petasoft.camaraalerta.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import br.com.petasoft.camaraalerta.R;
import dto.MinhasDenunciasDTO;
import model.Denuncia;
import model.Configuration;

import static android.widget.ImageView.ScaleType.CENTER_CROP;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private ProgressDialog progress;
    private MinhasDenunciasDTO denunciasDTO = null;
    private ArrayList<Marker> markers = new ArrayList<Marker>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);


        /**
         * Recebendo solicitações para colocar no mapa
         */
        progress=new ProgressDialog(this);
        progress.setMessage("Recebendo Solicitações");
        progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progress.setIndeterminate(true);
        progress.setProgress(0);
        progress.show();

        RequestQueue queue = Volley.newRequestQueue(this);
        String url = Configuration.base_url + "denuncia/minhasDenunciasUsu/"+Configuration.usuario.getId();
        StringRequest getRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("Solicitacoes", response);
                        try {
                            //Type collectionType = new TypeToken<Collection<Denuncia>>(){}.getType();
                            //Collection<Denuncia> denuncias = configuration.gson.fromJson(response, collectionType);
                            MinhasDenunciasDTO denuncias = Configuration.gson.fromJson(response, MinhasDenunciasDTO.class);
                            ArrayList<Denuncia> minhasDenuncias = denuncias.getMinhasDenuncias();
                            if (minhasDenuncias == null) {
                                Log.i("Error", "Usuário nao possui denuncias!");
                                Toast toast = Toast.makeText(getApplicationContext(), "Usuário nao possui denuncias!", Toast.LENGTH_LONG);
                                toast.show();
                            } else {
                                denunciasDTO = denuncias;
                                chamarMapa();
                            }
                        } catch (Exception e){
                            Toast toast = Toast.makeText(getApplicationContext(), response, Toast.LENGTH_LONG);
                            toast.show();
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

                                Toast toast = Toast.makeText(getApplicationContext(), json.getString("message"), Toast.LENGTH_LONG);
                                toast.show();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        } else {
                            Toast toast = Toast.makeText(getApplicationContext(), "Não foi possível se conectar com o servidor", Toast.LENGTH_LONG);
                            toast.show();
                        }
                        progress.dismiss();
                    }
                }
        );
        queue.add(getRequest);
    }

    private void chamarMapa(){
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        if(denunciasDTO.getNumeroDeDenuncias()>0){
            final ArrayList<Denuncia> minhasDenuncias = denunciasDTO.getMinhasDenuncias();
            Log.d("DenunciasMapa", "" + minhasDenuncias.size());
            for (int i = 0; i < minhasDenuncias.size(); i++) {
                Denuncia atual = minhasDenuncias.get(i);
                createMarker(atual.getCoordenadas().getLatitude(), atual.getCoordenadas().getLongitude(), "" + (i + 1) + "- " + atual.getTitulo(),
                        atual.getDescricao());
            }
            mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
                @Override
                public void onInfoWindowClick(Marker marker) {
                    String titulo[] = marker.getTitle().split("-");
                    int numeroDenuncia = Integer.parseInt(titulo[0]) - 1;
                    Intent intent = new Intent(MapsActivity.this, MostrarDenuncia.class);
                    intent.putExtra("Denuncia", denunciasDTO.getMinhasDenuncias().get(numeroDenuncia));
                    startActivity(intent);
                }
            });

            LatLngBounds.Builder builder = new LatLngBounds.Builder();
            for (Marker marker : markers) {
                builder.include(marker.getPosition());
            }
            LatLngBounds bounds = builder.build();
            int width = getResources().getDisplayMetrics().widthPixels;
            int height = getResources().getDisplayMetrics().heightPixels;
            int padding = (int) (width * 0.10); // offset das bordas do mapa


            CameraUpdate camUpd = CameraUpdateFactory.newLatLngBounds(bounds, width, height, padding);

            mMap.animateCamera(camUpd);
        }
        // Add a marker in Sydney and move the camera
        /*
        LatLng sydney = new LatLng(-34, 151);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
        */
    }

    protected void createMarker(double latitude, double longitude, String title, String snippet) {
        Log.d("AdicionandoMarker", title);
        markers.add(mMap.addMarker(new MarkerOptions()
                .position(new LatLng(latitude, longitude))
                .anchor(0.5f, 0.5f)
                .title(title)
                .snippet(snippet)));

    }

    public void closeMaps(View view){
        finish();
    }

}
