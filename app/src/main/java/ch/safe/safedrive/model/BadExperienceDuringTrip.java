package ch.safe.safedrive.model;

import com.google.firebase.database.Exclude;

import java.util.HashMap;
import java.util.Map;

public class BadExperienceDuringTrip {

    String idRequest;
    String testimony;


    String user;

    public BadExperienceDuringTrip(){

    }

    public BadExperienceDuringTrip(String idRequest, String testimony, String user){
        this.idRequest = idRequest;
        this.testimony = testimony;
        this.user = user;
    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();

        result.put("idRequest", idRequest);
        result.put("testimony", testimony);
        result.put("user", user);

        return result;
    }

    public String getIdRequest() {
        return idRequest;
    }

    public void setIdRequest(String idRequest) {
        this.idRequest = idRequest;
    }

    public String getTestimony() {
        return testimony;
    }

    public void setTestimony(String testimony) {
        this.testimony = testimony;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }



}
