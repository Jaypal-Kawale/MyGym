package com.example.mygym;

public class User {
    public String username;
    public String email;
    public String fullname;
    public String mob;
    public String link;

    public User() {
    }

    public User(String username, String email, String fullname, String mob, String link) {
        this.username = username;
        this.email = email;
        this.fullname = fullname;
        this.mob = mob;
        this.link = link;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public String getMob() {
        return mob;
    }

    public void setMob(String mob) {
        this.mob = mob;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }
}
