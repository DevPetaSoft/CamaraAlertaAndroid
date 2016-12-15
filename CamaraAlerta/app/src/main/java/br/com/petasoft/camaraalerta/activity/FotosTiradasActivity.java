package br.com.petasoft.camaraalerta.activity;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
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
    private ProgressDialog progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fotos_tiradas_layout);
        final Bundle b = this.getIntent().getExtras();
        progress=new ProgressDialog(this);
        progress.setMessage("Carregando fotos");
        progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progress.setIndeterminate(true);
        progress.setProgress(0);
        progress.show();

        final Thread t = new Thread() {
            @Override
            public void run() {
                String[] paths = b.getStringArray("fotos");

                LinearLayout layout = (LinearLayout)findViewById(R.id.layoutFotos);
                for (int i = 0; i < paths.length; i++) {
                    ImageView image = new ImageView(getApplicationContext());
                    image.setLayoutParams(new android.view.ViewGroup.LayoutParams(360, 360));

                    image.setScaleType(CENTER_CROP);


                    Bitmap myBitmap = BitmapFactory.decodeFile(paths[i]);

                    //redimensaionar o bitmap
                    myBitmap = Bitmap.createScaledBitmap(myBitmap, myBitmap.getScaledWidth(50), myBitmap.getScaledHeight(50), false);

                    image.setImageBitmap(myBitmap);


                    // Adds the view to the layout
                    layout.addView(image);

                }
            }
        };
        t.start();
    }

}
