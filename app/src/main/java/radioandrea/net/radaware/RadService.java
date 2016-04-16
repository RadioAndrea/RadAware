package radioandrea.net.radaware;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import java.util.Timer;
import java.util.TimerTask;

public class RadService extends Service {

    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {
        final Handler handler = new Handler();
        Timer timer = new Timer();
        TimerTask doAsynchronousTask = new TimerTask() {
            @Override
            public void run() {
                handler.post(new Runnable() {
                    public void run() {
                        try {
                            Log.i("RadService", "Do camera things");
                            //todo


                        } catch (Exception e) {
                        }
                    }
                });
            }
        };
        timer.schedule(doAsynchronousTask, 0, 600000); //execute in every 10 ms
        return 0;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}