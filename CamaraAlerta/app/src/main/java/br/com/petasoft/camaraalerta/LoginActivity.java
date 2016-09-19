package br.com.petasoft.camaraalerta;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

/**
 * Created by Lucas on 9/18/2016.
 */
public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_login);
    }

    public void normalLogin(View view) {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    public void registrarConta(View view){
        Intent intent = new Intent(this, Registrar.class);
        startActivity(intent);
    }

    public void esqueciSenha(View view){
        Intent intent = new Intent(this, EsqueciSenha.class);
        startActivity(intent);
    }

}
