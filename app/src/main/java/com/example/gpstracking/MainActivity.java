package com.example.gpstracking;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

public class MainActivity extends AppCompatActivity {

    public static final int DEFAULT_UPDATE_INTERVAL = 30;
    public static final int FAST_UPDATE_INTERVAL = 5;
    private static final int PERMISSIONS_FINE_LOCATIONS = 99;

    //References to UI elements
    TextView tv_lat, tv_lon, tv_altitude, tv_accuracy, tv_speed, tv_sensor, tv_updates, tv_address;
    Switch sw_locationUpdates, sw_gps;

    //Google's API for location services
    FusedLocationProviderClient fusedLocationProviderClient;

    //Location request class
    LocationRequest locationRequest;

    LocationCallback locationCallBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tv_lat = findViewById(R.id.tv_lat);
        tv_lon = findViewById(R.id.tv_lon);
        tv_altitude = findViewById(R.id.tv_altitude);
        tv_accuracy = findViewById(R.id.tv_accuracy);
        tv_speed = findViewById(R.id.tv_speed);
        tv_sensor = findViewById(R.id.tv_sensor);
        tv_updates = findViewById(R.id.tv_updates);
        tv_address = findViewById(R.id.tv_address);
        sw_locationUpdates = findViewById(R.id.sw_locationsupdates);
        sw_gps = findViewById(R.id.sw_gps);

        //set all properties of location request
        locationRequest = new LocationRequest();

        //Location check every 30 or 5 seconds
        locationRequest.setInterval(1000 * DEFAULT_UPDATE_INTERVAL);
        locationRequest.setFastestInterval(1000 * FAST_UPDATE_INTERVAL);

        locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);

        //This method will be called when the update interval is met
        locationCallBack = new LocationCallback() {


            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                super.onLocationResult(locationResult);

                //save the location
                updateUIValues(locationResult.getLastLocation());

            }
        };

        sw_gps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (sw_gps.isChecked()) {

                    //most accurate - use GPS
                    locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
                    tv_sensor.setText("Using GPS sensors");

                } else {

                    locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
                    tv_sensor.setText("Using Towers + WIFI");

                }

            }
        });

        sw_locationUpdates.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (sw_locationUpdates.isChecked()) {

                    startLocationUpdates();

                } else {

                    stopLocationUpdates();

                }

            }
        });


        updateGPS();

    } // END OF CREATE METHOD

    private void startLocationUpdates() {

        tv_updates.setText("Location is being tracked");

        //If you remove this verification, the app will crash
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
        }

        fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallBack, null);
        updateGPS();

    }

    private void stopLocationUpdates() {

        tv_updates.setText("Location is NOT being tracked");
        tv_lat.setText("Not traking location.");
        tv_lon.setText("Not traking location.");
        tv_speed.setText("Not traking speed.");
        tv_address.setText("Not traking location.");
        tv_accuracy.setText("Not traking location.");
        tv_altitude.setText("Not traking location.");
        tv_sensor.setText("Not traking location.");

        fusedLocationProviderClient.removeLocationUpdates(locationCallBack);



    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode){

            case PERMISSIONS_FINE_LOCATIONS:
            if(grantResults[0] == PackageManager.PERMISSION_GRANTED){
                updateGPS();
            } else {

                Toast.makeText(this, "This app needs permission to be granted in order to work properly.", Toast.LENGTH_SHORT).show();

            }
            break;

        }

    }

    private void updateGPS(){

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(MainActivity.this);

        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){

            //permission granted by the user
            fusedLocationProviderClient.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {

                    //we got permission and we put values of location inside the UI.
                    updateUIValues(location);

                }
            });

        } else {

            //permission not granted by the user
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                requestPermissions(new String [] {Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSIONS_FINE_LOCATIONS);
            }

        }

    }

    private void updateUIValues(Location location) {

        if(location == null){

            Toast.makeText(this, "Turn on your GPS Location to use this app.", Toast.LENGTH_LONG).show();

        } else {

            //update all the textviews with the new location
            tv_lat.setText(String.valueOf(location.getLatitude()));
            tv_lon.setText(String.valueOf(location.getLongitude()));
            tv_accuracy.setText(String.valueOf(location.getAccuracy()));

            if (location.hasAltitude()) {
                tv_altitude.setText(String.valueOf(location.getAltitude()));
            } else {
                tv_altitude.setText("Not available.");
            }

            if (location.hasSpeed()) {
                tv_altitude.setText(String.valueOf(location.getSpeed()));
            } else {
                tv_altitude.setText("Not available.");
            }
        }

    }


}