package br.com.petasoft.camaraalerta.activity;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import br.com.petasoft.camaraalerta.R;
import model.Configuration;
import model.Vereador;

public class SecondFrameDenuncia extends Fragment {
    View myView;
    private ProgressDialog progress;
    private List<Vereador> listaVereadores;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        myView = inflater.inflate(R.layout.fragment_second_frame_denuncia,container,false);
        carregarVereadores();
        Button previous = (Button) myView.findViewById(R.id.buttonPreviousDenuncia);
        previous.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {

                ((NovaDenuncia)getActivity()).anteriorFrame();
            }
        });

        Button next = (Button) myView.findViewById(R.id.buttonEnviarDenuncia);
        next.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {
                ((NovaDenuncia)getActivity()).enviarDenuncia();
            }
        });

        return myView;
    }

    private void atualizarSpinner(){
        if(listaVereadores.size()>0) {
            Spinner dropdown = (Spinner) myView.findViewById(R.id.vereadores_spinner);
            String[] items = new String[listaVereadores.size()+1];
            items[0] = "Selecione o vereador";
            for (int i = 0; i < listaVereadores.size(); i++) {
                items[i+1] = listaVereadores.get(i).getNome();
            }
            ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_dropdown_item, items);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            dropdown.setAdapter(adapter);
        } else {
            Toast toast = Toast.makeText(getActivity().getApplicationContext(), "Essa região não possui vereador cadastrado!", Toast.LENGTH_LONG);
            toast.show();
            getActivity().finish();
        }
    }

    public Vereador getVereadorEscolhido(){
        Spinner spinnerVereadores = (Spinner)myView.findViewById(R.id.vereadores_spinner);
        String text = spinnerVereadores.getSelectedItem().toString();
        Vereador vReturn = null;
        for(Vereador v : listaVereadores){
            if(v.getNome().equals(text))
                vReturn = v;
        }
        return vReturn;
    }

    private void carregarVereadores(){
        progress=new ProgressDialog(this.getActivity());
        progress.setMessage("Buscando vereadores");
        progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progress.setIndeterminate(true);
        progress.setProgress(0);
        progress.show();

        RequestQueue queue = Volley.newRequestQueue(this.getActivity());
        String url = Configuration.base_url + "vereador/listPorCidade";
        StringRequest getRequest = new StringRequest(Request.Method.GET,url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.i("Response",response.substring(0,30));
                        Type listType = new TypeToken<ArrayList<Vereador>>(){}.getType();

                        List<Vereador> vereadorList = new Gson().fromJson(response, listType);

                        Log.i("Numero de vereadores", String.valueOf(vereadorList.size()));
                        listaVereadores = vereadorList;
                        atualizarSpinner();
                        progress.dismiss();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.i("Response","error");
                        progress.dismiss();
                    }
                }
        );
        queue.add(getRequest);
    }

}