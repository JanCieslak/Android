package com.example.hikerswatch;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.pm.PermissionInfoCompat;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.webkit.PermissionRequest;
import android.widget.TextView;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements LocationListener {

    private final int LOCATION_PERMISSION_CODE = 1;
    private LocationManager locationManager;

    private TextView latitude;
    private TextView longitdue;
    private TextView gpsAccuracy;
    private TextView altitude;
    private TextView address;

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode != LOCATION_PERMISSION_CODE)
            return;

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        latitude = findViewById(R.id.Latitude);
        longitdue = findViewById(R.id.Longitdue);
        gpsAccuracy = findViewById(R.id.GPS_Accuracy);
        altitude = findViewById(R.id.Altitude);
        address = findViewById(R.id.Address);

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
            Location lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if (lastKnownLocation != null)
                updateLocation(lastKnownLocation);
        } else {
            // Ask for permission
            ActivityCompat.requestPermissions(this, new String[]{ Manifest.permission.ACCESS_FINE_LOCATION }, LOCATION_PERMISSION_CODE);
        }
    }

    @Override
    public void onLocationChanged(@NonNull Location location) {
        updateLocation(location);
    }


    private void updateLocation(Location location) {
        latitude.setText(Double.toString(location.getLatitude()));
        longitdue.setText(Double.toString(location.getLongitude()));
        gpsAccuracy.setText(Float.toString(location.getAccuracy()));
        altitude.setText(Double.toString(location.getAltitude()));

        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        try {
            List<Address> addressList = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
            if (addressList != null && addressList.size() > 0) {
                Address address = addressList.get(0);
                if (address.getMaxAddressLineIndex() != -1)
                    this.address.setText(address.getAddressLine(0));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) { }
}