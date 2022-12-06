package com.selim_jnu.kothayacho;

import java.io.Serializable;

public class User implements Serializable {
    private String id;
    private String number;
    private String name;
    private String key;
    private double lat;
    private double lon;
    private long lastSeen;

    public User() {

    }


    public User(String id, String number, String name, String key, double lat, double lon, long lastSeen) {
        this.id = id;
        this.number = number;
        this.name = name;
        this.key = key;
        this.lat = lat;
        this.lon = lon;
        this.lastSeen = lastSeen;
    }

    public long getLastSeen() {
        return lastSeen;
    }

    public void setLastSeen(long lastSeen) {
        this.lastSeen = lastSeen;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNumber() {
        return number;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLon() {
        return lon;
    }

    public void setLon(double lon) {
        this.lon = lon;
    }
}
