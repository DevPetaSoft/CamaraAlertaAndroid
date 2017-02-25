package br.com.petasoft.camaraalerta.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;

import br.com.petasoft.camaraalerta.R;
import model.Configuration;

import static android.widget.ImageView.ScaleType.CENTER_CROP;
import static android.widget.ImageView.ScaleType.FIT_XY;

public class FotoFullscreenActivity extends AppCompatActivity {

    private boolean clicado = false;
    private ImageView imagemDelete;
    private String path;
    private String source;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_foto_fullscreen);
        imagemDelete = (ImageView) findViewById(R.id.botaoDelete);
        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if (extras == null) {
                path = null;
            } else {
                path = extras.getString("pathFoto");
                source = extras.getString("source");
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
            if(source.equals("N")) {
                imagem.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (clicado) {
                            imagemDelete.setVisibility(View.GONE);
                            clicado = false;
                        } else {
                            imagemDelete.setVisibility(View.VISIBLE);
                            clicado = true;
                            imagemDelete.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    new AlertDialog.Builder(FotoFullscreenActivity.this)
                                            .setTitle("Deletar")
                                            .setMessage("Deseja deletar a foto?")
                                            .setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    File arquivo = new File(path);
                                                    arquivo.delete();
                                                    Intent resultIntent = new Intent();
                                                    resultIntent.putExtra("path", path);
                                                    setResult(Activity.RESULT_OK, resultIntent);
                                                    finish();
                                                }

                                            })
                                            .setNegativeButton("Não", null)
                                            .show();
                                }
                            });
                        }
                    }
                });
            }
        }
    }

}
