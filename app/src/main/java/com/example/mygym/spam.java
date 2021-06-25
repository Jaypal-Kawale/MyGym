package com.example.mygym;

public class spam {
    String userposted;
    String hospital;
    String userobjection;

    public spam() {
    }

    public spam(String userposted, String hospital, String userobjection) {
        this.userposted = userposted;
        this.hospital = hospital;
        this.userobjection = userobjection;
    }

    public String getUserposted() {
        return userposted;
    }

    public void setUserposted(String userposted) {
        this.userposted = userposted;
    }

    public String getHospital() {
        return hospital;
    }

    public void setHospital(String hospital) {
        this.hospital = hospital;
    }

    public String getUserobjection() {
        return userobjection;
    }

    public void setUserobjection(String userobjection) {
        this.userobjection = userobjection;
    }
}
