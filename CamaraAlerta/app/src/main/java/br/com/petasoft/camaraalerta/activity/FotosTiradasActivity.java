package br.com.petasoft.camaraalerta.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Display;
import android.view.MenuItem;
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
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarFotos);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        final Bundle b = this.getIntent().getExtras();
        String[] paths = b.getStringArray("fotos");
        id = b.getInt("id");

        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x;

        int sizeFotos = (width/3) - 10;

        LinearLayout layout = (LinearLayout)findViewById(R.id.layoutFotosTiradas);
        LinearLayout layout2 = (LinearLayout)findViewById(R.id.layoutFotosTiradas2);
        for (int i = 0; i < paths.length; i++) {
            ImageView image = new ImageView(getApplicationContext());
            image.setLayoutParams(new android.view.ViewGroup.LayoutParams(sizeFotos, sizeFotos));
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
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inJustDecodeBounds = true;
                //options.inInputShareable = true;
                //options.inPurgeable = true;

                BitmapFactory.decodeFile(arquivo.getPath(), options);
                if ((options.outWidth == -1) || (options.outHeight == -1)) {

                } else {
                    int originalSize = (options.outHeight > options.outWidth) ? options.outHeight
                            : options.outWidth;

                    BitmapFactory.Options opts = new BitmapFactory.Options();
                    opts.inSampleSize = originalSize / 275;

                    Bitmap myBitmap = BitmapFactory.decodeFile(arquivo.getPath(), opts);
                    image.setImageBitmap(myBitmap);
                }
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
                    intent.putExtra("source", "F");
                    startActivity(intent);
                }
            });

            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(sizeFotos, sizeFotos);
            layoutParams.setMargins(5,5,5,5);


            // Adds the view to the layout
            if(i<3) {
                layout.addView(image, layoutParams);
            } else {
                layout2.addView(image, layoutParams);
            }

        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
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
