package com.example.notesmanager.Objects;

import android.graphics.Bitmap;

import java.util.Date;

// Note object
public class Note implements Comparable<Note> {

    private int id;
    private Date date;
    private String title;
    private String body;
    private double latitude;
    private double longitude;
    private Bitmap bitmap;

    public Note(int id, Date date, String title, String body, double latitude, double longitude) {
        this.id = id;
        this.date = date;
        this.title = title;
        this.body = body;
        this.latitude = latitude;
        this.longitude = longitude;
        this.bitmap = null;
    }

    public Note(int id, Date date, String title, String body, double latitude, double longitude, Bitmap bitmap) {
        this.id = id;
        this.date = date;
        this.title = title;
        this.body = body;
        this.latitude = latitude;
        this.longitude = longitude;
        this.bitmap = bitmap;
    }

    // Getters and setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
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

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    // Compare between two notes by date of creation
    @Override
    public int compareTo(Note other) {
        if (getDate() == null || other.getDate() == null) {
            return 0;
        }
        return getDate().compareTo(other.getDate());
    }
}
