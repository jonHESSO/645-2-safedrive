package ch.safedrive.safedrive.model;

import com.google.firebase.database.Exclude;

import java.sql.Time;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class Request {

    private String id;
    private Date date;
    private Time time;
    private String locationTo, locationFrom;
    private User user;
    private String plate, idPlatePic;

    public Request() {

    }

    public Request(String id, Date date, Time time, String locationTo, String locationFrom, User user, String plate, String idPlatePic) {
        this.id = id;
        this.date = date;
        this.time = time;
        this.locationTo = locationTo;
        this.locationFrom = locationFrom;
        this.user = user;
        this.plate = plate;
        this.idPlatePic = idPlatePic;
    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("id", id);
        result.put("date", date);
        result.put("time", time);
        result.put("locationTo", locationTo);
        result.put("locationFrom", locationFrom);
        result.put("user", user);
        result.put("plate", plate);
        result.put("idPlatePic", idPlatePic);

        return result;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Time getTime() {
        return time;
    }

    public void setTime(Time time) {
        this.time = time;
    }

    public String getLocationTo() {
        return locationTo;
    }

    public void setLocationTo(String locationTo) {
        this.locationTo = locationTo;
    }

    public String getLocationFrom() {
        return locationFrom;
    }

    public void setLocationFrom(String locationFrom) {
        this.locationFrom = locationFrom;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getPlate() {
        return plate;
    }

    public void setPlate(String plate) {
        this.plate = plate;
    }

    public String getIdPlatePic() {
        return idPlatePic;
    }

    public void setIdPlatePic(String urlPlatePic) {
        this.idPlatePic = idPlatePic;
    }
}
