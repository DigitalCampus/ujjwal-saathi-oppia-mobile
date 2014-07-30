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

public class PreTestActivity extends AppActivity {
	public static final String TAG = ClientRegActivity.class.getSimpleName();
	private SharedPreferences prefs;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_pretest);
		
		prefs = PreferenceManager.getDefaultSharedPreferences(this);
		PreferenceManager.setDefaultValues(this, R.xml.prefs, false);
		
	
		Spinner pretest1Spinner = (Spinner) findViewById(R.id.pretest1_spinner);
		ArrayAdapter<CharSequence> cwfadapter = ArrayAdapter.createFromResource(this,
		        R.array.pretest1, android.R.layout.simple_spinner_item);
		// Specify the layout to use when the list of choices appears
		cwfadapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		// Apply the adapter to the spinner
		pretest1Spinner.setAdapter(cwfadapter);
		
		Spinner pretest2Spinner = (Spinner) findViewById(R.id.pretest2_spinner);
		ArrayAdapter<CharSequence> cwfadapter2 = ArrayAdapter.createFromResource(this,
		        R.array.pretest2, android.R.layout.simple_spinner_item);
		// Specify the layout to use when the list of choices appears
		cwfadapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		// Apply the adapter to the spinner
		pretest2Spinner.setAdapter(cwfadapter2);
		
		Spinner pretest3Spinner = (Spinner) findViewById(R.id.pretest3_spinner);
		ArrayAdapter<CharSequence> cwfadapter3 = ArrayAdapter.createFromResource(this,
		        R.array.pretest3, android.R.layout.simple_spinner_item);
		// Specify the layout to use when the list of choices appears
		cwfadapter3.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		// Apply the adapter to the spinner
		pretest3Spinner.setAdapter(cwfadapter3);
		
		
		Button counselling = (Button) findViewById(R.id.submit_btn);
		counselling.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				//startActivity(new Intent(PreTestActivity.this, CounsellingHomeActivity.class));
			}
		});
		
		
	}

}
