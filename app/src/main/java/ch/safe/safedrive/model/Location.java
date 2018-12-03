package ch.safe.safedrive.model;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

import java.util.HashMap;
import java.util.Map;

@IgnoreExtraProperties
public class Location {

    private String id;
    private String name;
    private double longitude, latitude;
    private Boolean depart, destination;

    public Location(){

    }

    public Location (String id, String name, double latitude, double longitude, Boolean depart, Boolean destination) {
        this.id = id;
        this.name = name;
        this.longitude = longitude;
        this.latitude = latitude;
        this.depart = depart;
        this.destination = destination;
    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("id", id);
        result.put("name", name);
        result.put("longitude", longitude);
        result.put("latitude", latitude);
        result.put("depart", depart);
        result.put("destination", destination);

        return result;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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
        return depart;
    }

    public void setDepart(Boolean depart) {
        depart = depart;
    }

    public Boolean getDestination() {
        return destination;
    }

    public void setDestination(Boolean destination) {
        destination = destination;
    }
}
