package mitso.v.homework_20.service;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.WakefulBroadcastReceiver;

public class MyAlarmReceiver extends WakefulBroadcastReceiver {

    public static final int REQUEST_CODE = 789;
    public static final String ACTION = "ALARM";

    @Override
    public void onReceive(Context context, Intent intent) {

        Intent i = new Intent(context, MyTestService.class);
        i.putExtra("xxx", "ZZZ");
        startWakefulService(context, i);
    }
}