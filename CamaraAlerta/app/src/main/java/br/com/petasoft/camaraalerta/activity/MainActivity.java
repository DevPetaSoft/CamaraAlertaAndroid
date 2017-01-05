package br.com.petasoft.camaraalerta.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import android.app.FragmentManager;
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

import java.util.HashMap;
import java.util.Map;

import br.com.petasoft.camaraalerta.R;
import model.Cidadao;
import model.Configuration;

import static android.R.attr.filter;
import static android.R.attr.start;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private Configuration configuration;
    private SmoothActionBarDrawerToggle mDrawerToggle;

    public TextView nomeTextView;
    public TextView emailTextView;


    private class SmoothActionBarDrawerToggle extends ActionBarDrawerToggle {

        private Runnable runnable;

        public SmoothActionBarDrawerToggle(Activity activity, DrawerLayout drawerLayout, Toolbar toolbar, int openDrawerContentDescRes, int closeDrawerContentDescRes) {
            super(activity, drawerLayout, toolbar, openDrawerContentDescRes, closeDrawerContentDescRes);
        }

        @Override
        public void onDrawerOpened(View drawerView) {
            super.onDrawerOpened(drawerView);
            invalidateOptionsMenu();
        }
        @Override
        public void onDrawerClosed(View view) {
            super.onDrawerClosed(view);
            invalidateOptionsMenu();
        }
        @Override
        public void onDrawerStateChanged(int newState) {
            super.onDrawerStateChanged(newState);
            if (runnable != null && newState == DrawerLayout.STATE_IDLE) {
                runnable.run();
                runnable = null;
            }
        }

        public void runWhenIdle(Runnable runnable) {
            this.runnable = runnable;
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //


        //

        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.content_frame
                        , new FirstFragment())
                .commit();
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);



        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        mDrawerToggle = new SmoothActionBarDrawerToggle(this, drawer, toolbar, R.string.open, R.string.close);
        drawer.setDrawerListener(mDrawerToggle);
        navigationView.getMenu().getItem(0).setChecked(true);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else if (this.isTaskRoot()) {
            new AlertDialog.Builder(this)
                    .setTitle("Fechar a aplicação")
                    .setMessage("Tem certeza que deseja fechar o CamaraAlerta?")
                    .setPositiveButton("Sim", new DialogInterface.OnClickListener()
                    {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                        }

                    })
                    .setNegativeButton("Não", null)
                    .show();
        } else {
            super.onBackPressed();
        }
    }

    /*Atualizar numero de denuncias apos fim da denuncia
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d("Dentro", "entrou");
        if (requestCode == 1) {

            if(resultCode == RESULT_OK){
                FirstFragment fragment = (FirstFragment) getFragmentManager().findFragmentById(R.id.first_layout);
                fragment.atualizarNumeroDenuncias();
            }
            if (resultCode == RESULT_CANCELED) {
                //Do nothing?
            }
        }
    }//onActivityResult
    */

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        // Troca o nome do menu lateral pelo nome da conta que foi logada
        TextView navUserName = (TextView) findViewById(R.id.nav_user_name_text);
        TextView navUserEmail = (TextView) findViewById(R.id.nav_user_name_email);
        if(configuration.usuario != null) {
            navUserName.setText(configuration.usuario.getNome());
            navUserEmail.setText(configuration.usuario.getEmail());
        }

        return true;
    }

    /* Menu de opções retirado
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    */

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        final FragmentManager fragmentManager = getFragmentManager();
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        //drawer.closeDrawer(GravityCompat.START);

        if (id == R.id.nav_first_layout) {
            mDrawerToggle.runWhenIdle(new Runnable(){
                @Override
                public void run(){
                    fragmentManager.beginTransaction()
                            .replace(R.id.content_frame
                                    , new FirstFragment())
                            .commit();
                }
            });
            drawer.closeDrawers();
        } else if (id == R.id.nav_second_layout) {
            mDrawerToggle.runWhenIdle(new Runnable(){
                @Override
                public void run(){
                    Intent intent = new Intent(MainActivity.this, NovaDenuncia.class);
                    startActivity(intent);
                }
            });
            drawer.closeDrawers();
        } else if (id == R.id.nav_third_layout) {
            mDrawerToggle.runWhenIdle(new Runnable(){
                @Override
                public void run(){
                    Intent intent = new Intent(MainActivity.this, MapsActivity.class);
                    startActivity(intent);
                }
            });
            drawer.closeDrawers();
        } else if (id == R.id.nav_fourth_layout){
            mDrawerToggle.runWhenIdle(new Runnable(){
                @Override
                public void run(){
                    fragmentManager.beginTransaction()
                            .replace(R.id.content_frame
                                    , new MinhasDenuncias())
                            .commit();
                }
            });
            drawer.closeDrawers();
        }
        return true;

    }

    public void novaDenuncia(View v){
        /*/*Atualizar numero de denuncias apos fim da denuncia
        Intent i = new Intent(this, NovaDenuncia.class);
        startActivityForResult(i, 1);
        */
        Intent intent = new Intent(this, NovaDenuncia.class);
        startActivity(intent);
    }

    public void mapaDenuncias(View v){
        Intent intent =  new Intent(this,MapsActivity.class);
        startActivity(intent);
    }

}
