package br.com.petasoft.camaraalerta.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;

import br.com.petasoft.camaraalerta.R;
import model.Denuncia;

import static android.widget.ImageView.ScaleType.CENTER_CROP;

public class MostrarDenuncia extends AppCompatActivity {

    private ImageView imagem;
    private TextView titulo;
    private TextView descricao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mostrar_denuncia);
        imagem = (ImageView)findViewById(R.id.fotoDenuncia);
        titulo = (TextView)findViewById(R.id.tituloDenuncia);
        descricao = (TextView)findViewById(R.id.descricaoDenuncia);

        Intent intent = getIntent();
        Denuncia denuncia = (Denuncia)intent.getSerializableExtra("Denuncia");

        imagem.setScaleType(CENTER_CROP);


        File arquivo = new File(denuncia.getFotos().get(0));

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

            BitmapFactory.Options opts = new BitmapFactory.Options();
            opts.inSampleSize = originalSize / 275;

            Bitmap myBitmap = BitmapFactory.decodeFile(arquivo.getPath(), opts);


            imagem.setImageBitmap(myBitmap);
        }

        titulo.setText(denuncia.getTitulo());
        descricao.setText(denuncia.getDescricao());
    }
}
