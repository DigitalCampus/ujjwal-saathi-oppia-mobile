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

import java.util.ArrayList;

import org.digitalcampus.oppia.model.Lang;
import org.digitalcampus.oppia.utils.UIUtils;
import org.ujjwal.saathi.oppia.mobile.learning.R;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;


public class RoutingActivity  extends AppActivity {

	public static final String TAG = RoutingActivity.class.getSimpleName();
	private SharedPreferences prefs;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_routing);
		
		prefs = PreferenceManager.getDefaultSharedPreferences(this);
		PreferenceManager.setDefaultValues(this, R.xml.prefs, false);
		
		
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
	
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		UIUtils.showUserData(menu,this);
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
		Log.d(TAG,"selected:" + item.getItemId());
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
				Editor editor = prefs.edit();
				editor.putString("prefUsername", "");
				editor.putString("prefApiKey", "");
				editor.putInt("prefBadges", 0);
				editor.putInt("prefPoints", 0);
				editor.commit();

				// restart the app
				RoutingActivity.this.startActivity(new Intent(RoutingActivity.this, StartUpActivity.class));
				RoutingActivity.this.finish();

			}
		});
		builder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				return; // do nothing
			}
		});
		builder.show();
	}
}
