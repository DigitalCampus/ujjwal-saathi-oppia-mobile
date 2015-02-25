package org.digitalcampus.oppia.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import org.digitalcampus.oppia.model.Client;
import org.ujjwal.saathi.oppia.mobile.learning.R;

import java.util.ArrayList;

/**
 * Created by raunak on 2/2/15.
 */
public class ClientListAdapter extends ArrayAdapter {
    public static final String TAG = ClientListAdapter.class.getSimpleName();

    private final Context ctx;
    private final ArrayList<Client> clientList;

    public ClientListAdapter(Activity context, ArrayList<Client> clientList) {
        super(context, R.layout.client_list_row, clientList);
        // initializing the data members
        this.ctx = context;
        this.clientList = clientList;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        // populating the adapter
        LayoutInflater inflater = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.client_list_row, parent, false);
        TextView clientTitle = (TextView) rowView.findViewById(R.id.client_title);
        Client s = clientList.get(position);
        String title = s.getClientName();
        clientTitle.setText(title);
        rowView.setTag(s);
        return rowView;
    }
}
