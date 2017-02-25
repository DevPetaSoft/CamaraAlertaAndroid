package br.com.petasoft.camaraalerta.activity;

import android.app.Fragment;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;

import java.io.File;

import br.com.petasoft.camaraalerta.R;

import static android.widget.ImageView.ScaleType.CENTER_CROP;

public class ThirdFrameDenuncia extends Fragment {
    View myView;
    LinearLayout layout;
    private String[] paths;
    private int i;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        myView = inflater.inflate(R.layout.fragment_third_frame_denuncia, container, false);

        Bundle b = this.getArguments();
        paths = b.getStringArray("fotos");

        LinearLayout layoutFotos = (LinearLayout)myView.findViewById(R.id.layoutFotos);
        LinearLayout layoutFotos2 = (LinearLayout)myView.findViewById(R.id.layoutFotos2);
        Display display = getActivity().getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x;

        int sizeFotos = (width/3) - 10;

        for(i=0;i<paths.length;i++){
            ImageView image = new ImageView(getActivity());
            image.setLayoutParams(new android.view.ViewGroup.LayoutParams(sizeFotos,sizeFotos));
            image.setScaleType(CENTER_CROP);


            /*Antigo reescalamento bitmap
            Bitmap myBitmap = BitmapFactory.decodeFile(paths[i]);

            //redimensaionar o bitmap
            myBitmap = Bitmap.createScaledBitmap(myBitmap, myBitmap.getScaledWidth(50), myBitmap.getScaledHeight(50), false);
            */

            //Nova função de reescalar bitmap
            File arquivo = new File(paths[i]);
            final String currentPath = paths[i];

            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            //options.inInputShareable = true;
            //options.inPurgeable = true;

            BitmapFactory.decodeFile(arquivo.getPath(), options);
            if ((options.outWidth == -1) || (options.outHeight == -1))
                return null;

            int originalSize = (options.outHeight > options.outWidth) ? options.outHeight
                    : options.outWidth;

            BitmapFactory.Options opts = new BitmapFactory.Options();
            opts.inSampleSize = originalSize / 275;

            Bitmap myBitmap = BitmapFactory.decodeFile(arquivo.getPath(), opts);

            image.setImageBitmap(myBitmap);

            image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ((NovaDenuncia)getActivity()).verFullScreen(currentPath);
                }
            });

            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                    sizeFotos, sizeFotos);

            layoutParams.setMargins(5,5,5,5);

            // Adds the view to the layout
            if(i<3) {
                layoutFotos.addView(image, layoutParams);
            } else {
                layoutFotos2.addView(image, layoutParams);
            }

        }

        return myView;
    }

    public interface InterfaceFrame3 {
        public void onFragment3Return(String string);
    }

}
