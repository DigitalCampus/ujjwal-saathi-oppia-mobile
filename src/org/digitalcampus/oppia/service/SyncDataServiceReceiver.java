package org.digitalcampus.oppia.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;

/**
 * Created by ronak on 13/2/15.
 */
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
    }
}
