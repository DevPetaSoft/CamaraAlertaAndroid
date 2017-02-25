package br.com.petasoft.camaraalerta.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
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

import com.facebook.login.LoginManager;

import br.com.petasoft.camaraalerta.R;
import model.Configuration;

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
                        , new FirstFragment(), "FRAME_INICIAL")
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
            if(Configuration.loginNormal) {
                new AlertDialog.Builder(this)
                        .setTitle("Fechar a aplicação")
                        .setMessage("Tem certeza que deseja fechar o CamaraAlerta?")
                        .setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                finish();
                            }

                        })
                        .setNegativeButton("Não", null)
                        .show();
            } else if (Configuration.loginFacebook){
                new AlertDialog.Builder(this)
                        .setTitle("Fechar a aplicação")
                        .setMessage("Tem certeza que deseja fechar o CamaraAlerta?")
                        .setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                finish();
                            }

                        })
                        .setNegativeButton("Não", null)
                        .show();
            } else if (Configuration.loginGoogle){

            }
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

    @Override
    public void onResume(){
        super.onResume();
        FirstFragment myFragment = (FirstFragment) getFragmentManager().findFragmentByTag("FRAME_INICIAL");
        if(myFragment != null && myFragment.isVisible()) {
            Log.d("Carregando", "de novo");
            FragmentManager fragmentManager = getFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.content_frame
                            , new FirstFragment(), "FRAME_INICIAL")
                    .commit();
            NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
            navigationView.getMenu().getItem(0).setChecked(true);
        }
        MinhasDenuncias fragmentMinhas = (MinhasDenuncias) getFragmentManager().findFragmentByTag("FRAME_MINHAS");
        if(fragmentMinhas != null && fragmentMinhas.isVisible()){
            Bundle bundle = new Bundle();
            bundle.putInt("statusFiltro", 4);
            MinhasDenuncias fragMinhasDenuncias = new MinhasDenuncias();
            fragMinhasDenuncias.setArguments(bundle);
            FragmentManager fragmentManager2 = getFragmentManager();
            fragmentManager2.beginTransaction()
                    .replace(R.id.content_frame
                            , fragMinhasDenuncias, "FRAME_MINHAS")
                    .commit();
            NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
            navigationView.getMenu().getItem(3).setChecked(true);
        }
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
        final DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        //drawer.closeDrawer(GravityCompat.START);

        if (id == R.id.nav_first_layout) {
            mDrawerToggle.runWhenIdle(new Runnable(){
                @Override
                public void run(){
                    fragmentManager.beginTransaction()
                            .replace(R.id.content_frame
                                    , new FirstFragment(), "FRAME_INICIAL")
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
                    Bundle bundle = new Bundle();
                    bundle.putInt("statusFiltro", 4);
                    MinhasDenuncias fragMinhasDenuncias = new MinhasDenuncias();
                    fragMinhasDenuncias.setArguments(bundle);
                    fragmentManager.beginTransaction()
                            .replace(R.id.content_frame
                                    , fragMinhasDenuncias, "FRAME_MINHAS")
                            .commit();
                }
            });
            drawer.closeDrawers();
        }/*else if (id == R.id.nav_fifth_layout) {
            mDrawerToggle.runWhenIdle(new Runnable() {
                @Override
                public void run() {
                    fragmentManager.beginTransaction()
                            .replace(R.id.content_frame
                                    , new MyPreferenceFragment(), "FRAME_PREFE")
                            .commit();
                }
            });
            drawer.closeDrawers();
        }*/else if (id == R.id.nav_seventh_layout){
            if(Configuration.loginNormal) {
                new AlertDialog.Builder(this)
                        .setTitle("Fazer Logout")
                        .setMessage("Tem certeza que deseja fazer Logout?")
                        .setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                SharedPreferences.Editor editor = getApplication().getApplicationContext().getSharedPreferences(LoginActivity.MY_PREFS_NAME, MODE_PRIVATE).edit();
                                editor.putString("nlEmail", "");
                                editor.putString("nlPass", "");
                                editor.putInt("nlFeito", 0); //1- login feito e nao teve logout , 0 - logout
                                boolean voltou = editor.commit();
                                if(voltou){
                                    Log.d("Normal", "deu commit");
                                } else {
                                    Log.d("Normal", "nao deu commit");
                                }
                                Intent intent = new Intent(MainActivity.this , LoginActivity.class);
                                startActivity(intent);
                                finish();
                            }

                        })
                        .setNegativeButton("Não", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                drawer.closeDrawers();
                                marcarMenuCerto();
                            }
                        })
                        .show();
            } else if(Configuration.loginFacebook){
                new AlertDialog.Builder(this)
                        .setTitle("Fazer Logout")
                        .setMessage("Tem certeza que deseja fazer Logout?")
                        .setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                LoginManager.getInstance().logOut();
                                SharedPreferences.Editor editor = getApplication().getApplicationContext().getSharedPreferences(LoginActivity.MY_PREFS_NAME, MODE_PRIVATE).edit();
                                editor.putString("fbName", "");
                                editor.putString("fbEmail", "");
                                boolean voltou = editor.commit();
                                if(voltou){
                                    Log.d("Facebook", "deu commit logout");
                                } else {
                                    Log.d("Facebook", "nao deu  logout");
                                }
                                Intent intent = new Intent(MainActivity.this , LoginActivity.class);
                                startActivity(intent);
                                finish();
                            }

                        })
                        .setNegativeButton("Não", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                drawer.closeDrawers();
                                marcarMenuCerto();
                            }
                        })
                        .show();
            }
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

    public void mudarNavegacao(int numero){
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.getMenu().getItem(numero).setChecked(true);
    }

    public void marcarMenuCerto(){
        FirstFragment myFragment = (FirstFragment) getFragmentManager().findFragmentByTag("FRAME_INICIAL");
        MinhasDenuncias minhasFrame = (MinhasDenuncias) getFragmentManager().findFragmentByTag("FRAME_MINHAS");
        MyPreferenceFragment prefeFrame = (MyPreferenceFragment) getFragmentManager().findFragmentByTag("FRAME_PREFE");
        int qual = 0;
        if(myFragment != null && myFragment.isVisible()) {
            qual = 0;
        } else if(minhasFrame != null && minhasFrame.isVisible()) {
            qual = 3;
        } else if(prefeFrame != null && prefeFrame.isVisible()) {
            qual = 4;
        }
        mudarNavegacao(qual);
    }

}
