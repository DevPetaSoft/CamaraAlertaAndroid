package br.com.petasoft.camaraalerta.activity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.LinearLayout;

import br.com.petasoft.camaraalerta.R;

import static android.widget.ImageView.ScaleType.FIT_XY;

/**
 * Created by Lucas on 11/14/2016.
 */

public class FotosTiradasActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fotos_tiradas_layout);
        Bundle b = this.getIntent().getExtras();
        String[] paths = b.getStringArray("fotos");

        LinearLayout layout = (LinearLayout)findViewById(R.id.myLinearLayout);
        for(int i=0;i<paths.length;i++){
            ImageView image = new ImageView(this);
            image.setLayoutParams(new android.view.ViewGroup.LayoutParams(360,360));

            image.setScaleType(FIT_XY);


            Bitmap myBitmap = BitmapFactory.decodeFile(paths[i]);

            //redimensaionar o bitmap
            myBitmap = Bitmap.createScaledBitmap(myBitmap, 360, 360, false);

            image.setImageBitmap(myBitmap);


            // Adds the view to the layout
            layout.addView(image);

        }
    }

}
