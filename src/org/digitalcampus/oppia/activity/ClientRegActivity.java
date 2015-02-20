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

import android.app.ProgressDialog;
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
    private ProgressDialog pDialog;

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

        ArrayAdapter<CharSequence> cwfadapter = ArrayAdapter.createFromResource(this,
		        R.array.sex, android.R.layout.simple_spinner_item);
		// Specify the layout to use when the list of choices appears
		cwfadapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		// Apply the adapter to the spinner
		sexSpinner.setAdapter(cwfadapter);
		
		ArrayAdapter<CharSequence> cwfadapter2 = ArrayAdapter.createFromResource(this,
		        R.array.yesno, android.R.layout.simple_spinner_item);
		// Specify the layout to use when the list of choices appears
		cwfadapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		// Apply the adapter to the spinner
		marriedSpinner.setAdapter(cwfadapter2);
		
		ArrayAdapter<CharSequence> cwfadapter3 = ArrayAdapter.createFromResource(this,
		        R.array.parity, android.R.layout.simple_spinner_item);
		// Specify the layout to use when the list of choices appears
		cwfadapter3.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		// Apply the adapter to the spinner
		paritySpinner.setAdapter(cwfadapter3);
		
		ArrayAdapter<CharSequence> cwfadapter4 = ArrayAdapter.createFromResource(this,
		        R.array.lifestage, android.R.layout.simple_spinner_item);
		// Specify the layout to use when the list of choices appears
		cwfadapter4.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		// Apply the adapter to the spinner
		plsSpinner.setAdapter(cwfadapter4);

        counsellingButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				DbHelper db = new DbHelper(ClientRegActivity.this);
				Course c = (Course) db.getCourse(db.getCourseID("us-counsel"), db.getUserId(prefs.getString("prefUsername", "")));

                String clientName = (String) nameClientEditText.getText().toString().trim();
                String clientPhoneNumber = (String) phoneNumberClientEditText.getText().toString();
                String clientAge = (String) ageClientEditText.getText().toString();
                String clientGender = (String) sexSpinner.getSelectedItem().toString();
                String clientMarried = (String) marriedSpinner.getSelectedItem().toString();
                String clientParity = (String) paritySpinner.getSelectedItem().toString();
                String clientLifeStage = (String) plsSpinner.getSelectedItem().toString();

                if (clientName.length() == 0) {
                    UIUtils.showAlert(context, R.string.error, R.string.error_register_no_name);
                    return;
                }
                if (clientPhoneNumber.length() == 0) {
                    UIUtils.showAlert(context, R.string.error, R.string.error_register_no_phone_number);
                    return;
                }
//                if (clientPhoneNumber.length() != 10) {
//                    UIUtils.showAlert(context, R.string.error, R.string.error_register_format_phone_number);
//                    return;
//                }
                if (clientGender.length() == 0) {
                    UIUtils.showAlert(context, R.string.error, R.string.error_register_no_gender);
                    return;
                }
                if (clientMarried.length() == 0) {
                    UIUtils.showAlert(context, R.string.error, R.string.error_register_no_marital_status);
                    return;
                }
                if (clientAge.length() == 0) {
                    UIUtils.showAlert(context, R.string.error, R.string.error_register_no_age);
                    return;
                }
                if (clientParity.length() == 0) {
                    UIUtils.showAlert(context, R.string.error, R.string.error_register_no_parity);
                    return;
                }
                if (clientLifeStage.length() == 0) {
                    UIUtils.showAlert(context, R.string.error, R.string.error_register_no_lifestage);
                    return;
                }

//                pDialog = new ProgressDialog(context);
//                pDialog.setTitle(R.string.register_alert_title);
//                pDialog.setMessage(getString(R.string.register_process));
//                pDialog.setCancelable(true);
//                pDialog.show();

//                ArrayList<Object> clients = new ArrayList<Object>();
                Client client = new Client();
                client.setClientName(clientName);
                client.setClientMobileNumber(clientPhoneNumber);
                client.setClientAge(clientAge);
                client.setClientGender(clientGender);
                client.setClientMaritalStatus(clientMarried);
                client.setClientParity(clientParity);
                client.setClientLifeStage(clientLifeStage);
                client.setLastModifiedDate(System.currentTimeMillis());
                client.setHealthWorker(prefs.getString("prefUsername", ""));

                client.setClientId(db.addOrUpdateClient(client));
                DatabaseManager.getInstance().closeDatabase();

                SharedPreferences.Editor editor = prefs.edit();
                editor.putLong("prefClientLocalID", client.getClientId());
                editor.commit();

//                Intent i = new Intent(ClientRegActivity.this, CourseIndexActivity.class);
                Intent i = new Intent(ClientRegActivity.this, ClientInfoActivity.class);
				Bundle tb = new Bundle();
				tb.putSerializable(Course.TAG, c);
				tb.putInt(MobileLearning.UJJWAL_COMPONENT_TAG, MobileLearning.CLIENT_COUNSELLING_COMPONENT);
				i.putExtras(tb);
				startActivity(i);
				ClientRegActivity.this.finish();
//                startActivity(new Intent(ClientRegActivity.this, ClientInfoActivity.class));
			}
		});
	}
}
