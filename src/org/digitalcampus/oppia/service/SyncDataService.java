package org.digitalcampus.oppia.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.util.Log;

import com.bugsense.trace.BugSenseHandler;

import org.digitalcampus.oppia.application.DbHelper;
import org.digitalcampus.oppia.application.MobileLearning;
import org.digitalcampus.oppia.listener.ClientDataSyncListener;
import org.digitalcampus.oppia.model.Client;
import org.digitalcampus.oppia.task.ClientDataSyncTask;
import org.digitalcampus.oppia.task.Payload;

import java.util.ArrayList;

public class SyncDataService extends Service implements ClientDataSyncListener {
    public static final String TAG = SyncDataService.class.getSimpleName();

    private final IBinder mBinder = new MyBinder();
    private SharedPreferences prefs;

    @Override
    public void onCreate() {
        super.onCreate();
        BugSenseHandler.initAndStartSession(this, MobileLearning.BUGSENSE_API_KEY);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d("SyncDataService", "SyncDataService");
        boolean backgroundData = true;
        Bundle b = intent.getExtras();
        if (b != null) {
            backgroundData = b.getBoolean("backgroundData");
        }
        if (isOnline() && backgroundData) {

            Payload p = null;

            prefs = PreferenceManager.getDefaultSharedPreferences(this);
            long lastRun = prefs.getLong("lastClientDataSync", 0L);

            long now = System.currentTimeMillis()/1000;
            if((lastRun + 3600*24 ) < now){ // checking when the last sync was done
//            if(lastRun < now){
                DbHelper db = new DbHelper(this);
                // getting the list of clients to be synced
                ArrayList<Client> clients = new ArrayList<Client>(db.getClientsForUpdates(prefs.getString("prefUsername",""), lastRun));
                ClientDataSyncTask task = new ClientDataSyncTask(this);
                p = new Payload(clients);
                p.setUrl(MobileLearning.SYNC_CLIENTS_DATA);
//                in the payload object, sent client arraylist and URL to the client sync task
                task.setClientDataSyncListener(this);
                task.execute(p);
            }
        }
        return Service.START_NOT_STICKY;
    }

    @Override
    public IBinder onBind(Intent arg0) {
        return mBinder;
    }

    public class MyBinder extends Binder {
        public SyncDataService getService() {
            return SyncDataService.this;
        }
    }

    private boolean isOnline() {
        getApplicationContext();
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnected()) {
            return true;
        }
        return false;
    }

    public void clientDataSyncComplete(Payload response) {
    }
}
