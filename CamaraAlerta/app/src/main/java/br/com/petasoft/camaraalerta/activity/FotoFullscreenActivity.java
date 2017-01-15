package br.com.petasoft.camaraalerta.activity;

import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;

import br.com.petasoft.camaraalerta.R;

import static android.widget.ImageView.ScaleType.CENTER_CROP;
import static android.widget.ImageView.ScaleType.FIT_XY;

public class FotoFullscreenActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_foto_fullscreen);

        String path;
        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if (extras == null) {
                path = null;
            } else {
                path = extras.getString("pathFoto");
            }
        } else {
            path = (String) savedInstanceState.getSerializable("pathFoto");
        }

        ImageView imagem = (ImageView) findViewById(R.id.fotoFullscreen);
        imagem.setScaleType(FIT_XY);


        File arquivo = new File(path);


        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        //options.inInputShareable = true;
        //options.inPurgeable = true;

        BitmapFactory.decodeFile(arquivo.getPath(), options);
        if ((options.outWidth == -1) || (options.outHeight == -1)) {
            Log.d("Erro", "Foto inexistente");
        } else {

            int originalSize = (options.outHeight > options.outWidth) ? options.outHeight
                    : options.outWidth;
            Display display = getWindowManager().getDefaultDisplay();
            Point size = new Point();
            display.getSize(size);
            int height = size.y;
            BitmapFactory.Options opts = new BitmapFactory.Options();
            opts.inSampleSize = originalSize / height;

            Bitmap myBitmap = BitmapFactory.decodeFile(arquivo.getPath(), opts);


            imagem.setImageBitmap(myBitmap);
        }
    }
}
