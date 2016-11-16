package br.com.petasoft.camaraalerta.activity;

import android.app.Fragment;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import br.com.petasoft.camaraalerta.R;

public class SecondFrameDenuncia extends Fragment {
    View myView;
    InterfaceFrame2 listener = null;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        myView = inflater.inflate(R.layout.fragment_second_frame_denuncia,container,false);

        listener = (InterfaceFrame2) getActivity();

        Button next = (Button) myView.findViewById(R.id.buttonEnviarDenuncia);
        next.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {
                EditText editText = (EditText) myView.findViewById(R.id.descricao);
                String str = editText.getText().toString();
                listener.onFragment2EditTextChanged(str);
                ((NovaDenuncia)getActivity()).enviarDenuncia();
            }
        });

        return myView;
    }

    public interface InterfaceFrame2 {
        public void onFragment2EditTextChanged(String string);
    }
}