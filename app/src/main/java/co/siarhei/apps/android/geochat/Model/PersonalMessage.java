/*
 * PersonalMessage
 * D:/course/geochat-client-master/app/src/main/java/co/siarhei/apps/android/geochat/Model/PersonalMessage.java
 *
 * Project: geochat-client-master
 * Module: app
 * Last Modified: 18.12.19 15:10 <dexte>
 * Last Compilation: 18.12.19 15:10
 *
 * Copyright (c) 2019. Martin David Shaw. All rights reserved.
 */

package co.siarhei.apps.android.geochat.Model;


import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;

public class PersonalMessage {

    private String contents;
    private String from_id ;
    public String to_id;
    @ServerTimestamp
    public Date created_at;
    @ServerTimestamp
    public Date updated_at;


    public PersonalMessage() {

    }

    public PersonalMessage(String contents,  String from_id, String to_id, Date created_at, Date updated_at) {
        this.contents = contents;
        this.from_id = from_id;
        this.to_id = to_id;
        this.created_at = created_at;
        this.updated_at = updated_at;
    }

    public String getContents() {
        return contents;
    }

    public void setContents(String contents) {
        this.contents = contents;
    }

    public String getFrom_id() {
        return from_id;
    }

    public void setFrom_id(String from_id) {
        this.from_id = from_id;
    }

    public String isTo_id() {
        return to_id;
    }

    public void setTo_id(String to_id) {
        this.to_id = to_id;
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
}
