package radioandrea.net.radaware;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.hardware.Camera;
import android.hardware.Camera.PictureCallback;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class MainActivity extends Activity implements SurfaceHolder.Callback {
    Camera camera;
    SurfaceView surfaceView;
    SurfaceHolder surfaceHolder;
    TextView textView2;


    Camera.PictureCallback rawCallback;
    Camera.ShutterCallback shutterCallback;
    Camera.PictureCallback jpegCallback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textView2 = (TextView) findViewById(R.id.textView2);

        surfaceView = (SurfaceView) findViewById(R.id.surfaceView);
        surfaceHolder = surfaceView.getHolder();

        surfaceHolder.addCallback(this);
        surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

        jpegCallback = new PictureCallback() {

            @Override
            public void onPictureTaken(byte[] data, Camera camera) {

                Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
                textView2.setText(safetyZone(listLum(bitmap)));


                Toast.makeText(getApplicationContext(), "Picture Run", Toast.LENGTH_LONG).show();
                refreshCamera();
            }
        };
    }

    public void captureImage(View v) throws IOException {
        camera.takePicture(null, null, jpegCallback);


    }

    public void refreshCamera() {
        if (surfaceHolder.getSurface() == null) {
            return;
        }

        try {
            camera.stopPreview();
        }

        catch (Exception e) {
        }

        try {
            camera.setPreviewDisplay(surfaceHolder);
            camera.startPreview();
        }
        catch (Exception e) {
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        try {
            camera = Camera.open();
        }

        catch (RuntimeException e) {
            System.err.println(e);
            return;
        }

        Camera.Parameters param;
        param = camera.getParameters();
        param.setPreviewSize(352, 288);
        camera.setParameters(param);

        try {
            camera.setPreviewDisplay(surfaceHolder);
            camera.startPreview();
        }

        catch (Exception e) {
            System.err.println(e);
            return;
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        refreshCamera();
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        camera.stopPreview();
        camera.release();
        camera = null;
    }

    public static double listLum(Bitmap bm) {
        int red = 0;
        int green = 0;
        int blue = 0;
        //int alpha = 0;

        double r = 0.0;
        double g = 0.0;
        double b = 0.0;

        int skip = 5;
        int totalPix = bm.getWidth() * bm.getHeight() / skip;

        for (int x = 0; x < (bm.getWidth()-skip); x+=skip) {
            for (int y = 0; y < (bm.getHeight()-skip); y+=skip) {
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
        int cutoff = 300;
        int warning = 100;

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