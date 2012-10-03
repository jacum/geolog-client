package mobi.geolog.client;

import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.*;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;

public class GeologMain extends MapActivity {

    private static MapView mapView;


    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        mapView = (MapView) findViewById(R.id.mapView);
        mapView.setBuiltInZoomControls(true);


        startRefreshPositionService();

    }

    private void startRefreshPositionService() {
        startService(new Intent(this, GeologPositionUpdateService.class));
    }

    public static void updateCurrentPosition(Position p) {
        mapView.getOverlays().clear();
        PositionOverlay po = new PositionOverlay(p);
        mapView.getOverlays().add(po);
        mapView.invalidate();

        final GeoPoint point = po.getPoint();
        if (point != null) { // application crashes here with NPE... cause unknown but let the check be here
            MapController mapControl = mapView.getController();
            mapControl.setZoom(16);
            mapControl.animateTo(point);
        }

    }


    protected boolean isRouteDisplayed() {
        return false;
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.actionbar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                return false;
            case R.id.menu_refresh:
                startRefreshPositionService();
                return true;
            case R.id.menu_trips:
                showDialog(0);
                return true;
            case R.id.menu_preferences:
                Intent i = new Intent("mobi.geolog.AppPreferenceActivity");
                startActivity(i);
                return true;
        }
        return true;
    }

    @Override
    protected android.app.Dialog onCreateDialog(int id, Bundle bundle) {
        final CharSequence[] items = {"Red", "Green", "Blue"};

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Pick a trip");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int item) {
                NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                // Create Notifcation
                Notification notification = new Notification(R.drawable.ic_launcher,
                        "A new trip selected.", System.currentTimeMillis());

                // Cancel the notification after its selected
                notification.flags |= Notification.FLAG_AUTO_CANCEL;

                notification.number += 1;

                // Specify the called Activity
                Intent intent = new Intent(getApplicationContext(), GeologMain.class);

                PendingIntent activity = PendingIntent.getActivity(getApplicationContext(), 0, intent, 0);
                notification.setLatestEventInfo(getApplicationContext(), "This is the trip title", "This is the trip text", activity);
                notificationManager.notify(0, notification);
            }
        });
        AlertDialog alert = builder.create();
        return alert;
    }

}
