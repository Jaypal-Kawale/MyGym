package com.example.mygym;

import com.google.firebase.iid.FirebaseInstanceIdService;

public class refreshtoken  {
   String token;

    public refreshtoken(String token) {
        this.token = token;
    }

    public refreshtoken() {
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
