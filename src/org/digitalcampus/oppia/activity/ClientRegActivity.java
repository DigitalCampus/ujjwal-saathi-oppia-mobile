/* 
 * This file is part of OppiaMobile - http://oppia-mobile.org/
 * 
 * OppiaMobile is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * OppiaMobile is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with OppiaMobile. If not, see <http://www.gnu.org/licenses/>.
 */

package org.digitalcampus.oppia.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import org.digitalcampus.oppia.application.DatabaseManager;
import org.digitalcampus.oppia.application.DbHelper;
import org.digitalcampus.oppia.application.MobileLearning;
import org.digitalcampus.oppia.model.Client;
import org.digitalcampus.oppia.model.Course;
import org.digitalcampus.oppia.utils.UIUtils;
import org.ujjwal.saathi.oppia.mobile.learning.R;

public class ClientRegActivity extends AppActivity {
	
	public static final String TAG = ClientRegActivity.class.getSimpleName();
	private SharedPreferences prefs;
    private Spinner sexSpinner, marriedSpinner, paritySpinner, plsSpinner;
    private Button counsellingButton;
    private EditText nameClientEditText, phoneNumberClientEditText, ageClientEditText;
    private Context context;
    public long clientId;

    String clientName, clientPhoneNumber, clientAge, clientGender, clientMarried, clientParity, clientLifeStage;
    ArrayAdapter<CharSequence> cwfadapter, cwfadapter2, cwfadapter3, cwfadapter4;
    DbHelper db;

