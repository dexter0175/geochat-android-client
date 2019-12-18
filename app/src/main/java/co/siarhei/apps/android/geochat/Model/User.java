


package co.siarhei.apps.android.geochat.Model;

import android.location.Location;

import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;

import co.siarhei.apps.android.geochat.Location.UserLocation;

public class User {
    int id;
    String fireID;
    public String first_name;
    public String last_name;
    public String email_address;
    @ServerTimestamp
    Date created_at;
    @ServerTimestamp
    Date updated_at;
    UserLocation location;

    public User() {

    }

    public UserLocation getLocation() {
        return location;
    }

    public void setLocation(UserLocation location) {
        this.location = location;
    }
    public Date getCreated_at() {
        return created_at;
    }

    public void setCreated_at(Date created_at) {
        this.created_at = created_at;
    }

    public Date getUpdated_at() {
        return updated_at;
    }

    public void setUpdated_at(Date updated_at) {
        this.updated_at = updated_at;
    }

    public String getFirst_name() {
        return first_name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setFirst_name(String first_name) {
        this.first_name = first_name;
    }

    public String getLast_name() {
        return last_name;
    }

    public void setLast_name(String last_name) {
        this.last_name = last_name;
    }

    public String getEmail_address() {
        return email_address;
    }

    public void setEmail_address(String email_address) {
        this.email_address = email_address;
    }

    public String getFireID() {
        return fireID;
    }

    public void setFireID(String fireID) {
        this.fireID = fireID;
    }

    public User(String first_name, String last_name, String email_address) {
        this.first_name = first_name;
        this.last_name = last_name;
        this.email_address = email_address;

    }
}
