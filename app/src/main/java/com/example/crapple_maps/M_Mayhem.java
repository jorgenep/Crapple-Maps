package com.example.crapple_maps;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.Manifest;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.example.crapple_maps.databinding.ActivityMmayhemBinding;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.net.PlacesClient;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PipedInputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class M_Mayhem extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private ActivityMmayhemBinding binding;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1; // Permission request code
    private FusedLocationProviderClient fusedLocationClient; // Client for accessing location
    private static final String API_KEY = "AIzaSyCp_DPsej9a2x_WWTlfPE5tSVr1DrqnFw0"; // <-- Replace this with your actual API key

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (!Places.isInitialized()) {
            Places.initialize(getApplicationContext(), API_KEY);
        }
        PlacesClient placesClient = Places.createClient(getApplicationContext());
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        binding = ActivityMmayhemBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }
    private void getLastLocation() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)   {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
            return;
        }
        fusedLocationClient.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null) {
                    double longitude = location.getLongitude();
                    double latitude = location.getLatitude();
                    LatLng userLocation = new LatLng(longitude,latitude);
                    mMap.addMarker(new MarkerOptions().position(userLocation).title("Current Location").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation, 15));
                    searchFoodNearby(userLocation);
                }
                else {
                    System.out.println("Location not found");
                }
            }
        });
    }
    private void searchFoodNearby(LatLng Location) {
        String locationStr = Location.latitude + "," + Location.longitude;
        int radius = 3000;
        String apiUrl = "https://maps.googleapis.com/maps/api/place/nearbysearch/json" +
                "?location=" + locationStr +
                "&radius=" + radius +
                "&type=restaurant" +
                "&key=" + API_KEY;
        new Thread(() -> {
            try {
                URL apiURL = new URL(apiUrl);
                HttpURLConnection connection = (HttpURLConnection) apiURL.openConnection();
                connection.connect();
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder result = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    result.append(line);
                }
                JSONObject jsonObject = new JSONObject(result.toString());
                JSONArray results = jsonObject.getJSONArray("results");

                runOnUiThread(()-> {
                    try {
                        for (int i = 0; i < results.length(); i++) {
                            JSONObject place = results.getJSONObject(i);
                            if (place.has("rating")) {
                                double rating = place.getDouble("rating");
                                if (rating >= 3.0) {
                                    continue;
                                }
                            } else {continue;}
                            JSONObject location = place.getJSONObject("geometry").getJSONObject("location");
                            String name = place.getString("name");
                            double lat = location.getDouble("lat");
                            double lng = location.getDouble("lng");
                            LatLng placeLatLng = new LatLng (lat,lng);
                            mMap.addMarker(new MarkerOptions().position(placeLatLng).title(name).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        getLastLocation();
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            // If permission granted, get the location
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getLastLocation();
            } else {
                // Otherwise, notify the user that permission is required
                Toast.makeText(this, "Location permission is required to show your location.", Toast.LENGTH_SHORT).show();
            }
        }
    }
}