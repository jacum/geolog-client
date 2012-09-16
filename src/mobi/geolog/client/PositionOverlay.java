package mobi.geolog.client;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.RectF;
import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.Projection;

/**
 * @author timur
 */
public class PositionOverlay extends Overlay {

    private final Position position;
    int rad = 5;

    public PositionOverlay(Position p) {
        this.position = p;
    }


    @Override
    public void draw(Canvas canvas, MapView mapView, boolean shadow) {
        Projection projection = mapView.getProjection();

        // Create and setup your paint brush
        Paint paint = new Paint();
        paint.setARGB(250, 255, 0, 0);
        paint.setAntiAlias(true);
        paint.setFakeBoldText(true);

        GeoPoint point = new GeoPoint(intGeoValue(position.getLatitude()), intGeoValue(position.getLongtitude()));

        if (!shadow) {
            Point myPoint = new Point();
            projection.toPixels(point, myPoint);

                RectF oval = new RectF(myPoint.x - rad, myPoint.y - rad,
                        myPoint.x + rad, myPoint.y + rad);

                canvas.drawOval(oval, paint);
            }
        }

    private int intGeoValue(double v) {
        Double d = v * 1E6;
        return d.intValue();
    }
}
