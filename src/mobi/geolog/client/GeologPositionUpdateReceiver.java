package mobi.geolog.client;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import com.google.android.maps.MapView;

/**
 * @author timur
 */
public class GeologPositionUpdateReceiver extends BroadcastReceiver {


    public void onReceive(Context context, Intent intent) {
        Position p = (Position) intent.getSerializableExtra(GeologPositionUpdateService.LAST_POSITION);
        GeologMain.updateCurrentPosition(p);
    }
}
