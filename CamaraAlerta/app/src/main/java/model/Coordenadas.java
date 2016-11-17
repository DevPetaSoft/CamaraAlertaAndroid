package model;

/**
 * Created by Lucas on 11/15/2016.
 */
import java.io.Serializable;

public class Coordenadas implements Serializable{

    private static final long serialVersionUID = 1L;
    private int id;
    private double latitude;
    private double longitude;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

}
