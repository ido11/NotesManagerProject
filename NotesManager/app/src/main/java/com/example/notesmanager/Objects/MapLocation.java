package com.example.notesmanager.Objects;

// Object for map location
public class MapLocation {

    private double latitude;
    private double longitude;

    public MapLocation(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    // Two locations are equals if they have the same latitude and longitude
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof MapLocation)) return false;
        MapLocation that = (MapLocation) o;
        return getLatitude() == that.getLatitude() && getLongitude() == that.getLongitude();
    }

}
