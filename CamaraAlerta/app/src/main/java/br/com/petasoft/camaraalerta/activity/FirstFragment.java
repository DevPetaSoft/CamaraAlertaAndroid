package br.com.petasoft.camaraalerta.activity;

import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import org.w3c.dom.Text;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import br.com.petasoft.camaraalerta.R;
import model.Configuration;

/**
 * Created by Gustavo on 07/09/16.
 */
public class FirstFragment extends Fragment implements View.OnClickListener{
    View myView;
    private Configuration configuration;
    private TextView numeroDenuncias;
    private TextView numeroDenunciasResolvidas;
    private TextView textoSolicitacoes;
    private TextView textoResolvidas;

    public void atualizarDenuncias(int numero){
        numeroDenuncias.setText(""+numero);
        if(numero == 1){
            textoSolicitacoes.setText("Solicitação");
        }
    }
    public void atualizarDenunciasResolvidas(int numero) {
        numeroDenunciasResolvidas.setText(""+numero);
        if(numero == 1){
            textoResolvidas.setText("Resolvida");
        }
    }

    @Override
    public void onClick(View v){
        FragmentManager fragmentManager = getFragmentManager();
        switch(v.getId()){
            case R.id.numeroDenuncias:
                fragmentManager.beginTransaction()
                        .replace(R.id.content_frame
                                , new MinhasDenuncias())
                        .commit();
                break;
            case R.id.numeroDenunciasResolvidas:
                fragmentManager.beginTransaction()
                        .replace(R.id.content_frame
                                , new MinhasDenuncias())
                        .commit();
                break;
            case R.id.textoSolicitacoes:
                fragmentManager.beginTransaction()
                        .replace(R.id.content_frame
                                , new MinhasDenuncias())
                        .commit();
                break;
            case R.id.textoResolvidas:
                fragmentManager.beginTransaction()
                        .replace(R.id.content_frame
                                , new MinhasDenuncias())
                        .commit();
                break;
        }
        ((MainActivity)getActivity()).mudarNavegacao(3);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        myView = inflater.inflate(R.layout.first_layout,container,false);
        numeroDenuncias = (TextView) myView.findViewById(R.id.numeroDenuncias);
        numeroDenunciasResolvidas = (TextView) myView.findViewById(R.id.numeroDenunciasResolvidas);
        textoSolicitacoes = (TextView) myView.findViewById(R.id.textoSolicitacoes);
        textoResolvidas = (TextView) myView.findViewById(R.id.textoResolvidas);


        numeroDenuncias.setOnClickListener(this);
        numeroDenunciasResolvidas.setOnClickListener(this);
        textoSolicitacoes.setOnClickListener(this);
        textoResolvidas.setOnClickListener(this);

        atualizarNumeroDenuncias();
        return myView;
    }

    public void atualizarNumeroDenuncias(){
        RequestQueue queue = Volley.newRequestQueue(getActivity());
        String url = configuration.base_url + "user/numeroDenuncias/"+Configuration.usuario.getId();
        StringRequest getRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("Solicitacoes", response);
                        int[] numeroDenuncias = Configuration.gson.fromJson(response, int[].class);
                        atualizarDenuncias(numeroDenuncias[0]);
                        atualizarDenunciasResolvidas(numeroDenuncias[1]);
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
                    }
                }
        );
        queue.add(getRequest);
    }
}
