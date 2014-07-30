package org.digitalcampus.oppia.activity;

import org.digitalcampus.oppia.adapter.SectionListAdapter;
import org.digitalcampus.oppia.model.Course;
import org.digitalcampus.oppia.model.Section;
import org.ujjwal.saathi.oppia.mobile.learning.R;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

public class CounsellingHomeActivity extends AppActivity{
	
	public static final String TAG = CounsellingHomeActivity.class.getSimpleName();
	private SharedPreferences prefs;
	private AlertDialog aDialog;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_counselling_home);
		
		prefs = PreferenceManager.getDefaultSharedPreferences(this);
		PreferenceManager.setDefaultValues(this, R.xml.prefs, false);
		
	
		
		Button counselling = (Button) findViewById(R.id.submit3_btn);
		counselling.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				aDialog = new AlertDialog.Builder(CounsellingHomeActivity.this).create();
				aDialog.setCancelable(true);
				aDialog.setTitle("Thank you for choosing Spacing");
				aDialog.setMessage("This section has 3 parts:\n\n1. Pretest\n2. Method related information\n3. Post test");

				aDialog.setButton(DialogInterface.BUTTON_NEGATIVE, (CharSequence) "Continue",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int which) {
								Intent intent = new Intent(CounsellingHomeActivity.this, PreTestActivity.class);
								startActivity(intent);
							}
						});
				aDialog.show();
				
				
				//startActivity(new Intent(CounsellingHomeActivity.this, CounsellingHomeActivity.class));
			}
		});
		
		
	}

}
