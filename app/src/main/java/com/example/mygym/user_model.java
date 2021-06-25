package com.example.mygym;

import com.google.firebase.Timestamp;

public class user_model implements Comparable<user_model> {
    String name;
    float rating;
    String review;
    Timestamp timestamp;
    String hospital;
    public user_model() {
    }

    public user_model(String name, float rating, String review, Timestamp timestamp, String hospital) {
        this.name = name;
        this.rating = rating;
        this.review = review;
        this.timestamp = timestamp;
        this.hospital = hospital;
    }

    public String getHospital() {
        return hospital;
    }

    public void setHospital(String hospital) {
        this.hospital = hospital;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }

    public String getReview() {
        return review;
    }

    public void setReview(String review) {
        this.review = review;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public int compareTo(user_model user) {
        return getTimestamp().compareTo(user.getTimestamp());
    }


}

