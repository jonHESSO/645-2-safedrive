package ch.safe.safedrive.model;

import android.location.Location;

import com.google.firebase.database.Exclude;

import java.util.HashMap;
import java.util.Map;

public class Alert {

    String idRequest;
    String plateNumber;
    String userUID;
    android.location.Location location;
    Long timestamp;

    public Alert()
    {

    }

    public Alert(String idRequest, String plateNumber, String userUID, Location location, Long timestamp) {
        this.idRequest = idRequest;
        this.plateNumber = plateNumber;
        this.userUID = userUID;
        this.location = location;
        this.timestamp = timestamp;
    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();

        result.put("idRequest", idRequest);
        result.put("plateNumber", plateNumber);
        result.put("userUID",userUID);
        result.put("location",location);
        result.put("timestamp",timestamp);

        return result;
    }

    public String getIdRequest() {
        return idRequest;
    }

    public void setIdRequest(String idRequest) {
        this.idRequest = idRequest;
    }

    public String getPlateNumber() {
        return plateNumber;
    }

    public void setPlateNumber(String plateNumber) {
        this.plateNumber = plateNumber;
    }

    public String getUserUID() {
        return userUID;
    }

    public void setUserUID(String userUID) {
        this.userUID = userUID;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }
}
