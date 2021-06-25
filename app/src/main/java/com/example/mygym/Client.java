package com.example.mygym;

import android.content.Intent;
import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.Timestamp;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Client extends Intent implements Parcelable,Comparable<Client> {
    String name;
    String url;
    String joining;
    String finish;
    String money;
    String batch;
    String mob;
    String rno;
    String timestamp;
    String total;
  //  String pnomber;
    public Client(String name, String url, String joining, String finish, String money,String batch,String mob,String timestamp,String total,String rno) {
        this.name = name;
        this.url = url;
        this.joining = joining;
        this.finish = finish;
        this.money = money;
        this.batch=batch;
        this.mob=mob;
        this.total=total;
        this.timestamp=timestamp;
        this.rno=rno;
       // this.pnomber=pno;
    }



    public String getRno() {
        return rno;
    }

    public void setRno(String rno) {
        this.rno = rno;
    }

    public String getTotal() {
        return total;
    }

    public void setTotal(String total) {
        this.total = total;
    }

    public String getBatch() {
        return batch;
    }

    public void setBatch(String batch) {
        this.batch = batch;
    }

    public Client() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getJoining() {
        return joining;
    }

    public void setJoining(String joining) {
        this.joining = joining;
    }

    public String getFinish() {
        return finish;
    }

    public void setFinish(String finish) {
        this.finish = finish;
    }

    public String getMoney() {
        return money;
    }

    public void setMoney(String money) {
        this.money = money;
    }

    public String getMob() {
        return mob;
    }

    public void setMob(String mob) {
        this.mob = mob;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public int describeContents() {
        return 0;
    }
    protected Client(Parcel in)
    {
        this.name=in.readString();
        this.money=in.readString();
        this.batch=in.readString();
        this.joining=in.readString();
        this.finish=in.readString();
        this.url=in.readString();
        this.mob=in.readString();
        this.timestamp=in.readString();
        this.total=in.readString();

    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeString(name);
        out.writeString(money);
        out.writeString(batch);
        out.writeString(joining);
        out.writeString(finish);
        out.writeString(url);
        out.writeString(mob);
        out.writeString(timestamp);
        out.writeString(total);
    }
    public static final Creator<Client>CREATOR=new Creator<Client>() {
        @Override
        public Client createFromParcel(Parcel parcel) {
            return new Client(parcel);
        }

        @Override
        public Client[] newArray(int i) {
            return new Client[i];
        }
    };

    @Override
    public int compareTo(Client client) {

//        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy kk:mm");
        Date strDate = null,str=null;
      String v=getFinish();
       String w=client.getFinish();
         SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
      // Date date1=new Date(v);
       //Date date2=new Date(w);
        try {
            strDate=sdf.parse(v);
            str=sdf.parse(w);
        } catch (ParseException e) {
            e.printStackTrace();
        }
//        if (strDate.getTime()>str.getTime())
//            return -1;
//        else
//            return 1;
        return strDate.compareTo(str);
        //return date1.compareTo(date2);


       // return 1;
    }
}
