package org.digitalcampus.oppia.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;

import java.util.Calendar;

public class SyncDataServiceReceiver extends BroadcastReceiver {

    public final static String TAG = SyncDataServiceReceiver.class.getSimpleName();

    @Override
    public void onReceive(Context ctx, Intent intent) {

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(ctx);
        boolean backgroundData = prefs.getBoolean("prefBackgroundDataConnect", true);
        Intent service = new Intent(ctx, SyncDataService.class);

        Bundle tb = new Bundle();
        tb.putBoolean("backgroundData", backgroundData);
        service.putExtras(tb);

        ctx.startService(service);

        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.SECOND, 30);
//        ctx.stopService(service);
    }
}
