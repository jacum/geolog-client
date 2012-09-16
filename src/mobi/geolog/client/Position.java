package mobi.geolog.client;

import com.google.android.maps.Overlay;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.Date;

/**
 * @author timur
 */
public class Position implements Externalizable {

    private String id;
    private Date lastSeen;
    private String imei;
    private double latitude;
    private double longtitude;
    private int bearing;
    private double speed;

    public String getImei() {
        return imei;
    }

    public void setImei(String imei) {
        this.imei = imei;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongtitude() {
        return longtitude;
    }

    public void setLongtitude(double longtitude) {
        this.longtitude = longtitude;
    }

    public int getBearing() {
        return bearing;
    }

    public void setBearing(int bearing) {
        this.bearing = bearing;
    }

    public double getSpeed() {
        return speed;
    }

    public void setSpeed(double speed) {
        this.speed = speed;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Date getLastSeen() {
        return lastSeen;
    }

    public void setLastSeen(Date lastSeen) {
        this.lastSeen = lastSeen;
    }

    @Override
    public void readExternal(ObjectInput input) throws IOException, ClassNotFoundException {
        id = input.readUTF();
        imei = input.readUTF();
        latitude = input.readDouble();
        longtitude = input.readDouble();
        bearing = input.readInt();
        speed = input.readDouble();
//        lastSeen = new Date(input.readLong());
    }

    @Override
    public void writeExternal(ObjectOutput output) throws IOException {
        output.writeUTF(safeUtf(id));
        output.writeUTF(safeUtf(imei));
        output.writeDouble(latitude);
        output.writeDouble(longtitude);
        output.writeInt(bearing);
        output.writeDouble(speed);
//        output.writeLong(lastSeen.getTime());
    }

    private String safeUtf(String v) {
        return v == null ? "" : v;
    }

    @Override
    public String toString() {
        return "Position{" +
                "id='" + id + '\'' +
//                ", lastSeen=" + lastSeen +
                ", imei='" + imei + '\'' +
                ", latitude=" + latitude +
                ", longtitude=" + longtitude +
                ", bearing=" + bearing +
                ", speed=" + speed +
                '}';
    }
}
