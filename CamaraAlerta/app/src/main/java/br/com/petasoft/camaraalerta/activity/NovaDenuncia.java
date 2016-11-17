package br.com.petasoft.camaraalerta.activity;

import android.app.FragmentManager;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
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
import com.google.gson.Gson;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


import br.com.petasoft.camaraalerta.R;

import dto.DenunciaDTO;
import model.Cidadao;
import model.Denuncia;
import model.Configuration;
import model.Vereador;

public class NovaDenuncia extends AppCompatActivity implements FirstFrameDenuncia.InterfaceFrame1, SecondFrameDenuncia.InterfaceFrame2 {

    static final int REQUEST_IMAGE_CAPTURE = 1;
    private List<String> listaPaths;

    FirstFrameDenuncia f1;

    SecondFrameDenuncia f2;

    private String titulo = "";
    //endereço virá aqui
    private String descricao = "";
    private Boolean anonima = false;

    private String fotosJuntas = "";
    Vereador vereador;

    private String dDTOString = "";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        f1 = new FirstFrameDenuncia();
        f2 = new SecondFrameDenuncia();
        setContentView(R.layout.activity_nova_denuncia);
        listaPaths = new ArrayList<String>();
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

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
               dispatchTakePictureIntent();
            }
        });

    }

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
            ImageView fotoDenuncia = (ImageView) findViewById(R.id.fotosDenuncia);

            Bitmap myBitmap = BitmapFactory.decodeFile(mCurrentPhotoPath);
            myBitmap = Bitmap.createScaledBitmap(myBitmap, myBitmap.getScaledWidth(100), myBitmap.getScaledHeight(100), false);

            //colocar o caminho dentro da ArrayList para a proxima activity
            listaPaths.add(mCurrentPhotoPath);

            fotoDenuncia.setImageBitmap(myBitmap);
        }
    }

    public void proximoFrame(){
        FragmentManager fragmentManager = getFragmentManager();

        fragmentManager.beginTransaction()
                .replace(R.id.denuncia_frame
                        , f2)
                .commit();

    }

    public void anteriorFrame(){
        FragmentManager fragmentManager = getFragmentManager();

        fragmentManager.beginTransaction()
                .replace(R.id.denuncia_frame
                        , f1)
                .commit();

    }

    public void onCheckboxClicked(View view) {
        // Is the view now checked?
        boolean checked = ((CheckBox) view).isChecked();

        // Check which checkbox was clicked
        switch(view.getId()) {
            case R.id.checkbox_denuncia_anonima:
                if (checked) {
                    anonima = true;
                }else {
                    anonima = false;
                }
                break;
        }
    }

    public void listaDeFotos(View v){
        if(!listaPaths.isEmpty()) {
            String[] strings = new String[listaPaths.size()];
            strings = listaPaths.toArray(strings);

            Bundle b = new Bundle();
            b.putStringArray("fotos", strings);
            Intent i = new Intent(NovaDenuncia.this, FotosTiradasActivity.class);
            i.putExtras(b);
            startActivity(i);
        }
    }
    @Override
    public void onFragment1EditTextChanged(String s){
        titulo = s;
    }
    @Override
    public void onFragment2EditTextChanged(String s){
        descricao = s;
    }



    public void enviarDenuncia(){


        if (titulo.equals("")){
            Toast.makeText(getApplicationContext(), "Você precisa incluir um título.", Toast.LENGTH_LONG).show();
        } else if (descricao.equals("")){
            Toast.makeText(getApplicationContext(), "Você precisa incluir uma descrição.", Toast.LENGTH_LONG).show();
        } else if(listaPaths.isEmpty()){
            Toast.makeText(getApplicationContext(), "Você precisa incluir pelo menos uma foto.", Toast.LENGTH_LONG).show();
        } else {
            Denuncia denuncia = new Denuncia();
            denuncia.setDescricao(descricao);
            denuncia.setData(new Date());
            denuncia.setAnonima(anonima);
            denuncia.setCidadao(Configuration.usuario);

            //por agora
            vereador = new Vereador();
            //
            denuncia.setVereador(vereador);

            List<String> listaDeFotos = new ArrayList<String>();

            for(String s : listaPaths){
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
                    fotosJuntas+= Base64.encodeToString(bytes, Base64.NO_WRAP) + ",";
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            DenunciaDTO dDTO = new DenunciaDTO(denuncia, listaDeFotos);


            //Log.d("dDTOString: Tamanho ", ""+dDTOString.length());

            Gson gson = new Gson();
            final String myJSONString = gson.toJson(dDTO);

            Log.d("Gson ", myJSONString);

            RequestQueue queue = Volley.newRequestQueue(this);
            String url = Configuration.base_url + "denuncia/novaDenuncia";


            StringRequest strRequest = new StringRequest(Request.Method.POST, url,
                    new Response.Listener<String>()
                    {
                        @Override
                        public void onResponse(String response)
                        {
                            Toast.makeText(getApplicationContext(), "Nova Solicitação criada com sucesso!", Toast.LENGTH_LONG).show();
                        }
                    },
                    new Response.ErrorListener()
                    {
                        @Override
                        public void onErrorResponse(VolleyError error)
                        {
                            Toast.makeText(getApplicationContext(), "Erro na criaçao de nova Solicitação", Toast.LENGTH_LONG).show();
                        }
                    })
            {
                @Override
                protected Map<String, String> getParams()
                {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("dDTOString", myJSONString);
                    return params;
                }
            };

            queue.add(strRequest);
            //VolleySingleton.getInstance(this).addToRequestQueue(strRequest);

        }
    }

}
