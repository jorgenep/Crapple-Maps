package com.example.crapple_maps;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PointOfInterest;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.net.PlacesClient;

import org.json.JSONArray;
import org.json.JSONObject;
import java.util.ArrayList;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

// Main activity that displays a Google Map and locates nearby food places
public class M_Mayhem extends FragmentActivity implements OnMapReadyCallback {

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1; // Permission request code
    private GoogleMap mMap; // Google Map object
    private FusedLocationProviderClient fusedLocationClient; // Client for accessing location
    private static final String API_KEY = "AIzaSyCp_DPsej9a2x_WWTlfPE5tSVr1DrqnFw0"; // <-- Replace this with your actual API key

    private ArrayList<Integer> selectedStars;
    private ArrayList<String> selectedCuisines;
    private ArrayList<String> selectedPrices;
    private double minDistance = 0;
    private double maxDistance = Double.MAX_VALUE;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mmayhem);

        Intent intent = getIntent();

        // Check if filters exist in the Intent
        if (intent.hasExtra("selectedStars")) {
            selectedStars = intent.getIntegerArrayListExtra("selectedStars");
        } else {
            selectedStars = new ArrayList<>();
        }

        if (intent.hasExtra("selectedCuisines")) {
            selectedCuisines = intent.getStringArrayListExtra("selectedCuisines");
        } else {
            selectedCuisines = new ArrayList<>();
        }

        if (intent.hasExtra("selectedPrices")) {
            selectedPrices = intent.getStringArrayListExtra("selectedPrices");
        } else {
            selectedPrices = new ArrayList<>();
        }

        minDistance = intent.getDoubleExtra("minDistance", 0);
        maxDistance = intent.getDoubleExtra("maxDistance", Double.MAX_VALUE);

        // Initialize the Places API with the API key if not already done
        if (!Places.isInitialized()) {
            Places.initialize(getApplicationContext(), API_KEY);
        }

        PlacesClient placesClient = Places.createClient(this);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        assert mapFragment != null;
        mapFragment.getMapAsync(this);
    }

    // Retrieve the user's last known location
    private void getLastLocation() {
        // Check if location permissions are granted
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // Request location permission if not granted
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
            return;
        }

        // Fetch last known location
        fusedLocationClient.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                // If location is available, update map with user location and search for nearby food
                if (location != null) {
                    double latitude = location.getLatitude();
                    double longitude = location.getLongitude();
                    LatLng userLocation = new LatLng(latitude, longitude);

                    // Add a blue marker for user's current location
                    mMap.addMarker(new MarkerOptions()
                            .position(userLocation)
                            .title("You are here")
                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));

                    // Move camera to user's location with zoom level 15
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation, 15));

                    // Search for nearby restaurants
                    searchNearbyFood(userLocation);
                } else {
                    Log.d("User Location", "Location is null");
                }
            }
        });
    }

    // Search for nearby food places using Places API and plot them on the map
    private void searchNearbyFood(LatLng location) {
        String locationStr = location.latitude + "," + location.longitude;
        int radius = 5000; // Radius in meters

        // Build the URL for the Places API nearby search
        String url = "https://maps.googleapis.com/maps/api/place/nearbysearch/json" +
                "?location=" + locationStr +
                "&radius=" + radius +
                "&type=restaurant" +
                "&key=" + API_KEY;

        // Run network operation on a background thread
        new Thread(() -> {
            try {
                URL apiUrl = new URL(url);
                HttpURLConnection connection = (HttpURLConnection) apiUrl.openConnection();
                connection.connect();

                // Read the response from the API
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder result = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    result.append(line);
                }

                // Parse the JSON response
                JSONObject jsonObject = new JSONObject(result.toString());
                JSONArray results = jsonObject.getJSONArray("results");

                // Update UI on main thread
                runOnUiThread(() -> {
                    try {
                        for (int i = 0; i < results.length(); i++) {
                            JSONObject place = results.getJSONObject(i);

                            Double rating = place.has("rating") ? place.getDouble("rating") : 0;

                            // If NO filters applied, use original logic
                            if (selectedStars.isEmpty() && selectedCuisines.isEmpty() && selectedPrices.isEmpty() && minDistance == 0) {
                                if (rating >= 4.0) continue;
                            } else {
                                // ⭐ Star Rating Filter
                                if (!selectedStars.isEmpty() && !selectedStars.contains((int) Math.floor(rating))) {
                                    continue;
                                }

                                // 💲 Price Filter
                                int priceLevel = place.has("price_level") ? place.getInt("price_level") : -1;
                                if (!selectedPrices.isEmpty()) {
                                    String priceSymbol = priceLevel > 0 ? new String(new char[priceLevel]).replace("\0", "$") : "";
                                    if (!selectedPrices.contains(priceSymbol)) {
                                        continue;
                                    }
                                }
                            }

                            JSONObject loc = place.getJSONObject("geometry").getJSONObject("location");
                            String name = place.getString("name");
                            double lat = loc.getDouble("lat");
                            double lng = loc.getDouble("lng");

                            LatLng placeLatLng = new LatLng(lat, lng);

                            // 📏 Distance Filter (only if filters applied)
                            if (!(selectedStars.isEmpty() && selectedCuisines.isEmpty() && selectedPrices.isEmpty() && minDistance == 0)) {
                                float[] distanceResult = new float[1];
                                Location.distanceBetween(location.latitude, location.longitude, lat, lng, distanceResult);
                                double distanceInMiles = distanceResult[0] * 0.000621371;
                                if (distanceInMiles < minDistance || distanceInMiles > maxDistance) {
                                    continue;
                                }
                            }

                            // 🍽 Cuisine Filter (if filters applied)
                            if (!selectedCuisines.isEmpty()) {
                                boolean cuisineMatch = false;
                                JSONArray types = place.getJSONArray("types");
                                for (int j = 0; j < types.length(); j++) {
                                    String type = types.getString(j);
                                    for (String cuisine : selectedCuisines) {
                                        if (type.toLowerCase().contains(cuisine.toLowerCase())) {
                                            cuisineMatch = true;
                                            break;
                                        }
                                    }
                                    if (cuisineMatch) break;
                                }
                                if (!cuisineMatch) continue;
                            }
                            // Add marker if passed all conditions
                            mMap.addMarker(new MarkerOptions()
                                    .position(placeLatLng)
                                    .title(name)
                                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE)));
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

    // Called when the map is ready to use
    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;
        getLastLocation(); // Get and show user location once map is ready
        mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(@NonNull LatLng latLng) { // This adds a function so when you hold down on the screen it changes the location
                mMap.clear();
                mMap.addMarker(new MarkerOptions() // Sets the location to the spot held down on
                        .position(latLng)
                        .title("You are here")
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));

                // Move camera to user's location with zoom level 15
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));

                // Search for nearby restaurants
                searchNearbyFood(latLng);
            }
        });
    }

    // Handle the result of the location permission request
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