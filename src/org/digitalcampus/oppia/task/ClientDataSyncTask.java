package org.digitalcampus.oppia.task;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.HTTP;
import org.codehaus.jackson.map.ObjectMapper;
import org.digitalcampus.oppia.application.DatabaseManager;
import org.digitalcampus.oppia.application.DbHelper;
import org.digitalcampus.oppia.listener.ClientDataSyncListener;
import org.digitalcampus.oppia.model.Client;
import org.digitalcampus.oppia.model.ClientDTO;
import org.digitalcampus.oppia.utils.HTTPConnectionUtils;
import org.ujjwal.saathi.oppia.mobile.learning.R;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

public class ClientDataSyncTask extends AsyncTask<Payload, Object, Payload> {
    public static final String TAG = ClientDataSyncTask.class.getSimpleName();

    private Context ctx;
    private ClientDataSyncListener clientDataSyncListener;
    private SharedPreferences prefs;

    public ClientDataSyncTask(Context c) {
        this.ctx = c;
    }

    @Override
    protected Payload doInBackground(Payload... params) {

        prefs = PreferenceManager.getDefaultSharedPreferences(ctx);
        HTTPConnectionUtils client = new HTTPConnectionUtils(ctx);
        ClientDTO clientDTO = new ClientDTO(); // POJO object used to exchange data
        Payload payload = params[0];
        ArrayList<Client> clients = (ArrayList<Client>) payload.getData();
        String url = client.getFullURLClientSync(payload.getUrl());
        HttpPost httpPost = new HttpPost(url);
        ObjectMapper mapper = new ObjectMapper(); // using jackson to create json and exchange data
        long lastRun = prefs.getLong("lastClientDataSync", 0);
        try {
            clientDTO.getClients().addAll(clients);
            clientDTO.setPreviousSyncTime(lastRun);
            publishProgress(ctx.getString(R.string.client_data_sync));

            String str = mapper.writeValueAsString(clientDTO);
            StringEntity se = new StringEntity( str,"utf8");

            se.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
            httpPost.addHeader(client.getAuthHeader()); // authorization
            httpPost.setEntity(se);
            // send request
            HttpResponse response = client.execute(httpPost);

            // read response
            InputStream content = response.getEntity().getContent();
            BufferedReader buffer = new BufferedReader(new InputStreamReader(content), 4096);
            String responseStr = "";
            String s = "";

            while ((s = buffer.readLine()) != null) {
                responseStr += s;
            }
            Log.d("jsonFromServer", responseStr);

            switch (response.getStatusLine().getStatusCode()){
                case 400: // unauthorised
                    payload.setResult(false);
                    payload.setResultResponse(ctx.getString(R.string.error_login));
                    break;
                case 201: // logged in
                    ClientDTO clientDTO2 = mapper.readValue(responseStr,ClientDTO.class);
                    DbHelper db = new DbHelper(ctx);

                    for (Client client1: clients) {
                        db.deleteUnregisteredClients(client1.getClientId());
                    }
                    ArrayList<Client> clients2 = clientDTO2.getClients();
                    db.addOrUpdateClient(clients2);
                    DatabaseManager.getInstance().closeDatabase();
//                    correctly setting the previous sync time
                    long now = System.currentTimeMillis()/1000;
                    SharedPreferences.Editor editor = prefs.edit();
                    editor.putLong("lastClientDataSync", now);
                    editor.commit();
                    break;
                default:
                    payload.setResult(false);
                    payload.setResultResponse(ctx.getString(R.string.error_connection));
            }

        } catch (UnsupportedEncodingException e) {
            payload.setResult(false);
            payload.setResultResponse(ctx.getString(R.string.error_connection));
        } catch (ClientProtocolException e) {
            payload.setResult(false);
            payload.setResultResponse(ctx.getString(R.string.error_connection));
        } catch (IOException e) {
            payload.setResult(false);
            payload.setResultResponse(ctx.getString(R.string.error_connection));
        }
//        catch (JSONException e) {
//            if(!MobileLearning.DEVELOPER_MODE){
//                BugSenseHandler.sendException(e);
//            } else {
//                e.printStackTrace();
//            }
//            payload.setResult(false);
//            payload.setResultResponse(ctx.getString(R.string.error_processing_response));
//        }
        finally {

        }
        return payload;
    }

    @Override
    protected void onPostExecute(Payload response) {
        synchronized (this) {
            if (clientDataSyncListener != null) {
                clientDataSyncListener.clientDataSyncComplete(response);
            }
        }
    }

    public void setClientDataSyncListener(ClientDataSyncListener srl) {
        synchronized (this) {
            clientDataSyncListener = srl;
        }
    }

//    // Method to stop the service
//    public void stopService(View view) {
//        stopService(new Intent(this, SyncDataService.class));
//    }
}
