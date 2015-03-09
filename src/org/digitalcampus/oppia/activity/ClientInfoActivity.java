package org.digitalcampus.oppia.activity;

import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;

import org.digitalcampus.oppia.application.DbHelper;
import org.digitalcampus.oppia.application.MobileLearning;
import org.digitalcampus.oppia.model.Client;
import org.digitalcampus.oppia.model.Course;
import org.digitalcampus.oppia.model.Lang;
import org.digitalcampus.oppia.service.TrackerService;
import org.digitalcampus.oppia.utils.UIUtils;
import org.ujjwal.saathi.oppia.mobile.learning.R;

import java.util.ArrayList;
import java.util.Map;

public class ClientInfoActivity extends AppActivity implements SharedPreferences.OnSharedPreferenceChangeListener {
    public static final String TAG = ClientInfoActivity.class.getSimpleName();
    private DbHelper db;
    private Client client;
    private SharedPreferences prefs;
    private AlertDialog aDialog;
    private ArrayList<Course> courses = new ArrayList<Course>() ;
    private TextView clientNameTextView;
    private TextView clientMobileTextView;
    private TextView clientGenderTextView;
    private TextView clientMaritalStatusTextView;
    private TextView clientAgeTextView;
    private TextView clientParityTextView;
    private TextView clientLifeStageTextView;
    private Button makeVisitButton, editClientInfoButton, makeCallButton;//button_client_edit

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client_detail);
        clientNameTextView = (TextView) findViewById(R.id.client_name_value);
        clientMobileTextView = (TextView) findViewById(R.id.client_mobile_number_value);
        clientGenderTextView = (TextView) findViewById(R.id.client_sex_value);
        clientMaritalStatusTextView = (TextView) findViewById(R.id.client_is_married_value);
        clientAgeTextView = (TextView) findViewById(R.id.client_age_value);
        clientParityTextView = (TextView) findViewById(R.id.client_parity_value);
        clientLifeStageTextView = (TextView) findViewById(R.id.client_life_stage_value);
        makeVisitButton = (Button) findViewById(R.id.client_detail_visit_button);
        editClientInfoButton = (Button) findViewById(R.id.client_edit_info_button);
        makeCallButton = (Button) findViewById(R.id.client_call_button);
        makeVisitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Course c = (Course) db.getCourse(db.getCourseID("us-counsel"), db.getUserId(prefs.getString("prefUsername", "")));
                Intent i = new Intent(ClientInfoActivity.this, CourseIndexActivity.class);
                Bundle tb = new Bundle();
                tb.putSerializable(Course.TAG, c);
                tb.putInt(MobileLearning.UJJWAL_COMPONENT_TAG, MobileLearning.CLIENT_COUNSELLING_COMPONENT);
                i.putExtras(tb);
                startActivity(i);
                ClientInfoActivity.this.finish();
            }
        });
        editClientInfoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(ClientInfoActivity.this, ClientRegActivity.class);
                Bundle tb = new Bundle();
                tb.putBoolean("editClient", true);
                tb.putLong("localClientID", client.getClientId());
                i.putExtras(tb);
                startActivity(i);
                ClientInfoActivity.this.finish();
            }
        });
        makeCallButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Intent callIntent = new Intent(Intent.ACTION_CALL);
                    callIntent.setData(Uri.parse("tel:" + "+91" + Long.toString(client.getClientMobileNumber())));
                    startActivity(callIntent);
                } catch (ActivityNotFoundException activityException) {
                    Log.e("Calling a Phone Number", "Call failed", activityException);
                }
            }
        });

        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        PreferenceManager.setDefaultValues(this, R.xml.prefs, false);
    }

//    @Override
//    public void drawReminders(ArrayList<Activity> activities) {
//        super.drawReminders(activities);
//    }

    @Override
    public void onStart() {
        super.onStart();
        db = new DbHelper(this);
        long clientId = prefs.getLong("prefClientLocalID", 0L);
        client = db.getClient(clientId);
        clientNameTextView.setText(client.getClientName());
        clientMobileTextView.setText(Long.toString(client.getClientMobileNumber()));
        clientGenderTextView.setText(client.getClientGender());
        clientMaritalStatusTextView.setText(client.getClientMaritalStatus());
        clientAgeTextView.setText(Integer.toString(client.getClientAge()));
        clientParityTextView.setText(client.getClientParity());
        clientLifeStageTextView.setText(client.getClientLifeStage());
    }

    @Override
    public void onPause() {
        if (aDialog != null) {
            aDialog.dismiss();
            aDialog = null;
        }
        super.onPause();
    }

    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equalsIgnoreCase("prefPoints")
                || key.equalsIgnoreCase("prefBadges")) {
            supportInvalidateOptionsMenu();
        }
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        UIUtils.showUserData(menu, this);
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getSupportMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        Log.d(TAG, "selected:" + item.getItemId());
        switch (item.getItemId()) {
            case R.id.menu_about:
                startActivity(new Intent(this, AboutActivity.class));
                return true;
            case R.id.menu_download:
                startActivity(new Intent(this, TagSelectActivity.class));
                return true;
            case R.id.menu_settings:
                Intent i = new Intent(this, PrefsActivity.class);
                Bundle tb = new Bundle();
                ArrayList<Lang> langs = new ArrayList<Lang>();
                tb.putSerializable("langs", langs);
                i.putExtras(tb);
                startActivity(i);
                return true;
            case R.id.menu_search:
                startActivity(new Intent(this, SearchActivity.class));
                return true;
            case R.id.menu_logout:
                logout();
                return true;
        }
        return true;
    }

    private void logout() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(false);
        builder.setTitle(R.string.logout);
        builder.setMessage(R.string.logout_confirm);
        builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
            // wipe user prefs
            SharedPreferences.Editor editor = prefs.edit();
            editor.putString("prefUsername", "");
            editor.putString("prefApiKey", "");
            editor.putInt("prefBadges", 0);
            editor.putInt("prefPoints", 0);
            editor.putLong("lastClientSync", 0L);

            editor.commit();

            ClientInfoActivity.this.startActivity(new Intent(ClientInfoActivity.this, StartUpActivity.class));
//            ClientInfoActivity.this.finish();
            }
        });
        builder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                return; // do nothing
            }
        });
        builder.show();
    }

    @Override
    public void onResume() {
        super.onResume();
        // start a new tracker service
        Intent service = new Intent(this, TrackerService.class);

        Bundle tb = new Bundle();
        tb.putBoolean("backgroundData", true);
        service.putExtras(tb);
        this.startService(service);

        // remove any saved state info from shared prefs in case they interfere with subsequent page views
        SharedPreferences.Editor editor = prefs.edit();
        Map<String,?> keys = prefs.getAll();

        for(Map.Entry<String,?> entry : keys.entrySet()){
            if (entry.getKey().startsWith("widget_")){
                editor.remove(entry.getKey());
            }
        }
        editor.commit();
    }
}
