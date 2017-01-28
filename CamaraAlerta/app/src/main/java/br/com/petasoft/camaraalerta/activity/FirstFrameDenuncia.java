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
import model.Vereador;
import model.Configuration;

public class FirstFrameDenuncia extends Fragment {
    View myView;
    private Bundle savedState = null;
    private InterfaceFrame1 listener = null;

    private InterfaceFrame2 listener2 = null;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        myView = inflater.inflate(R.layout.fragment_first_frame_denuncia,container,false);

        listener = (InterfaceFrame1) getActivity();

        listener2 = (InterfaceFrame2) getActivity();

        Button next = (Button) myView.findViewById(R.id.buttonNextDenuncia);
        next.setOnClickListener(new View.OnClickListener(){
           public void onClick(View v) {
               EditText editText = (EditText) myView.findViewById(R.id.editTitulo);
               String str = editText.getText().toString();
               listener.onFragment1EditTextChanged(str);
               EditText editText2 = (EditText) myView.findViewById(R.id.descricao);
               String str2 = editText2.getText().toString();
               listener2.onFragment2EditTextChanged(str2);
               ((NovaDenuncia)getActivity()).proximoFrame();
           }
        });

        Button cancelar = (Button) myView.findViewById(R.id.buttonCancelarDenuncia);
        cancelar.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
            });
        return myView;
    }


    public interface InterfaceFrame1 {
        public void onFragment1EditTextChanged(String string);
    }

    public interface InterfaceFrame2 {
        public void onFragment2EditTextChanged(String string);
    }

    public void adicionarItensSpinner(){

    }
}