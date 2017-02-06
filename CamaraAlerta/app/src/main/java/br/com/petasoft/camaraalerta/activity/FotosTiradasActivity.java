package br.com.petasoft.camaraalerta.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;

import br.com.petasoft.camaraalerta.R;

import static android.widget.ImageView.ScaleType.CENTER_CROP;
import static android.widget.ImageView.ScaleType.FIT_CENTER;
import static android.widget.ImageView.ScaleType.FIT_XY;

/**
 * Created by Lucas on 11/14/2016.
 */

public class FotosTiradasActivity extends AppCompatActivity {
    private int id;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fotos_tiradas_layout);
        final Bundle b = this.getIntent().getExtras();
        String[] paths = b.getStringArray("fotos");
        id = b.getInt("id");

        LinearLayout layout = (LinearLayout)findViewById(R.id.layoutFotos);
        for (int i = 0; i < paths.length; i++) {
            ImageView image = new ImageView(getApplicationContext());
            image.setLayoutParams(new android.view.ViewGroup.LayoutParams(360, 360));
            image.setId(78+i);

            image.setScaleType(CENTER_CROP);

            File arquivo = new File(paths[i]);
            if (!arquivo.exists()) {
                String pathAbsoluto = arquivo.getAbsolutePath();
                int pos = pathAbsoluto.lastIndexOf("/");
                String pathDir = pathAbsoluto.substring(0, pos + 1);
                File directory = new File(pathDir);
                if (!directory.exists()) {
                    directory.mkdir();
                }
                String url = "http://191.252.0.77/public/denounce_image/" + id + "_" + i + ".png";
                new DownloadFilesTaskFotosTiradas().execute(url, ("" + image.getId()), arquivo.getAbsolutePath());

            } else {
                Bitmap myBitmapGrande = BitmapFactory.decodeFile(arquivo.getPath());
                Bitmap myBitmap = Bitmap.
                        createScaledBitmap(myBitmapGrande, myBitmapGrande.getWidth() / 6, myBitmapGrande.getHeight() / 6, false);
                image.setImageBitmap(myBitmap);
            }

            //redimensaionar o bitmap
            /*
            Bitmap myBitmapGrande = BitmapFactory.decodeFile(paths[i]);
            Bitmap myBitmap = Bitmap.
                    createScaledBitmap(myBitmapGrande, myBitmapGrande.getWidth() / 6, myBitmapGrande.getHeight() / 6, false);
            image.setImageBitmap(myBitmap);
            */

            final String currentPath = paths[i];

            image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(FotosTiradasActivity.this, FotoFullscreenActivity.class);
                    intent.putExtra("pathFoto", currentPath);
                    /*TODO: Deletar Fotos
                    intent.putExtra("source", "F");
                    */
                    startActivity(intent);
                }
            });

            // Adds the view to the layout
            layout.addView(image);

        }
    }

    private class DownloadFilesTaskFotosTiradas extends AsyncTask<String, Void, Bitmap> {
        String idImage;
        String path;
        protected Bitmap doInBackground(String... strings) {
            String url = strings[0];
            idImage = strings[1];
            path = strings[2];
            try{
                URL urlToGet = new URL(url);
                Bitmap bitmap = BitmapFactory.decodeStream(urlToGet.openConnection().getInputStream());
                return bitmap;
            } catch (Exception e){
                return null;
            }
        }

        protected void onPostExecute(Bitmap result) {
            if(result!=null) {
                File arquivo = new File(path);
                FileOutputStream out;
                try {
                    Log.d("Imagem path", arquivo.getPath());
                    out = new FileOutputStream(arquivo);
                    result.compress(Bitmap.CompressFormat.JPEG, 100, out); // bmp is your Bitmap instance
                    out.flush();
                    out.close();
                } catch (FileNotFoundException e) {
                    Log.d("Imagem", "Erro File not found");
                    e.printStackTrace();
                } catch (IOException e) {
                    Log.d("Imagem", "Erro IO");
                    e.printStackTrace();
                }
                int id = Integer.parseInt(idImage);
                ImageView imageToUpdate = (ImageView) findViewById(id);
                imageToUpdate.setImageBitmap(Bitmap.createScaledBitmap(result, result.getWidth() / 5, result.getHeight() / 5, false));
            }
        }
    }

}
