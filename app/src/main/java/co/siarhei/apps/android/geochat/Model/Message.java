

package co.siarhei.apps.android.geochat.Model;


import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;

public class Message {

    private int id;
    public double origin_lat;
    public double origin_long;
    private String user_id = "anonym";
    public boolean is_anonymaus = true;
    private String message_type;
    public String contents;
    @ServerTimestamp
    public Date created_at;
    @ServerTimestamp
    public Date updated_at;
    public String sender;


    public Message (String _contents){
        contents = _contents;
    }
    public Message (){

    };

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public double getOrigin_lat() {
        return origin_lat;
    }

    public void setOrigin_lat(double origin_lat) {
        this.origin_lat = origin_lat;
    }

    public double getOrigin_long() {
        return origin_long;
    }

    public void setOrigin_long(double origin_long) {
        this.origin_long = origin_long;
    }



    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }



    public boolean getIs_anonymaus() {
        return is_anonymaus;
    }

    public void setIs_anonymaus(boolean is_anonymaus) {
        this.is_anonymaus = is_anonymaus;
    }

    public String getMessage_type() {
        return message_type;
    }

    public void setMessage_type(String message_type) {
        this.message_type = message_type;
    }

    public String getContents() {
        return contents;
    }

    public void setContents(String contents) {
        this.contents = contents;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String name) {
        this.sender = name;
    }




}
