package radioandrea.net.radaware;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.hardware.Camera;
import android.net.Uri;
import android.os.Environment;
import android.os.IBinder;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {

    private ImageView imageView;
    private int colorId = 0;
    private static final int BLUR_RADIUS = 4;
    static Bitmap largeIcon;

    private Uri outputFileUri;
    private ImageView pictureView;
    private static final int TAKE_PICTURE = 0;
    private final File file = new File(Environment.getExternalStorageDirectory(), "implicit.jpg");
    private final File outputFile = new File(Environment.getExternalStorageDirectory(), "share.jpg");
    private double scaleFactor;
    private int sketch;
    private int color;
    private String subjectString;
    private String textString;
    TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button button = (Button) findViewById(R.id.button);
        textView = (TextView) findViewById(R.id.gammaDisplay);
        imageView = (ImageView) findViewById(R.id.imageView);
        scaleFactor = 0.8;

    }

    public void launchCameraApp(View v) {
        // create intent to take picture with camera and specify storage
        // location so we can easily get it
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        outputFileUri = Uri.fromFile(file);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
        startActivityForResult(intent, TAKE_PICTURE);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case (TAKE_PICTURE):
                takepicture(resultCode);
                break;
        }
    }

    private void takepicture(int resultCode) {
        if (resultCode == RESULT_OK) {
            int height = 100;
            int width = 100;
            Bitmap bitmap = Camera_Helpers.loadAndScaleImage(file.getAbsolutePath(), height, width);
            //imageView.setImageBitmap(bitmap);
            textView.setText(safetyZone(listLum(bitmap)));
            Camera_Helpers.saveProcessedImage(bitmap, file.getAbsolutePath());
        }
    }

    public static double listLum(Bitmap bm) {
        int red = 0;
        int green = 0;
        int blue = 0;
        //int alpha = 0;

        double r = 0.0;
        double g = 0.0;
        double b = 0.0;

        int totalPix = bm.getWidth() * bm.getHeight();

        for (int x = 0; x < bm.getWidth(); x++) {
            for (int y = 0; y < bm.getHeight(); y++) {
                int colour = bm.getPixel(x, y);

                red = Color.red(colour);
                green = Color.green(colour);
                blue = Color.blue(colour);
                //alpha = Color.alpha(colour);

                r += red;
                g += green;
                b += blue;
            }
        }

        r = r / totalPix;
        g = g / totalPix;
        b = b / totalPix;


        Log.i("red", r + "");
        Log.i("green", g + "");
        Log.i("blue", b + "");

        double lum = (0.299 * Math.sqrt(r * r)) + (0.587 * (g * g)) + (0.114 * (b * b));

        //Log.i("Luminance", lum + "");

        return lum;
    }

    public static String safetyZone(double lum) {
        int cutoff = 7000;
        int warning = 5000;

        if (lum >= cutoff) {
            return "Fail!!!";
        } else if (lum < cutoff && lum > warning) {
            return "Warning";
        } else if (lum < warning) {
            return "Safe";
        }

        return "System Failure";
    }
}
