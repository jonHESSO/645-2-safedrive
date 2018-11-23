package ch.safedrive.safedrive.model;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

import java.util.HashMap;
import java.util.Map;

@IgnoreExtraProperties
public class Location {

    private String name;
    private double longitude, latitude;
    private Boolean isDepart, isDestination;

    public Location(){

    }

    public Location (String name, double latitude, double longitude, Boolean isDepart, Boolean isDestination) {
        this.name = name;
        this.longitude = longitude;
        this.latitude = latitude;
        this.isDepart = isDepart;
        this.isDestination = isDestination;
    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("name", name);
        result.put("longitude", longitude);
        result.put("latitude", latitude);
        result.put("isDepart", isDepart);
        result.put("isDestination", isDestination);

        return result;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public Boolean getDepart() {
        return isDepart;
    }

    public void setDepart(Boolean depart) {
        isDepart = depart;
    }

    public Boolean getDestination() {
        return isDestination;
    }

    public void setDestination(Boolean destination) {
        isDestination = destination;
    }
}
