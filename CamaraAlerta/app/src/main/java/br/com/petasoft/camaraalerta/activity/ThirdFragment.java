package br.com.petasoft.camaraalerta.activity;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import br.com.petasoft.camaraalerta.R;

/**
 * Created by Gustavo on 07/09/16.
 */
public class ThirdFragment extends Fragment {
    View myView;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        myView = inflater.inflate(R.layout.third_layout,container,false);
        return myView;
    }
}
