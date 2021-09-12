package com.example.notesmanager.DataAccessLayer;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;

import androidx.annotation.Nullable;

import com.example.notesmanager.Objects.Note;
import com.example.notesmanager.Objects.ToastManager;

import java.io.ByteArrayOutputStream;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class DBManager extends SQLiteOpenHelper {

    private Context context;
    private static final String DATABASE_NAME = "Notes.db";
    private static final int DATABASE_VERSION = 1;

    private static final String TABLE_NAME = "my_notes";
    private static final String COLUMN_ID = "_id";
    private static final String COLUMN_DATE = "date";
    private static final String COLUMN_TITLE = "title";
    private static final String COLUMN_BODY = "body";
    private static final String COLUMN_LATITUDE = "latitude";
    private static final String COLUMN_LONGITUDE = "longitude";
    private static final String COLUMN_IMAGE = "image";
    private static final String COLUMN_USER_NAME = "user";
    final String pattern = "dd/MM/yyyy";
    DateFormat df;

    public DBManager(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
        df = new SimpleDateFormat(pattern);
    }

    // Create the table
    @Override
    public void onCreate(SQLiteDatabase db) {
        String query =
                "CREATE TABLE " + TABLE_NAME +
                        " (" + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        COLUMN_DATE + " TEXT, " +
                        COLUMN_TITLE + " TEXT, " +
                        COLUMN_BODY + " TEXT, " +
                        COLUMN_LATITUDE + " REAL, " +
                        COLUMN_LONGITUDE + " REAL, " +
                        COLUMN_IMAGE + " BLOB, " +
                        COLUMN_USER_NAME + " TEXT);";
        db.execSQL(query);

    }

    public void dropDB(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    // Insert a note to DB
    public void insert(Note note, String userName){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        byte[] img_data = null;
        if(note.getBitmap() != null){
            img_data = getBitmapAsByteArray(note.getBitmap());
        }
        cv.put(COLUMN_DATE, df.format(note.getDate()));
        cv.put(COLUMN_TITLE, note.getTitle());
        cv.put(COLUMN_BODY, note.getBody());
        cv.put(COLUMN_LATITUDE, note.getLatitude());
        cv.put(COLUMN_LONGITUDE, note.getLongitude());
        cv.put(COLUMN_IMAGE, img_data);
        cv.put(COLUMN_USER_NAME, userName);

        long result = db.insert(TABLE_NAME, null, cv);
        if (result == -1){
            ToastManager.makeCustomToast(context, "Failed insert", Color.rgb(128,0,0), Color.BLACK);
        }
        else{
            ToastManager.makeCustomToast(context, "Added Successfully", Color.rgb(0,128,0), Color.BLACK);
        }
    }

    // Read all notes from DB
    public List<Note> readData(String user){
        String query = "SELECT * From " + TABLE_NAME;
        SQLiteDatabase db = this.getReadableDatabase();
        List<Note> notes = new ArrayList<>();
        Cursor cursor = null;
        if(db != null){
            cursor = db.rawQuery(query, null);
            if(cursor.getCount() == 0){

            }
            else{
                while (cursor.moveToNext()){
                    int id = Integer.parseInt(cursor.getString(0));
                    Date date = null;
                    try {
                        date = df.parse(cursor.getString(1));
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    String title = cursor.getString(2);
                    String body = cursor.getString(3);
                    double latitude = cursor.getDouble(4);
                    double lonitude = cursor.getDouble(5);
                    byte[] imgByte = cursor.getBlob(6);
                    String user_name = cursor.getString(7);
                    Bitmap bitmap = null;
                    if(imgByte != null){
                        bitmap = BitmapFactory.decodeByteArray(imgByte, 0, imgByte.length);
                    }
                    if(user.equals(user_name)){
                        notes.add(new Note(id, date, title, body, latitude, lonitude, bitmap));
                    }
                }
            }
        }
        return notes;
    }

    // Get a specific note by id
    public Note getNoteById(int id){
        String query = "SELECT * FROM " + TABLE_NAME
                + " WHERE " + COLUMN_ID + " = " + id;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        if(db != null){
            cursor = db.rawQuery(query, null);

        }
        if(cursor.getCount() != 0){
            if(cursor.moveToNext()){
                Date date = null;
                try {
                    date = df.parse(cursor.getString(1));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                String title = cursor.getString(2);
                String body = cursor.getString(3);
                double latitude = cursor.getDouble(4);
                double lonitude = cursor.getDouble(5);
                byte[] imgByte = cursor.getBlob(6);
                Bitmap bitmap = null;
                if(imgByte != null){
                    bitmap = BitmapFactory.decodeByteArray(imgByte, 0, imgByte.length);
                }
                return new Note(id, date, title, body, latitude, lonitude, bitmap);
            }
        }
        return null;
    }

    // Update a specific note
    public void update(Note note, String userName){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        byte[] img_data = null;
        if(note.getBitmap() != null){
            img_data = getBitmapAsByteArray(note.getBitmap());
        }

        cv.put(COLUMN_DATE, df.format(note.getDate()));
        cv.put(COLUMN_TITLE, note.getTitle());
        cv.put(COLUMN_BODY, note.getBody());
        cv.put(COLUMN_LATITUDE, note.getLatitude());
        cv.put(COLUMN_LONGITUDE, note.getLongitude());
        cv.put(COLUMN_IMAGE, img_data);
        cv.put(COLUMN_USER_NAME, userName);

        long result = db.update(TABLE_NAME, cv, "_id=?", new String[]{String.valueOf(note.getId())});
        if (result == -1){
            ToastManager.makeCustomToast(context, "Update Failed", Color.rgb(128,0,0), Color.BLACK);
        }
        else{
            ToastManager.makeCustomToast(context, "Updated Successfully", Color.rgb(0,128,0), Color.BLACK);
        }
    }

    // Delete a note from DB
    public void delete(String id){
        SQLiteDatabase db = this.getWritableDatabase();
        long result = db.delete(TABLE_NAME, "_id=?", new String[]{id});
        if (result == -1){
            ToastManager.makeCustomToast(context, "Delete Failed", Color.rgb(128,0,0), Color.BLACK);
        }
        else{
            ToastManager.makeCustomToast(context, "Deleted Successfully", Color.rgb(0,128,0), Color.BLACK);
        }
    }

    // change BitMap to BitArray
    public static byte[] getBitmapAsByteArray(Bitmap bitmap) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 0, outputStream);
        return outputStream.toByteArray();
    }
}
