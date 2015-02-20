package org.digitalcampus.oppia.service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.util.Calendar;

public class SyncDataReceiver extends BroadcastReceiver {
    public static final String TAG = SyncDataReceiver.class.getSimpleName();

    // Restart service every 24 hour
//    private static final long REPEAT_TIME = 1000 * 3600 * 24;
    private static final long REPEAT_TIME = 1000 * 45;

    @Override
    public void onReceive(Context context, Intent intent) {
        AlarmManager service = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        Intent i = new Intent(context, SyncDataServiceReceiver.class);
        Log.d("SyncDataReceiver","SyncDataReceiver");
        PendingIntent pending = PendingIntent.getBroadcast(context, 0, i, PendingIntent.FLAG_CANCEL_CURRENT);
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.SECOND, 30);
        // every 24 hour
        // InexactRepeating allows Android to optimize the energy consumption
        service.setInexactRepeating(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), REPEAT_TIME, pending);

    }
}
