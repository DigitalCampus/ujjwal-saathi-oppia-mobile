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

import java.io.File;
import java.util.ArrayList;
import java.util.Locale;

import org.digitalcampus.oppia.activity.PrefsActivity;
import org.digitalcampus.oppia.model.SearchResult;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
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

    static class SearchResultsViewHolder{
        TextView activityTitle;
        TextView sectionTitle;
        TextView courseTitle;
        ImageView courseImage;
    }

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

        SearchResultsViewHolder viewHolder;
        SearchOutput sr = searchResultList.get(position);
        if (sr.getClass().equals(Client.class)) {
            Client client = (Client)sr;
            LayoutInflater inflater = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.client_list_row, parent, false);
            TextView clientTitle = (TextView) convertView.findViewById(R.id.client_title);
            String title = client.getClientName();
            clientTitle.setText(title);
            convertView.setTag(R.id.TAG_CLIENT,client);
        } else {
	        if (convertView == null) {
	            LayoutInflater inflater = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	            convertView  = inflater.inflate(R.layout.search_results_row, parent, false);
	            viewHolder = new SearchResultsViewHolder();
	            viewHolder.activityTitle = (TextView) convertView.findViewById(R.id.activity_title);
	            viewHolder.sectionTitle = (TextView) convertView.findViewById(R.id.section_title);
	            viewHolder.courseTitle = (TextView) convertView.findViewById(R.id.course_title);
	            viewHolder.courseImage = (ImageView) convertView.findViewById(R.id.course_image);
	            convertView.setTag(viewHolder);
	        }
	        else{
	            viewHolder = (SearchResultsViewHolder) convertView.getTag();
	        }
	
	        SearchResult searchResult = (SearchResult)sr;
	
		    String cTitle = searchResult.getCourse().getTitle(prefs.getString(PrefsActivity.PREF_LANGUAGE, Locale.getDefault().getLanguage()));
		    String sTitle = searchResult.getSection().getTitle(prefs.getString(PrefsActivity.PREF_LANGUAGE, Locale.getDefault().getLanguage()));
		    String aTitle = searchResult.getActivity().getTitle(prefs.getString(PrefsActivity.PREF_LANGUAGE, Locale.getDefault().getLanguage()));
	
		    // app crashed here
	        viewHolder.activityTitle.setText(aTitle);
	        viewHolder.sectionTitle.setText(sTitle);
	        viewHolder.courseTitle.setText(cTitle);
	
	        convertView.setTag(R.id.TAG_COURSE,searchResult.getCourse());
	        convertView.setTag(R.id.TAG_ACTIVITY_DIGEST,searchResult.getActivity().getDigest());
	
	        if(searchResult.getCourse().getImageFile() != null){
	            String image = searchResult.getCourse().getImageFileFromRoot();
	            Picasso.with(ctx).load(new File(image))
	                    .placeholder(R.drawable.ujjwal_logo)
	                    .into(viewHolder.courseImage);
	        }
        }
	    return convertView;
	}
}
