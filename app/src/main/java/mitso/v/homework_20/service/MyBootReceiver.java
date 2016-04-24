package mitso.v.homework_20.service;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.WakefulBroadcastReceiver;

public class MyBootReceiver extends WakefulBroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        if (intent.getAction().equalsIgnoreCase(Intent.ACTION_BOOT_COMPLETED)) {

//            final Intent i = new Intent(context, MyAlarmReceiver.class);
//            final PendingIntent pIntent = PendingIntent.getBroadcast(context, MyAlarmReceiver.REQUEST_CODE, i, PendingIntent.FLAG_UPDATE_CURRENT);
//
//            final AlarmManager alarm = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
//            alarm.setInexactRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), 60000, pIntent);
        }
    }
}