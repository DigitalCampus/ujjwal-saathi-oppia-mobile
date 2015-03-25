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

package org.digitalcampus.oppia.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.BitmapDrawable;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import org.digitalcampus.oppia.model.Client;
import org.digitalcampus.oppia.model.SearchOutput;
import org.digitalcampus.oppia.model.SearchResult;
import org.digitalcampus.oppia.utils.ImageUtils;
import org.ujjwal.saathi.oppia.mobile.learning.R;

import java.util.ArrayList;
import java.util.Locale;

public class SearchResultsListAdapter  extends ArrayAdapter<SearchOutput>{

	public static final String TAG = SearchResultsListAdapter.class.getSimpleName();
	
	private final Context ctx;
	private final ArrayList<SearchOutput> searchResultList;
	private SharedPreferences prefs;
	
	public SearchResultsListAdapter(Activity context, ArrayList<SearchOutput> searchResultList) {
		super(context, R.layout.search_results_row, searchResultList);
		this.ctx = context;
		this.searchResultList = searchResultList;
		prefs = PreferenceManager.getDefaultSharedPreferences(ctx);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
        View rowView;
        SearchOutput sr = searchResultList.get(position);
        // identify which instance of SearchOutput is in context
        if (sr.getClass().equals(Client.class)) {
            Client client = (Client)sr;
            LayoutInflater inflater = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            rowView = inflater.inflate(R.layout.client_list_row, parent, false);
            TextView clientTitle = (TextView) rowView.findViewById(R.id.client_title);
            String title = client.getClientName();
            clientTitle.setText(title);
            rowView.setTag(R.id.TAG_CLIENT,client);
        } else {
            SearchResult searchResult = (SearchResult)sr;
            LayoutInflater inflater = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            rowView = inflater.inflate(R.layout.search_results_row, parent, false);
            rowView.setTag(sr);

            TextView activityTitle = (TextView) rowView.findViewById(R.id.activity_title);
            TextView sectionTitle = (TextView) rowView.findViewById(R.id.section_title);
            TextView courseTitle = (TextView) rowView.findViewById(R.id.course_title);

            String cTitle = searchResult.getCourse().getTitle(prefs.getString("prefLanguage", Locale.getDefault().getLanguage()));
            String sTitle = searchResult.getSection().getTitle(prefs.getString("prefLanguage", Locale.getDefault().getLanguage()));
            String aTitle = searchResult.getActivity().getTitle(prefs.getString("prefLanguage", Locale.getDefault().getLanguage()));

            activityTitle.setText(aTitle);
            sectionTitle.setText(sTitle);
            courseTitle.setText(cTitle);

            rowView.setTag(R.id.TAG_COURSE,searchResult.getCourse());
            rowView.setTag(R.id.TAG_ACTIVITY_DIGEST,searchResult.getActivity().getDigest());

            if(searchResult.getCourse().getImageFile() != null){
                ImageView iv = (ImageView) rowView.findViewById(R.id.course_image);
                BitmapDrawable bm = ImageUtils.LoadBMPsdcard(searchResult.getCourse().getImageFile(), ctx.getResources(), R.drawable.ujjwal_logo);
                iv.setImageDrawable(bm);
            }
        }
	    return rowView;
	}
}
