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
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;


public class RoutingActivity  extends AppActivity {

	public static final String TAG = RoutingActivity.class.getSimpleName();
	
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_routing);
		
		Button mLearning = (Button) findViewById(R.id.button_learning);
		mLearning.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				Toast toast = Toast.makeText(RoutingActivity.this, "Not yet implemented", Toast.LENGTH_SHORT);
				toast.show();
			}
		});
		
		
		Button counselling = (Button) findViewById(R.id.button_counselling);
		counselling.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				startActivity(new Intent(RoutingActivity.this, OppiaMobileActivity.class));
			}
		});
		
		
	}
}
