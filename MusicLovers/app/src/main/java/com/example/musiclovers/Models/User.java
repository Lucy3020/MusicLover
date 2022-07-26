package com.example.musiclovers.Models;

import java.util.Date;

public class User {
    String _id;
    String userName;
    String email;
    String avatar;
    Date create_at;

    public User(String userName, String email, String avatar, Date create_at) {
        this.userName = userName;
        this.email = email;
        this.avatar = avatar;
        this.create_at = create_at;
    }

    public String getUserName() {
        return userName;
    }

    public String getEmail() {
        return email;
    }

    public String getAvatar() {
        return avatar;
    }

    public Date getCreate_at() {
        return create_at;
    }

    public String get_id(){
        return  _id;
    }
}
