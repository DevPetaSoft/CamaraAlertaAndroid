package br.com.petasoft.camaraalerta.activity;

import android.Manifest;
import android.app.AlertDialog;
import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.BaseColumns;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.test.mock.MockPackageManager;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.VolleyError;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.lang.reflect.Type;
import java.nio.channels.FileChannel;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;


import br.com.petasoft.camaraalerta.R;

import dto.DenunciaDTO;
import model.Cidadao;
import model.Cidade;
import model.Coordenadas;
import model.Denuncia;
import model.Configuration;
import model.Vereador;

public class NovaDenuncia extends AppCompatActivity implements FirstFrameDenuncia.InterfaceFrame1, FirstFrameDenuncia.InterfaceFrame2 {

    static final int REQUEST_IMAGE_CAPTURE = 1;
    private ArrayList<String> listaPaths;
    private ProgressDialog progress;
    private Configuration configuration;
    private boolean flagLocalizacao = false;
    private Button botaoVerFotos;
    private boolean vendoFotos = false;
    private boolean solicitacaoSent = false;


    //Variaveis relacionadas a localização ---------------------------------------------------------
    String gpsPermission = Manifest.permission.ACCESS_FINE_LOCATION;
    String gpsPermission2 = Manifest.permission.ACCESS_COARSE_LOCATION;
    private Context context;
    // GPSTracker class
    GPSTracker gps;
    private Double latitude = null;
    private Double longitude = null;



    private ProviderLocationTracker locationTracker = null;
    //----------------------------------------------------------------------------------------------

    String write_external_storagePermission = Manifest.permission.WRITE_EXTERNAL_STORAGE;

    String cameraPermission = Manifest.permission.CAMERA;

    String read_external_storagePermission = Manifest.permission.READ_EXTERNAL_STORAGE;

    private static final int MULTIPLE_REQUEST_PERMISSION = 123;

    View myView;

    FirstFrameDenuncia f1;

    SecondFrameDenuncia f2;

    private String titulo = "";
    //endereço virá aqui
    private String descricao = "";
    private Boolean anonima = false;

    private String fotosJuntas = "";
    Vereador vereador;

