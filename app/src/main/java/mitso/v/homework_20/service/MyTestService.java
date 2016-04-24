package mitso.v.homework_20.service;

import android.app.IntentService;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

public class MyTestService extends IntentService {

    private final String LOG_TAG = "MY_TEST_SERVICE_LOG_TAG";

    public MyTestService() {
        super("MyTestService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        String string = intent.getStringExtra("xxx");

        Log.e(LOG_TAG, "service running ... " + string);

        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(new Runnable() {

            @Override
            public void run() {
                Toast.makeText(getApplicationContext(),
                        "z z z . . .",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }
}
