package com.example.notesmanager.Activities;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.notesmanager.DataAccessLayer.DBManager;
import com.example.notesmanager.Objects.MapLocation;
import com.example.notesmanager.Objects.Note;
import com.example.notesmanager.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class MenuActivity extends AppCompatActivity implements OnMapReadyCallback, BottomNavigationView.OnNavigationItemSelectedListener {

    private GoogleMap map;
    private List<double[]> adresses;
    SupportMapFragment mapFragment;
    private BottomNavigationView bottom_menu;
    private LinearLayout map_layout, list_layout;
    private HashMap<Marker, Integer> mHashMap = new HashMap<Marker, Integer>();

    static final float COORDINATE_OFFSET = 0.00008f;

    RecyclerView notesList;
    DBManager dbManager;
    ArrayList<String> id, title;
    CustomAdapter customAdapter;
    TextView no_notes;
    private FirebaseAuth auth;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        // init values
        map_layout = findViewById(R.id.map_layout);
        list_layout = findViewById(R.id.list_layout);
        bottom_menu = findViewById(R.id.bottomNavigationView);
        dbManager = new DBManager(MenuActivity.this);
        notesList = findViewById(R.id.notesList);
        no_notes = findViewById(R.id.no_notes);
        id = new ArrayList<>();
        title = new ArrayList<>();
        auth = FirebaseAuth.getInstance();

        // request permission for location
        requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);

        // Get the notes data from DB
        storeDataInArrays();

        // if there are no notes, show that to screen
        noNotesVisibility();

        // set the notes list layout
        customAdapter = new CustomAdapter(MenuActivity.this, id, title);
        notesList.setAdapter(customAdapter);
        notesList.setLayoutManager(new LinearLayoutManager(MenuActivity.this));

        bottom_menu.setOnNavigationItemSelectedListener(this);
        bottom_menu.setSelectedItemId(R.id.map_layout);

        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);

        mapFragment.getMapAsync(this);

    }

    // if there are no notes, show that to screen
    private void noNotesVisibility() {
        if(id.size() > 0){
            no_notes.setVisibility(View.GONE);
            notesList.setVisibility(View.VISIBLE);
        }
        else{
            no_notes.setVisibility(View.VISIBLE);
            notesList.setVisibility(View.GONE);
        }
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        map = googleMap;
        List<MapLocation> locations = new ArrayList<>();
        // for each note in db, get is location and put on map
        for(int i = 0; i<id.size(); i++){
            Note note = dbManager.getNoteById(Integer.parseInt(id.get(i)));
            MapLocation location = new MapLocation(note.getLatitude(), note.getLongitude());
            location = generateLocation(location, locations);
            locations.add(location);
            LatLng currLatLng = new LatLng(location.getLatitude(), location.getLongitude());
            Marker marker = map.addMarker(new MarkerOptions()
                    .position(currLatLng)
                    .title(note.getTitle())
                    .infoWindowAnchor(0.1F,0));
            marker.showInfoWindow();
            marker.setDraggable(true);
            mHashMap.put(marker, note.getId());
            map.moveCamera(CameraUpdateFactory.newLatLng(currLatLng));
        }
        // if a marker is clicked, go to note activity
        map.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(@NonNull Marker marker) {
                Note note = dbManager.getNoteById(mHashMap.get(marker));
                Intent intent = new Intent(MenuActivity.this, NoteActivity.class);
                Bundle b = new Bundle();
                b.putInt("key", note.getId());
                intent.putExtras(b); //Put your id to your next Intent
                startActivity(intent);
                finish();
                return false;
            }
        });

        // if a marker drag, show is info and put him back to the start location
        map.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener() {

            private LatLng location_start;
            @Override
            public void onMarkerDragStart(@NonNull Marker marker) {
                marker.showInfoWindow();
                location_start = marker.getPosition();
            }

            @Override
            public void onMarkerDrag(@NonNull Marker marker) {
                marker.showInfoWindow();
            }

            @Override
            public void onMarkerDragEnd(@NonNull Marker marker) {
                marker.showInfoWindow();
                marker.setPosition(location_start);
            }
        });
    }

    // if location of marker is already on map, move it by offset
    private MapLocation generateLocation(MapLocation location, List<MapLocation> locations) {
        while(locations.contains(location)){
            location.setLatitude(location.getLatitude() + COORDINATE_OFFSET);
            location.setLongitude(location.getLongitude() + COORDINATE_OFFSET);
        }
        return location;
    }

    // Navigate between map and list view
    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch(item.getItemId()){
            case R.id.map_menu:
                list_layout.setVisibility(View.GONE);
                map_layout.setVisibility(View.VISIBLE);
                return true;
            case R.id.list_menu:
                list_layout.setVisibility(View.VISIBLE);
                map_layout.setVisibility(View.GONE);
                return true;
        }
        return false;
    }

    // Get the notes from DB
    void storeDataInArrays(){
        List<Note> notes = dbManager.readData(auth.getCurrentUser().getEmail());
        Collections.sort(notes);
        Collections.reverse(notes);
        for(Note n : notes){
            id.add(String.valueOf(n.getId()));
            title.add(n.getTitle());
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.top_menu ,menu);
        return true;
    }

    // listener to top menu
    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.logout:
                FirebaseAuth.getInstance().signOut();
                Toast.makeText(MenuActivity.this, "Logged Out!", Toast.LENGTH_LONG).show();
                startActivity(new Intent(MenuActivity.this, StartActivity.class));
                finish();
                break;
            case R.id.add_note:
                startActivity(new Intent(MenuActivity.this, NoteActivity.class));
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed(){
        startActivity(new Intent(MenuActivity.this, MenuActivity.class));
        finish();
    }
}