    @Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_clientreg);
        context = this;

        sexSpinner = (Spinner) findViewById(R.id.clientreg_form_sex_spinner);
        marriedSpinner = (Spinner) findViewById(R.id.clientreg_form_married_spinner);
        paritySpinner = (Spinner) findViewById(R.id.clientreg_form_parity_spinner);
        plsSpinner = (Spinner) findViewById(R.id.clientreg_form_lifestage_spinner);
        counsellingButton = (Button) findViewById(R.id.submit_btn);
        nameClientEditText = (EditText) findViewById(R.id.clientreg_form_name_field);
        phoneNumberClientEditText = (EditText) findViewById(R.id.clientreg_form_mobile_field);
        ageClientEditText = (EditText) findViewById(R.id.clientreg_form_age_field);
        prefs = PreferenceManager.getDefaultSharedPreferences(this);
		PreferenceManager.setDefaultValues(this, R.xml.prefs, false);

        cwfadapter = ArrayAdapter.createFromResource(this,
		        R.array.sex, android.R.layout.simple_spinner_item);
		// Specify the layout to use when the list of choices appears
		cwfadapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		// Apply the adapter to the spinner
		sexSpinner.setAdapter(cwfadapter);
		
		cwfadapter2 = ArrayAdapter.createFromResource(this,
		        R.array.yesno, android.R.layout.simple_spinner_item);
		// Specify the layout to use when the list of choices appears
		cwfadapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		// Apply the adapter to the spinner
		marriedSpinner.setAdapter(cwfadapter2);
		
		cwfadapter3 = ArrayAdapter.createFromResource(this,
		        R.array.parity, android.R.layout.simple_spinner_item);
		// Specify the layout to use when the list of choices appears
		cwfadapter3.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		// Apply the adapter to the spinner
		paritySpinner.setAdapter(cwfadapter3);
		
		cwfadapter4 = ArrayAdapter.createFromResource(this,
		        R.array.lifestage, android.R.layout.simple_spinner_item);
		// Specify the layout to use when the list of choices appears
		cwfadapter4.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		// Apply the adapter to the spinner
		plsSpinner.setAdapter(cwfadapter4);
        db = new DbHelper(ClientRegActivity.this);

        counsellingButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Course c = (Course) db.getCourse(db.getCourseID("us-counsel"), db.getUserId(prefs.getString("prefUsername", "")));
                clientName = (String) nameClientEditText.getText().toString().trim();
                clientPhoneNumber = (String) phoneNumberClientEditText.getText().toString();
                clientAge = (String) ageClientEditText.getText().toString();
                clientGender = (String) sexSpinner.getSelectedItem().toString();
                clientMarried = (String) marriedSpinner.getSelectedItem().toString();
                clientParity = (String) paritySpinner.getSelectedItem().toString();
                clientLifeStage = (String) plsSpinner.getSelectedItem().toString();

                if (verificationClientData()) {
                    Client client = new Client();
                    client.setClientName(clientName);
                    client.setClientMobileNumber(Long.parseLong(clientPhoneNumber));
                    client.setClientAge(Integer.parseInt(clientAge));
                    client.setClientGender(clientGender);
                    client.setClientMaritalStatus(clientMarried);
                    client.setClientParity(clientParity);
                    client.setClientLifeStage(clientLifeStage);
                    client.setHealthWorker(prefs.getString("prefUsername", "")); //USER
                    Intent intent = getIntent();
                    Bundle bundle=intent.getExtras();
                    Boolean b = bundle.getBoolean("editClient");
                    if (b != null && b) {
                        client.setClientId(clientId);
                        client.setClientServerId(db.getClient(clientId).getClientServerId());
                        db.addOrUpdateClient(client);
                    } else {
                        client.setClientId(db.addClient(client));
                    }

                    DatabaseManager.getInstance().closeDatabase();

                    SharedPreferences.Editor editor = prefs.edit();
                    editor.putLong("prefClientLocalID", client.getClientId());
                    editor.commit();

                    Intent i = new Intent(ClientRegActivity.this, ClientInfoActivity.class);
                    Bundle tb = new Bundle();
                    tb.putSerializable(Course.TAG, c);
                    tb.putInt(MobileLearning.UJJWAL_COMPONENT_TAG, MobileLearning.CLIENT_COUNSELLING_COMPONENT);
                    i.putExtras(tb);
                    startActivity(i);
                    ClientRegActivity.this.finish();
                } else
                    return;
            }
        });
	}

    @Override
    public void onStart() {
        super.onStart();
        Intent intent = getIntent();
        Bundle bundle=intent.getExtras();
        clientId = bundle.getLong("localClientID");
//        long clientId = prefs.getLong("prefClientLocalID", 0L);
        if (clientId > 0) {
            Client client = db.getClient(clientId);
            nameClientEditText.setText(client.getClientName());
            phoneNumberClientEditText.setText(Long.toString(client.getClientMobileNumber()));
            ageClientEditText.setText(Integer.toString(client.getClientAge()));
            int spinnerPosition;
            spinnerPosition = cwfadapter.getPosition(client.getClientGender());
            sexSpinner.setSelection(spinnerPosition);
            spinnerPosition = cwfadapter2.getPosition(client.getClientMaritalStatus());
            marriedSpinner.setSelection(spinnerPosition);
            spinnerPosition = cwfadapter3.getPosition(client.getClientParity());
            paritySpinner.setSelection(spinnerPosition);
            spinnerPosition = cwfadapter4.getPosition(client.getClientLifeStage());
            plsSpinner.setSelection(spinnerPosition);
        }


    }

    public boolean verificationClientData() {
        if (clientName.length() == 0) {
            UIUtils.showAlert(context, R.string.error, R.string.error_register_no_name);
            return false;
        }
        if (clientPhoneNumber.length() == 0) {
            UIUtils.showAlert(context, R.string.error, R.string.error_register_no_phone_number);
            return false;
        }
        if (clientPhoneNumber.length() != 10) {
            UIUtils.showAlert(context, R.string.error, R.string.error_register_format_phone_number);
            return false;
        }
        if (clientGender.length() == 0) {
            UIUtils.showAlert(context, R.string.error, R.string.error_register_no_gender);
            return false;
        }
        if (clientMarried.length() == 0) {
            UIUtils.showAlert(context, R.string.error, R.string.error_register_no_marital_status);
            return false;
        }
        if (clientAge.length() == 0 || Integer.parseInt(clientAge) > 100) {
            UIUtils.showAlert(context, R.string.error, R.string.error_register_no_age);
            return false;
        }
        if (clientParity.length() == 0) {
            UIUtils.showAlert(context, R.string.error, R.string.error_register_no_parity);
            return false;
        }
        if (clientLifeStage.length() == 0) {
            UIUtils.showAlert(context, R.string.error, R.string.error_register_no_lifestage);
            return false;
        }
        return true;
    }


}