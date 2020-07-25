package com.example.memorableplaces;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {
    private final int LOCATION_PERMISSION_CODE = 1;

    private GoogleMap gMap;
    private LocationManager locationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // Ask for permission
            ActivityCompat.requestPermissions(this, new String[]{ Manifest.permission.ACCESS_FINE_LOCATION }, LOCATION_PERMISSION_CODE);
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        gMap = googleMap;

        moveToLocation();

        gMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng latLng) {
                Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
                try {
                    List<Address> fromLocation = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);
                    if (fromLocation != null && fromLocation.size() > 0) {
                        Address address = fromLocation.get(0);
                        if (address.getMaxAddressLineIndex() != -1) {
                            MainActivity.places.add(address.getAddressLine(0));
                            MainActivity.arrayAdapter.notifyDataSetChanged();
                            finish();
                        }
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode != LOCATION_PERMISSION_CODE)
            return;

        moveToLocation();
    }

    private void moveToLocation() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            Location lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if (lastKnownLocation != null) {
                Intent intent = getIntent();
                String location = intent.getStringExtra("setLocation");
                if (location != null) {
                    try {
                        Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
                        List<Address> fromLocationName = geocoder.getFromLocationName(location, 1);

                        if (fromLocationName != null && fromLocationName.size() > 0) {
                            Address address = fromLocationName.get(0);
                            LatLng latLng = new LatLng(address.getLatitude(), address.getLongitude());
                            gMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 18.0f));
                            gMap.addMarker(new MarkerOptions().position(latLng).title(location));

                        } else {
                            LatLng lastLoc = new LatLng(lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude());
                            gMap.animateCamera(CameraUpdateFactory.newLatLngZoom(lastLoc, 18.0f));

                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    LatLng lastLoc = new LatLng(lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude());
                    gMap.animateCamera(CameraUpdateFactory.newLatLngZoom(lastLoc, 18.0f));

                }
            }
        }
    }
}