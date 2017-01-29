package br.com.petasoft.camaraalerta.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import br.com.petasoft.camaraalerta.R;

import static android.widget.ImageView.ScaleType.CENTER_CROP;
import static android.widget.ImageView.ScaleType.FIT_CENTER;
import static android.widget.ImageView.ScaleType.FIT_XY;

/**
 * Created by Lucas on 11/14/2016.
 */

public class FotosTiradasActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fotos_tiradas_layout);
        final Bundle b = this.getIntent().getExtras();
        String[] paths = b.getStringArray("fotos");

        LinearLayout layout = (LinearLayout)findViewById(R.id.layoutFotos);
        for (int i = 0; i < paths.length; i++) {
            ImageView image = new ImageView(getApplicationContext());
            image.setLayoutParams(new android.view.ViewGroup.LayoutParams(360, 360));

            image.setScaleType(CENTER_CROP);

            //redimensaionar o bitmap
            Bitmap myBitmapGrande = BitmapFactory.decodeFile(paths[i]);
            Bitmap myBitmap = Bitmap.
                    createScaledBitmap(myBitmapGrande, myBitmapGrande.getWidth() / 6, myBitmapGrande.getHeight() / 6, false);
            image.setImageBitmap(myBitmap);

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

}
