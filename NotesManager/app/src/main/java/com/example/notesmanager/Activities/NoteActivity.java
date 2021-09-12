package com.example.notesmanager.Activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.example.notesmanager.DataAccessLayer.DBManager;
import com.example.notesmanager.Objects.Note;
import com.example.notesmanager.R;
import com.example.notesmanager.Objects.ToastManager;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import de.hdodenhof.circleimageview.CircleImageView;

public class NoteActivity extends AppCompatActivity implements PopupMenu.OnMenuItemClickListener {

    private TextView mDisplayDate;
    private DatePickerDialog.OnDateSetListener mDateSetListener;

    private EditText title;
    private EditText body;
    private Button save;
    private Button delete;
    private DBManager dbManager;
    private CircleImageView image;
    private static final int PICK_IMAGE_GALLERY = 1;
    private static final int PICK_IMAGE_CAMERA = 100;
    int id;
    private Date date;
    private Uri imageUri;
    private Bitmap bitmap;
    private FusedLocationProviderClient fusedLocationProviderClient;
    final String pattern = "dd/MM/yyyy";
    private FirebaseAuth auth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note);

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        // Get user location and after that call the create function to create the activity
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if(getApplicationContext().checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
                fusedLocationProviderClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        if(location != null){
                            double latitude = location.getLatitude();
                            double longitude = location.getLongitude();
                            create(latitude, longitude);
                        }
                    }
                });
            }
            else{
                ToastManager.makeCustomToast(NoteActivity.this, "Do not have permission for location", Color.rgb(128,0,0), Color.BLACK);
                startActivity(new Intent(NoteActivity.this, MenuActivity.class));
                finish();
            }
        }
    }

    // Creates the activity
    private void create(double latitude, double longitude){
        dbManager = new DBManager(this);
        auth = FirebaseAuth.getInstance();

        // check if there is a current note to update or a new note
        Bundle b = getIntent().getExtras();
        this.id = -1; // or other values
        if(b != null)
            this.id = b.getInt("key");

        // connect the layouts
        title = findViewById(R.id.title);
        body = findViewById(R.id.body);
        save = findViewById(R.id.save);
        delete = findViewById(R.id.delete);
        mDisplayDate = findViewById(R.id.date);
        image = findViewById(R.id.image);

        // init the date format
        DateFormat df = new SimpleDateFormat(pattern);

        // set display date listener
        mDisplayDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar cal = Calendar.getInstance();
                int year = cal.get(Calendar.YEAR);
                int month = cal.get(Calendar.MONTH);
                int day = cal.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog dialog = new DatePickerDialog(NoteActivity.this,
                        android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                        mDateSetListener,
                        year, month, day);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();
            }
        });

        // When date set, set the text and save the value
        mDateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                month += 1;
                String dateStr =day + "/" + month + "/" + year;
                mDisplayDate.setText(dateStr);
                mDisplayDate.setBackgroundResource(0);
                date = new GregorianCalendar(year, month - 1, day).getTime();
            }
        };

        // Set image click listener
        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PopupMenu popupMenu = new PopupMenu(NoteActivity.this, view);
                popupMenu.setOnMenuItemClickListener(NoteActivity.this);
                popupMenu.inflate(R.menu.popup_menu);
                popupMenu.show();
            }
        });

        // if its update mode, set the values of the current note
        setValuesOfCurrNote(df);

        // set save click listener
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String titleString = title.getText().toString().trim();
                String bodyString = body.getText().toString().trim();
                if(!validateNote(titleString, bodyString, date)){
                    return;
                }
                Note note = new Note(id, date, titleString, bodyString, latitude, longitude, bitmap);
                if(id == -1){
                    dbManager.insert(note, auth.getCurrentUser().getEmail());
                }
                else{
                    dbManager.update(note, auth.getCurrentUser().getEmail());
                }
                startActivity(new Intent(NoteActivity.this, MenuActivity.class));
                finish();
            }
        });

        // set delete click listener
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(id == -1){
                    ToastManager.makeCustomToast(NoteActivity.this, "This note not saved yet", Color.rgb(128,0,0), Color.BLACK);
                }
                else{
                    dbManager.delete(String.valueOf(id));
                    startActivity(new Intent(NoteActivity.this, MenuActivity.class));
                    finish();
                }
            }
        });

        requestCameraPerm();
    }

    // listener for camera/gallery buttons
    @Override
    public boolean onMenuItemClick(MenuItem menuItem) {
        switch (menuItem.getItemId()){
            case R.id.item_gallery:
                // request gallery choose activity
                Intent gallery = new Intent();
                gallery.setType("image/*");
                gallery.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(gallery, "sellect picture"), PICK_IMAGE_GALLERY);
                return true;

            case R.id.item_camera:
                // request open Camera
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent, PICK_IMAGE_CAMERA);
                return true;

            default:
                return false;
        }
    }

    // when photo has been chosen, receive and handle the photo
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // if it was gallery
        if((requestCode == PICK_IMAGE_GALLERY) && resultCode == RESULT_OK){
            imageUri = data.getData();
            try{
                Bitmap captureImage = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
                this.bitmap = captureImage;
                image.setImageBitmap(captureImage);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        // if it was camera
        if(requestCode == PICK_IMAGE_CAMERA && resultCode == RESULT_OK){
            Bitmap captureImage = (Bitmap) data.getExtras().get("data");
            this.bitmap = captureImage;
            image.setImageBitmap(captureImage);
        }
    }

    // Validate that all notes properties inserted correctly
    private boolean validateNote(String titleString, String bodyString, Date date) {
        if(date == null){
            ToastManager.makeCustomToast(NoteActivity.this, "Please Insert Date", Color.rgb(128,0,0), Color.BLACK);
        }
        else if(titleString == null || titleString.isEmpty()){
            ToastManager.makeCustomToast(NoteActivity.this, "Please Insert Title", Color.rgb(128,0,0), Color.BLACK);
        }
        else if(bodyString == null || bodyString.isEmpty()){
            ToastManager.makeCustomToast(NoteActivity.this, "Please Insert Body", Color.rgb(128,0,0), Color.BLACK);
        }
        else{
            return true;
        }
        return false;
    }

    // Top menu creation
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.top_menu_note ,menu);
        return true;
    }

    // Back button listener
    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        startActivity(new Intent(NoteActivity.this, MenuActivity.class));
        finish();
        return super.onOptionsItemSelected(item);
    }

    // Request for using camera permission
    public void requestCameraPerm(){
        if(ContextCompat.checkSelfPermission(NoteActivity.this,
                Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(NoteActivity.this,
                    new String[]{
                            Manifest.permission.CAMERA
                    },
                    100);
        }
    }

    // if its update mode, set the values of the current note
    public void setValuesOfCurrNote(DateFormat df){
        if(id != -1){
            Note note = dbManager.getNoteById(id);
            date = note.getDate();
            mDisplayDate.setText(df.format(date));
            mDisplayDate.setBackgroundResource(0);
            title.setText(note.getTitle());
            body.setText(note.getBody());
            bitmap = note.getBitmap();
            if(bitmap != null){
                image.setImageBitmap(bitmap);
            }
        }
    }

    @Override
    public void onBackPressed(){
        startActivity(new Intent(NoteActivity.this, MenuActivity.class));
        finish();
    }
}