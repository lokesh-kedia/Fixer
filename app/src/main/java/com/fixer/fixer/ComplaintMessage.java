package com.fixer.fixer;

public class ComplaintMessage {

    //private String Problem;
    private String Address;
    private String Date;
    private String Desc;
    private String Owner;
    private double lat;
    private double lng;
    private String UID;
    private String url;
    private String Oimg;
    private String Key;


    public ComplaintMessage() {
    }


    public ComplaintMessage(String Address, String Date, String Desc, String Owner, double lat, double lng, String UID, String url, String Oimg, String Key) {
        this.Address = Address;
        this.Date = Date;
        this.Desc = Desc;
        this.Owner = Owner;
        this.lat = lat;
        this.lng = lng;
        this.UID = UID;
        this.url = url;
        this.Oimg = Oimg;
        this.Key = Key;

    }


    public String getAddress() {
        return Address;
    }

    public void setAddress(String address) {
        Address = address;
    }

    public String getDate() {
        return Date;
    }

    public void setDate(String date) {
        Date = date;
    }

    public String getDesc() {
        return Desc;
    }

    public void setDesc(String desc) {
        Desc = desc;
    }

    public String getOwner() {
        return Owner;
    }

    public void setOwner(String owner) {
        Owner = owner;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }

    public String getUID() {
        return UID;
    }

    public void setUID(String UID) {
        this.UID = UID;
    }

    public String geturl() {
        return url;
    }

    public void seturl(String url) {
        this.url = url;
    }

    public String getOimg() {
        return Oimg;
    }

    public String getKey() {
        return Key;
    }
}