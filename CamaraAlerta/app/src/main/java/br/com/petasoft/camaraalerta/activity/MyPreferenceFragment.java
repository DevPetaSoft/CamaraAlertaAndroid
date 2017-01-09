package br.com.petasoft.camaraalerta.activity;

/**
 * Created by Lucas on 1/8/2017.
 */

import android.os.Bundle;
import android.preference.PreferenceFragment;

import br.com.petasoft.camaraalerta.R;

public class MyPreferenceFragment extends PreferenceFragment
{
    @Override
    public void onCreate(final Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
    }
}
