package mitso.v.homework_20.service;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.WakefulBroadcastReceiver;

public class AlarmReceiver extends WakefulBroadcastReceiver {

    @Override
    public void onReceive(Context _context, Intent _intent) {

        final Intent i = new Intent(_context, UpdateService.class);
        startWakefulService(_context, i);
    }
}