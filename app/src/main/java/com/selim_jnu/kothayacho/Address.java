package com.selim_jnu.kothayacho;

public class Address {
    String id;
    String address;
    double lat;
    double lon;
    long timestamp;
    String key;

    public Address() {
    }

    public Address(String id, String address, double lat, double lon, long timestamp, String key) {
        this.id = id;
        this.address = address;
        this.lat = lat;
        this.key = key;
        this.lon = lon;
        this.timestamp = timestamp;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public double getLon() {
        return lon;
    }

    public void setLon(double lon) {
        this.lon = lon;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
