package mobi.geolog.client;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * @author timur
 */
public class GeologPositionRefreshReceiver extends BroadcastReceiver {

    public static final String  MOBI_GEOLOG_REFRESH_POSITION = "mobi.geolog.REFRESH_POSITION";

    @Override
    public void onReceive(Context context, Intent intent) {
        Intent startIntent = new Intent(context, GeologPositionUpdateService.class);
        context.startService(startIntent);
    }
}
