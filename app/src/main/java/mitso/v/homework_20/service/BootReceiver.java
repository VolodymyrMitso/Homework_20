package mitso.v.homework_20.service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.WakefulBroadcastReceiver;

import mitso.v.homework_20.constansts.Constants;

public class BootReceiver extends WakefulBroadcastReceiver {

    @Override
    public void onReceive(Context _context, Intent _intent) {

        if (_intent.getAction().equalsIgnoreCase(Intent.ACTION_BOOT_COMPLETED)) {

            final Intent alarmIntent = new Intent(_context, AlarmReceiver.class);
            final PendingIntent alarmRendingIntent = PendingIntent.getBroadcast(_context, Constants.ALARM_REQUEST_CODE, alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT);

            final AlarmManager alarm = (AlarmManager) _context.getSystemService(Context.ALARM_SERVICE);
            alarm.setInexactRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + Constants.TIME_5_MINUTES, AlarmManager.INTERVAL_HALF_HOUR, alarmRendingIntent);
        }
    }
}