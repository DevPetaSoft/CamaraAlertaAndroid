package br.com.petasoft.camaraalerta.activity;

import android.app.Fragment;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import br.com.petasoft.camaraalerta.R;

public class FirstFrameDenuncia extends Fragment {
    View myView;
    private Bundle savedState = null;
    private InterfaceFrame1 listener = null;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        myView = inflater.inflate(R.layout.fragment_first_frame_denuncia,container,false);

        Spinner dropdown = (Spinner)myView.findViewById(R.id.vereadores_spinner);
        String[] items = new String[]{"Vereador 1", "Vereador 2", "Vereador 3"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_dropdown_item, items);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        dropdown.setAdapter(adapter);

        listener = (InterfaceFrame1) getActivity();

        Button next = (Button) myView.findViewById(R.id.buttonNextDenuncia);
        next.setOnClickListener(new View.OnClickListener(){
           public void onClick(View v) {
               EditText editText = (EditText) myView.findViewById(R.id.editTitulo);
               String str = editText.getText().toString();
               listener.onFragment1EditTextChanged(str);
               ((NovaDenuncia)getActivity()).proximoFrame();
           }
        });
        return myView;
    }


    public interface InterfaceFrame1 {
        public void onFragment1EditTextChanged(String string);
    }

    public void adicionarItensSpinner(){

    }
}