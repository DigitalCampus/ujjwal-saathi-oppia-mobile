package org.digitalcampus.oppia.task;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;

import com.bugsense.trace.BugSenseHandler;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.HTTP;
import org.codehaus.jackson.map.ObjectMapper;
import org.digitalcampus.oppia.application.DatabaseManager;
import org.digitalcampus.oppia.application.DbHelper;
import org.digitalcampus.oppia.application.MobileLearning;
import org.digitalcampus.oppia.listener.ClientDataSyncListener;
import org.digitalcampus.oppia.model.Client;
import org.digitalcampus.oppia.model.ClientDTO;
import org.digitalcampus.oppia.utils.HTTPConnectionUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.ujjwal.saathi.oppia.mobile.learning.R;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

/**
 * Created by ronak on 15/2/15.
 */
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
        ClientDTO clientDTO = new ClientDTO();
        Payload payload = params[0];
        ArrayList<Client> clients = (ArrayList<Client>) payload.getData();
        String url = "http://183.82.96.201:8000/api/v1/client/";
        JSONObject json = new JSONObject();

        HttpPost httpPost = new HttpPost(url);
        try {
            long lastRun = prefs.getLong("lastClientDataSync", 0);
            clientDTO.getClients().addAll(clients);
            clientDTO.setPreviousSyncTime(lastRun);
            publishProgress(ctx.getString(R.string.client_data_sync));
            // add post params , previous sync time and list of clients to be synced/updated
            ObjectMapper mapper = new ObjectMapper();
            json.put("clients", mapper.writeValueAsString(clientDTO));
            json.put("previousSyncTime", lastRun);
            StringEntity se = new StringEntity( json.toString(),"utf8");
            se.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
            httpPost.setEntity(se);
            httpPost.addHeader(client.getAuthHeader());
//            Log.d("json1",mapper.writeValueAsString(clientDTO));
//            Log.d("json2",json.toString());
            HttpResponse response = client.execute(httpPost);

            // read response
            InputStream content = response.getEntity().getContent();
            BufferedReader buffer = new BufferedReader(new InputStreamReader(content), 4096);
            String responseStr = "";
            String s = "";

            while ((s = buffer.readLine()) != null) {
                responseStr += s;
            }

            // check status code
            switch (response.getStatusLine().getStatusCode()){
                case 400: // unauthorised
                    payload.setResult(false);
                    payload.setResultResponse(ctx.getString(R.string.error_login));
                    break;
                case 201: // logged in
                    JSONObject jsonResp = new JSONObject(responseStr);
//                    u.setApiKey(jsonResp.getString("api_key"));
//                    u.setPassword(u.getPassword());
//                    u.setPasswordEncrypted();
//                    u.setFirstname(jsonResp.getString("first_name"));
//                    u.setLastname(jsonResp.getString("last_name"));

                    DbHelper db = new DbHelper(ctx);
//                    db.addOrUpdateUser(u);
                    DatabaseManager.getInstance().closeDatabase();
                    payload.setResult(true);
                    payload.setResultResponse(ctx.getString(R.string.login_complete));
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
        } catch (JSONException e) {
            if(!MobileLearning.DEVELOPER_MODE){
                BugSenseHandler.sendException(e);
            } else {
                e.printStackTrace();
            }
            payload.setResult(false);
            payload.setResultResponse(ctx.getString(R.string.error_processing_response));
        } finally {

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
}
