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

import org.ujjwal.saathi.oppia.mobile.learning.R;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

public class ClientRegActivity extends AppActivity {
	
	public static final String TAG = ClientRegActivity.class.getSimpleName();
	private SharedPreferences prefs;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_clientreg);
		
		prefs = PreferenceManager.getDefaultSharedPreferences(this);
		PreferenceManager.setDefaultValues(this, R.xml.prefs, false);
		
	
		Spinner sexSpinner = (Spinner) findViewById(R.id.clientreg_form_sex_spinner);
		ArrayAdapter<CharSequence> cwfadapter = ArrayAdapter.createFromResource(this,
		        R.array.sex, android.R.layout.simple_spinner_item);
		// Specify the layout to use when the list of choices appears
		cwfadapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		// Apply the adapter to the spinner
		sexSpinner.setAdapter(cwfadapter);
		
		Spinner marriedSpinner = (Spinner) findViewById(R.id.clientreg_form_married_spinner);
		ArrayAdapter<CharSequence> cwfadapter2 = ArrayAdapter.createFromResource(this,
		        R.array.yesno, android.R.layout.simple_spinner_item);
		// Specify the layout to use when the list of choices appears
		cwfadapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		// Apply the adapter to the spinner
		marriedSpinner.setAdapter(cwfadapter2);
		
		Spinner paritySpinner = (Spinner) findViewById(R.id.clientreg_form_parity_spinner);
		ArrayAdapter<CharSequence> cwfadapter3 = ArrayAdapter.createFromResource(this,
		        R.array.parity, android.R.layout.simple_spinner_item);
		// Specify the layout to use when the list of choices appears
		cwfadapter3.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		// Apply the adapter to the spinner
		paritySpinner.setAdapter(cwfadapter3);
		
		Spinner plsSpinner = (Spinner) findViewById(R.id.clientreg_form_lifestage_spinner);
		ArrayAdapter<CharSequence> cwfadapter4 = ArrayAdapter.createFromResource(this,
		        R.array.lifestage, android.R.layout.simple_spinner_item);
		// Specify the layout to use when the list of choices appears
		cwfadapter4.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		// Apply the adapter to the spinner
		plsSpinner.setAdapter(cwfadapter4);
		
		Button counselling = (Button) findViewById(R.id.submit_btn);
		counselling.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				startActivity(new Intent(ClientRegActivity.this, CounsellingHomeActivity.class));
			}
		});
		
		
	}

}
