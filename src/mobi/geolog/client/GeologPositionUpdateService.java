package mobi.geolog.client;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.IBinder;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * @author timur
 */
public class GeologPositionUpdateService extends Service {

    public final static String POSITION_UPDATED = "mobi.geolog.POSITION_UPDATED";
    public static final String LAST_POSITION = "POSITION";


    private AlarmManager alarms;
    private PendingIntent alarmIntent;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        alarms = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intentToFire = new Intent(GeologPositionRefreshReceiver.MOBI_GEOLOG_REFRESH_POSITION);
        alarmIntent = PendingIntent.getBroadcast(this, 0, intentToFire, 0);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Context context = getApplicationContext();
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);

        int updateFreq = 0;
        try {
            updateFreq = Integer.parseInt(prefs.getString(GeologPreferences.FREQUENCY_PREF, "0"));
        } catch (Exception e) {
            //
        }

        String imei = getImei();
        if (updateFreq > 0 && imei != null && !"".equals(imei) ) {
            int alarmType = AlarmManager.ELAPSED_REALTIME_WAKEUP;
            long timeToRefresh = SystemClock.elapsedRealtime() + updateFreq * 60 * 1000;
            alarms.setRepeating(alarmType, timeToRefresh, updateFreq * 60 * 1000, alarmIntent);
        }

        refreshPosition();

        return Service.START_NOT_STICKY;
    }

    private String getServerUrl() {
        Context context = getApplicationContext();
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);

        return "http://" + prefs.getString(GeologPreferences.HOSTNAME_PREF, "0") + ":" + prefs.getString(GeologPreferences.PORT_PREF, "0");

    }

    private String getImei() {
        Context context = getApplicationContext();
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);

        return prefs.getString(GeologPreferences.IMEI_PREF, "");

    }

    private PositionUpdateTask lastLookup;

    private void refreshPosition() {
        if (lastLookup == null ||
                lastLookup.getStatus().equals(AsyncTask.Status.FINISHED)) {
            lastLookup = new PositionUpdateTask();
            lastLookup.execute((Void[]) null);
        }
    }

    private class PositionUpdateTask extends AsyncTask<Void, Position, Void> {

        private String errorMessage = null;
        private Position position = null;

        public String readServerResponse(String uri) {
            StringBuilder builder = new StringBuilder();
            HttpClient client = new DefaultHttpClient();

            final String requestUrl = getServerUrl() + uri;
            HttpGet httpGet = new HttpGet(requestUrl);
            try {
                HttpResponse response = client.execute(httpGet);
                StatusLine statusLine = response.getStatusLine();
                int statusCode = statusLine.getStatusCode();
                if (statusCode == 200) {
                    Log.i(PositionUpdateTask.class.toString(), "Got response from server");
                    HttpEntity entity = response.getEntity();
                    InputStream content = entity.getContent();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(content));
                    String line;
                    while ((line = reader.readLine()) != null) {
                        builder.append(line);
                    }
                } else {
                    Log.e(PositionUpdateTask.class.toString(), "Error accessing server, code " + statusCode);
                    errorMessage = "Error accessing server, code " + statusCode;
                }
            } catch (ClientProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return builder.toString();
        }

        public boolean isOnline() {
            ConnectivityManager cm =
                    (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo netInfo = cm.getActiveNetworkInfo();
            if (netInfo != null && netInfo.isConnectedOrConnecting()) {
                return true;
            }
            return false;
        }

        @Override
        protected Void doInBackground(Void... params) {
            Log.i(PositionUpdateTask.class.toString(), "Position update initiated");
            errorMessage = null;
            if (!isOnline()) {
                Log.i(PositionUpdateTask.class.toString(), "No connectivity at this moment, skipping refresh");
            } else {
                final String imei = getImei();
                String response = readServerResponse("/imei/" + imei);
                try {
                    JSONObject positionJson = new JSONObject(response);
                    position = new Position();
                    position.setId(positionJson.getString("_id"));
                    position.setImei(imei);
                    position.setLatitude(positionJson.getDouble("latitude"));
                    position.setLongtitude(positionJson.getDouble("longtitude"));
                    position.setBearing(positionJson.getInt("bearing"));
                    position.setSpeed(positionJson.getDouble("speed"));

                    Log.i(PositionUpdateTask.class.toString(), "Parsed position: " + position);
    //                position.setLastSeen(positionJson.getDouble("last_seen")); // todo parse ISO date

                } catch (JSONException e) {
                    Log.e(PositionUpdateTask.class.toString(), "Can't parse server response", e);
                    errorMessage = "Can't parse server response: " + e.getMessage();
                }
            }
            return null;
        }



        @Override
        protected void onPostExecute(Void result) {
            if (errorMessage == null) {
                if (position != null) {
                    final Intent intent = new Intent(POSITION_UPDATED);
                    intent.putExtra(GeologPositionUpdateService.LAST_POSITION, position);
                    sendBroadcast(intent);
                }
            } else {
                Toast.makeText(getApplicationContext(), errorMessage, Toast.LENGTH_LONG).show();
            }
            stopSelf();
        }
    }

}


