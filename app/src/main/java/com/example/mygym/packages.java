package com.example.mygym;

public class packages {
    String plan;
    String validity;
    String money;

    public packages() {
    }

    public packages(String plan, String validity, String money) {
        this.plan = plan;
        this.validity = validity;
        this.money = money;
    }

    public String getPlan() {
        return plan;
    }

    public void setPlan(String plan) {
        this.plan = plan;
    }

    public String getValidity() {
        return validity;
    }

    public void setValidity(String validity) {
        this.validity = validity;
    }

    public String getMoney() {
        return money;
    }

    public void setMoney(String money) {
        this.money = money;
    }
}
