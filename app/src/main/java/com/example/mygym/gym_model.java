package com.example.mygym;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class gym_model {
    String name;
    String location;
    String link;
    Map<String,String> list=new HashMap<>();
    String about;
    String email;
List<Client>user=new ArrayList<>();

    public gym_model(String name, String location, String link, Map<String,String> list, String about, String email, List<Client> user) {
        this.name = name;
        this.location = location;
        this.link = link;
        this.list = list;
        this.about = about;
        this.email = email;
        this.user = user;
    }

    public List<Client> getUser() {
        return user;
    }

    public void setUser(List<Client> ulist) {
        this.user = ulist;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public gym_model(String name, String location, String link, Map<String,String> list, String about, String email) {
        this.name = name;
        this.location = location;
        this.link = link;
        this.list = list;
        this.about = about;
        this.email = email;
    }

    public gym_model() {
    }

    public gym_model(String name, String location, String link, Map<String,String> list, String about) {
        this.name = name;
        this.location = location;
        this.link = link;
        this.list = list;
        this.about = about;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public Map<String,String> getList() {
        return list;
    }

    public void setList(Map<String,String> list) {
        this.list = list;
    }

    public String getAbout() {
        return about;
    }

    public void setAbout(String about) {
        this.about = about;
    }
}