    private String dDTOString = "";
    private ArrayList<String> GalleryList = new ArrayList<String>();
    private File currentFile = null;
    private Uri currentUri = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        /*TODO: Deletar Fotos
        Configuration.fotosDeletadas = new ArrayList<String>();
        */
        super.onCreate(savedInstanceState);
        f1 = new FirstFrameDenuncia();
        f2 = new SecondFrameDenuncia();
        setContentView(R.layout.activity_nova_denuncia);
        context = this;
        listaPaths = new ArrayList<String>();
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        botaoVerFotos = (Button)findViewById(R.id.botaoVerFotos);
        //inserindo fragmento inicial
        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.denuncia_frame
                        , f1)
                .commit();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(listaPaths.size()<4) {
                    dispatchTakePictureIntent();
                } else {
                    Toast toast = Toast.makeText(NovaDenuncia.this, "Número máximo de fotos é 4!", Toast.LENGTH_LONG);
                    toast.show();
                }
            }
        });
        try {
            if (ActivityCompat.checkSelfPermission(this, gpsPermission) != PackageManager.PERMISSION_GRANTED ||
                    ActivityCompat.checkSelfPermission(this, gpsPermission2) != PackageManager.PERMISSION_GRANTED ||
                    ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
                    ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED ||
                    ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(this, new String[]{gpsPermission, gpsPermission2, write_external_storagePermission, cameraPermission, read_external_storagePermission},
                        MULTIPLE_REQUEST_PERMISSION);

                // If any permission above not allowed by user, this condition will
                //execute every time, else your else part will work
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        Log.d("Inicio", "novo");
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MULTIPLE_REQUEST_PERMISSION:{
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    /**
                     * FillPhotoList() fazendo travar a aplicação
                     */
                    /*
                    FillPhotoList();
                    */

                } else {
                    finish();
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
            }
        }
    }

    /**
     * Fazendo travar a aplicação
     */
    /*
    //Pegar fotos na galeria;
    private void FillPhotoList()
    {
        // initialize the list!
        GalleryList.clear();
        String[] projection = { MediaStore.Images.ImageColumns.DISPLAY_NAME };
        // intialize the Uri and the Cursor, and the current expected size.
        Cursor c = null;
        Uri u = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        //
        // Query the Uri to get the data path.  Only if the Uri is valid.
        if (u != null)
        {
            c = managedQuery(u, projection, null, null, null);
        }

        // If we found the cursor and found a record in it (we also have the id).
        if ((c != null) && (c.moveToFirst()))
        {
            do
            {
                // Loop each and add to the list.
                GalleryList.add(c.getString(0));
            }
            while (c.moveToNext());
        }
    }
    */

    //Caminho para ultima foto tirada
    String mCurrentPhotoPath;

    //Criando um arquivo unico para salvar a foto
    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        Log.w("myApp", "Caminho: " + storageDir.getAbsolutePath());

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();
        currentFile = image;
        currentUri = Uri.fromFile(currentFile);
        return image;
    }

    static final int REQUEST_TAKE_PHOTO = 1;


    //Tirar a foto
    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
                Log.w("myApp", "Erro criando arquivo");
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "com.example.android.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        }
    }

    //Receber resultado da foto
    //Utiliza o caminho da foto atual para criar a thumbnail
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {

            botaoVerFotos.setBackgroundResource(R.drawable.blue_button_background);
            botaoVerFotos.setTextColor(Color.WHITE);
            /**
             * Fazendo travar a aplicação
             */
            /*
            //Tratando foto duplicada na galera e deletando:


            // This is ##### ridiculous.  Some versions of Android save
            // to the MediaStore as well.  Not sure why!  We don't know what
            // name Android will give either, so we get to search for this
            // manually and remove it.
            String[] projection = {MediaStore.Images.ImageColumns.SIZE,
                    MediaStore.Images.ImageColumns.DISPLAY_NAME,
                    MediaStore.Images.ImageColumns.DATA,
                    BaseColumns._ID,};
            //
            // intialize the Uri and the Cursor, and the current expected size.
            Cursor c = null;
            Uri u = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
            //
            if (currentFile != null) {
                // Query the Uri to get the data path.  Only if the Uri is valid,
                // and we had a valid size to be searching for.
                if ((u != null) && (currentFile.length() > 0)) {
                    c = managedQuery(u, projection, null, null, null);
                }
                //
                // If we found the cursor and found a record in it (we also have the size).
                if ((c != null) && (c.moveToFirst())) {
                    do {
                        // Check each area in the gallary we built before.
                        boolean bFound = false;
                        for (String sGallery : GalleryList) {
                            if (sGallery.equalsIgnoreCase(c.getString(1))) {
                                bFound = true;
                                break;
                            }
                        }
                        //
                        // To here we looped the full gallery.
                        if (!bFound) {
                            // This is the NEW image.  If the size is bigger, copy it.
                            // Then delete it!
                            File f = new File(c.getString(2));

                            // Ensure it's there, check size, and delete!
                            if ((f.exists()) && (currentFile.length() < c.getLong(0)) && (currentFile.delete())) {
                                // Finally we can stop the copy.
                                try {
                                    currentFile.createNewFile();
                                    FileChannel source = null;
                                    FileChannel destination = null;
                                    try {
                                        source = new FileInputStream(f).getChannel();
                                        destination = new FileOutputStream(currentFile).getChannel();
                                        destination.transferFrom(source, 0, source.size());
                                    } finally {
                                        if (source != null) {
                                            source.close();
                                        }
                                        if (destination != null) {
                                            destination.close();
                                        }
                                    }
                                } catch (IOException e) {
                                    // Could not copy the file over.
                                    Toast.makeText(getApplicationContext(), "Erro ao criar arquivo", Toast.LENGTH_LONG).show();;
                                }
                            }
                            //
                            ContentResolver cr = getContentResolver();
                            cr.delete(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                                    BaseColumns._ID + "=" + c.getString(3), null);
                            break;
                        }
                    }
                    while (c.moveToNext());
                }
            }
            */
            //fim do tratamento de foto duplicada


            ImageView fotoDenuncia = (ImageView) findViewById(R.id.fotosDenuncia);

            Bitmap myBitmap = BitmapFactory.decodeFile(mCurrentPhotoPath);
            myBitmap = Bitmap.createScaledBitmap(myBitmap, myBitmap.getScaledWidth(100), myBitmap.getScaledHeight(100), false);

            //colocar o caminho dentro da ArrayList para a proxima activity
            listaPaths.add(mCurrentPhotoPath);

            fotoDenuncia.setImageBitmap(myBitmap);
            ThirdFrameDenuncia myFragment = (ThirdFrameDenuncia)getFragmentManager().findFragmentByTag("FOTO_FRAGMENT");
            if(myFragment != null && myFragment.isVisible()){
                String[] strings = new String[listaPaths.size()];
                strings = listaPaths.toArray(strings);

                Bundle b = new Bundle();
                b.putStringArray("fotos", strings);

                FragmentManager fragmentManager = getFragmentManager();
                ThirdFrameDenuncia f3 = new ThirdFrameDenuncia();
                f3.setArguments(b);
                fragmentManager.beginTransaction()
                        .replace(R.id.denuncia_frame
                                , f3, "FOTO_FRAGMENT")
                        .commit();
            }
            Log.i("Teste","teste");
            if (locationTracker==null) {
                locationTracker = new ProviderLocationTracker(NovaDenuncia.this, ProviderLocationTracker.ProviderType.GPS);
                locationTracker.start();
            }
            //Pegando localização-----------------------------------------------------------
            //if(flagLocalizacao == false) {
                if(locationTracker.hasLocation()){
                    Location location = locationTracker.getLocation();
                    latitude = location.getLatitude();
                    longitude = location.getLongitude();
                }else {


                    gps = new GPSTracker(NovaDenuncia.this);

                    // check if GPS enabled
                    if (gps.canGetLocation()) {

                        latitude = gps.getLatitude();
                        longitude = gps.getLongitude();

                        // Mostrar localização no log
                        Log.d("Your Location is - ", "Lat: " + latitude + "\nLong: " + longitude);
                    } else {
                        // can't get location
                        // GPS or Network is not enabled
                        // Ask user to enable GPS/network in settings
                        gps.showSettingsAlert();
                    }
                }

                //flagLocalizacao = true;
            //}
            //------------------------------------------------------------------------------

        }
    }

    public void proximoFrame() {
        FragmentManager fragmentManager = getFragmentManager();
        if (titulo.equals("")) {
            Toast.makeText(getApplicationContext(), "Você precisa incluir um assunto.", Toast.LENGTH_LONG).show();
            return;
        } else if (descricao.equals("")) {
            Toast.makeText(getApplicationContext(), "Você precisa incluir uma descrição.", Toast.LENGTH_LONG).show();
            return;
        }

        fragmentManager.beginTransaction()
                .replace(R.id.denuncia_frame
                        , f2)
                .commit();

    }

    public void anteriorFrame() {
        FragmentManager fragmentManager = getFragmentManager();

        fragmentManager.beginTransaction()
                .replace(R.id.denuncia_frame
                        , f1)
                .commit();

    }

    public void returnFotos() {
        FragmentManager fragmentManager = getFragmentManager();

        fragmentManager.beginTransaction()
                .replace(R.id.denuncia_frame
                        , f1)
                .commit();

    }

    /*
    public void onCheckboxClicked(View view) {
        // Is the view now checked?
        boolean checked = ((CheckBox) view).isChecked();

        // Check which checkbox was clicked
        switch (view.getId()) {
            case R.id.checkbox_denuncia_anonima:
                if (checked) {
                    anonima = true;
                } else {
                    anonima = false;
                }
                break;
        }
    }
    */

    public void listaDeFotos(View v) {
        if (!listaPaths.isEmpty()) {
            if(!vendoFotos) {
                String[] strings = new String[listaPaths.size()];
                strings = listaPaths.toArray(strings);

                Bundle b = new Bundle();
                b.putStringArray("fotos", strings);

                /*
                Intent i = new Intent(NovaDenuncia.this, FotosTiradasActivity.class);
                i.putExtras(b);
                startActivity(i);
                */
                FragmentManager fragmentManager = getFragmentManager();
                ThirdFrameDenuncia f3 = new ThirdFrameDenuncia();
                f3.setArguments(b);
                fragmentManager.beginTransaction()
                        .replace(R.id.denuncia_frame
                                , f3, "FOTO_FRAGMENT")
                        .commit();
                vendoFotos=true;
                botaoVerFotos.setText("VOLTAR");
            } else {
                returnFotos();
                vendoFotos=false;
                botaoVerFotos.setText("VIZUALIZAR FOTOS");
            }
        }
    }

    /**
     * Carrega uma lista de vereadores por cidade
     * TODO: passar a cidade como parametro
     * @return
     */

    @Override
    public void onFragment1EditTextChanged(String s) {
        titulo = s;
    }

    @Override
    public void onFragment2EditTextChanged(String s) {
        descricao = s;
    }

    @Override
    public void onBackPressed() {
        Log.d("Valores", "" + titulo.length() + descricao.length() + listaPaths.size());
        if(titulo.length()!=0 || descricao.length()!=0 || listaPaths.size()!= 0){
            new AlertDialog.Builder(this)
                    .setMessage("Deseja descartar as modificações feitas?")
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

    public void enviarDenuncia() {
        boolean stuckLoading = false;

        progress = new ProgressDialog(this);
        progress.setMessage("Enviando Solicitação");
        progress.setCancelable(false);
        progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progress.setIndeterminate(true);
        progress.setProgress(0);
        progress.show();
        if (locationTracker.hasLocation()) {
            Location location = locationTracker.getLocation();
            latitude = location.getLatitude();
            longitude = location.getLongitude();
        }


        String lat = "" + latitude;
        Log.d("Loc Lat", lat);
        String lon = "" + longitude;
        Log.d("Loc Lon", lon);

        Log.i("re", "ee");

        /*TODO: Deletar Fotos
        for(String path : Configuration.fotosDeletadas){
            for(int i = 0; i<listaPaths.size(); i++){
                if(path.equals(listaPaths.get(i))){
                    listaPaths.remove(i);
                }
            }
        }
        */
        if (longitude == null || latitude == null) {
            Toast.makeText(getApplicationContext(), "A sua localização não pode ser definida, clique Enviar para tentar outra vez", Toast.LENGTH_LONG).show();
            stuckLoading = true;

            gps = new GPSTracker(NovaDenuncia.this);
                // check if GPS enabled
                if (gps.canGetLocation()) {

                    latitude = gps.getLatitude();
                    longitude = gps.getLongitude();

                    // Mostrar localização no log
                    Log.d("Your Location is - ", "Lat: " + latitude + "\nLong: " + longitude);
                } else {
                    // can't get location
                    // GPS or Network is not enabled
                    // Ask user to enable GPS/network in settings
                    gps.showSettingsAlert();
                }

        } else if (listaPaths.isEmpty()) {
            Toast.makeText(getApplicationContext(), "Você precisa incluir pelo menos uma foto.", Toast.LENGTH_LONG).show();
            stuckLoading = true;
        } else {
            locationTracker.stop();
            final Thread t = new Thread() {
                @Override
                public void run() {
                    Coordenadas c = new Coordenadas();
                    c.setLatitude(latitude);
                    c.setLongitude(longitude);

                    Denuncia denuncia = new Denuncia();
                    denuncia.setNovo(true);
                    denuncia.setTitulo(titulo);
                    denuncia.setDescricao(descricao);

                    //  denuncia.setData(null);
                    if (denuncia.getData() != null) {
                        Log.i("DATA", denuncia.getData().toString());
                    } else {
                        Log.i("Data", "error");
                    }
                    denuncia.setAnonima(anonima);
                    Cidadao cidadao = new Cidadao();
                    cidadao.setId(Configuration.usuario.getId());
                    denuncia.setCidadao(cidadao);
                    denuncia.setFotos(listaPaths);
                    Vereador v = new Vereador();
                    v.setId(f2.getVereadorEscolhido().getId());
                    denuncia.setVereador(v);
                    denuncia.setCoordenadas(c);

                    List<String> listaDeFotos = new ArrayList<String>();
                    for (String s : listaPaths) {
                        try {
                            ByteArrayOutputStream baos = new ByteArrayOutputStream(3000);
                            Bitmap src = BitmapFactory.decodeFile(s);
                            src = Bitmap.createScaledBitmap(src, src.getScaledWidth(100), src.getScaledHeight(100), false);
                            src.compress(Bitmap.CompressFormat.PNG, 0, baos);
                            baos.flush();
                            byte[] bytes = baos.toByteArray();
                            baos.close();
                            listaDeFotos.add(Base64.encodeToString(bytes, Base64.NO_WRAP));

                            //RequesString
                            fotosJuntas += Base64.encodeToString(bytes, Base64.NO_WRAP) + ",";
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    DenunciaDTO dDTO = new DenunciaDTO(denuncia, listaDeFotos);


                    Gson gson = new Gson();
                    final String myJSONString = gson.toJson(dDTO);

                    //Log.d("Gson ", myJSONString);

                    final RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
                    String url = Configuration.base_url + "denuncia/novaDenuncia";


                    final StringRequest strRequest = new StringRequest(Request.Method.POST, url,
                            new Response.Listener<String>() {
                                @Override
                                public void onResponse(String response) {
                                    Toast.makeText(getApplicationContext(), "Nova Solicitação criada com sucesso!", Toast.LENGTH_LONG).show();
                                    progress.dismiss();
                                        /*Atualizar numero de denuncias apos fim da denuncia
                                        Intent returnIntent = new Intent();
                                        setResult(RESULT_OK, returnIntent);
                                        */
                                    finish();
                                }
                            },
                            new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    Toast.makeText(getApplicationContext(), "Erro na criaçao de nova Solicitação", Toast.LENGTH_LONG).show();
                                    progress.dismiss();
                                }
                            }) {
                        @Override
                        protected Map<String, String> getParams() {
                            Map<String, String> params = new HashMap<String, String>();
                            params.put("dDTOString", myJSONString);
                            return params;
                        }
                    };
                    queue.add(strRequest);


                    //VolleySingleton.getInstance(this).addToRequestQueue(strRequest);

                }
            };
            t.start();
        }
        if (stuckLoading == true)
            progress.dismiss();
    }


}
