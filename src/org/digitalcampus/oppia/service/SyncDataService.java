package org.digitalcampus.oppia.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.bugsense.trace.BugSenseHandler;

import org.digitalcampus.oppia.activity.DownloadActivity;
import org.digitalcampus.oppia.application.DatabaseManager;
import org.digitalcampus.oppia.application.DbHelper;
import org.digitalcampus.oppia.application.MobileLearning;
import org.digitalcampus.oppia.listener.ClientDataSyncListener;
import org.digitalcampus.oppia.model.Client;
import org.digitalcampus.oppia.task.ClientDataSyncTask;
import org.digitalcampus.oppia.task.Payload;
import org.json.JSONException;
import org.json.JSONObject;
import org.ujjwal.saathi.oppia.mobile.learning.R;

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
        Log.d("SyncDataService","SyncDataReceiver");
        boolean backgroundData = true;
        Bundle b = intent.getExtras();
        if (b != null) {
            backgroundData = b.getBoolean("backgroundData");
        }
        if (isOnline() && backgroundData) {

            Payload p = null;

            // check for updated courses
            // should only do this once a day or so....
            prefs = PreferenceManager.getDefaultSharedPreferences(this);
            long lastRun = prefs.getLong("lastClientDataSync", 0);

            long now = System.currentTimeMillis()/1000;
            if(lastRun < now){
//            if((lastRun + (3600*24)) < now){
                DbHelper db = new DbHelper(this);
//                long userId = db.isUser();
                ArrayList<Client> clients = new ArrayList<Client>(db.getClientsForUpdates(prefs.getString("prefUsername",""), lastRun));
                ClientDataSyncTask task = new ClientDataSyncTask(this);
//                p = new Payload(MobileLearning.SYNC_CLIENTS_DATA);
                p = new Payload(clients);
                task.setClientDataSyncListener(this);
                task.execute(p);

                SharedPreferences.Editor editor = prefs.edit();
                editor.putLong("lastClientDataSync", now);
                editor.commit();
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
        boolean updateAvailable = false;
        try {
            JSONObject json = new JSONObject(response.getResultResponse());
            Log.d(TAG,json.toString(4));
            for (int i = 0; i < (json.getJSONArray("courses").length()); i++) {
                JSONObject json_obj = (JSONObject) json.getJSONArray("courses").get(i);
                String shortName = json_obj.getString("shortname");
                Double version = json_obj.getDouble("version");
                DbHelper db = new DbHelper(this);
                if(db.toUpdate(shortName,version)){
                    updateAvailable = true;
                }
                if(json_obj.has("schedule")){
                    Double scheduleVersion = json_obj.getDouble("schedule");
                    if(db.toUpdateSchedule(shortName, scheduleVersion)){
                        updateAvailable = true;
                    }
                }
                DatabaseManager.getInstance().closeDatabase();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if(updateAvailable){
            Bitmap icon = BitmapFactory.decodeResource(getResources(),
                    R.drawable.ujjwal_logo);
            NotificationCompat.Builder mBuilder =
                    new NotificationCompat.Builder(this)
                            .setSmallIcon(R.drawable.ic_stat_notification)
                            .setLargeIcon(icon)
                            .setContentTitle(getString(R.string.notification_course_update_title))
                            .setContentText(getString(R.string.notification_course_update_text));
            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            Intent resultIntent = new Intent(this, DownloadActivity.class);
            PendingIntent resultPendingIntent =
                    PendingIntent.getActivity(
                            this,
                            0,
                            resultIntent,
                            PendingIntent.FLAG_UPDATE_CURRENT
                    );
            mBuilder.setContentIntent(resultPendingIntent);
            int mId = 001;
            Notification notification = mBuilder.build();
            notification.flags |= Notification.FLAG_AUTO_CANCEL;
            notificationManager.notify(mId, notification);
        }
    }
}
