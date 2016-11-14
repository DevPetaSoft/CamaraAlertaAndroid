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
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import br.com.petasoft.camaraalerta.R;

public class NovaDenuncia extends AppCompatActivity {

    static final int REQUEST_IMAGE_CAPTURE = 1;
    private List<String> listaPaths;

    FirstFrameDenuncia f1;

    SecondFrameDenuncia f2;

    private EditText titulo = null;
    private Spinner vereador = null;
    //endereço virá aqui
    private EditText descricao = null;
    private CheckBox anonima = null;



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

            //colocar o caminho dentro da ArrayList para a proxima activity
            listaPaths.add(mCurrentPhotoPath);

            fotoDenuncia.setImageBitmap(myBitmap);
        }
    }

    public void proximoFrame(View v){
        FragmentManager fragmentManager = getFragmentManager();

        fragmentManager.beginTransaction()
                .replace(R.id.denuncia_frame
                        , f2)
                .commit();

    }

    public void anteriorFrame(View v){
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

                }else {

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

}
