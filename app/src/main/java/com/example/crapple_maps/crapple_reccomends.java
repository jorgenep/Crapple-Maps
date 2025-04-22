package com.example.crapple_maps;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.Random;

public class crapple_reccomends extends AppCompatActivity {

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1001;

    // UI components
    private TextView name, address, rating, otherInfo, typesView;
    private Button getRecommendation, mainActivityBtn;
    private JSONArray restaurantArray;
    private ImageView crappleImage;

    // Location
    private FusedLocationProviderClient fusedLocationClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.crapple_reccomends);

        // UI setup
        name = findViewById(R.id.nameView);
        address = findViewById(R.id.addressView);
        rating = findViewById(R.id.ratingView);
        otherInfo = findViewById(R.id.infoView);
        getRecommendation = findViewById(R.id.btnRec);
        mainActivityBtn = findViewById(R.id.mainActivityBtn);
        crappleImage = findViewById(R.id.crappleImage);
        typesView = findViewById(R.id.typesView);

        mainActivityBtn.setOnClickListener(v -> {
            //startActivity(new Intent(MainActivity.this, CrappleRecommendsActivity.class));
            Intent randIntent = new Intent(crapple_reccomends.this, MainActivity.class);
            startActivity(randIntent);
            finish();

        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Location client
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        // Get location
        requestLocation();

        // Button listener
        getRecommendation.setOnClickListener(v -> displayRandomRestaurant());
    }

    private void requestLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE);
        } else {
            getLastLocation();
        }
    }

    private void getLastLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, location -> {
                    if (location != null) {
                        fetchRestaurants(location);
                    }
                });
    }

    private void fetchRestaurants(Location location) {
        String API_KEY = "AIzaSyCp_DPsej9a2x_WWTlfPE5tSVr1DrqnFw0";
        String locationStr = location.getLatitude() + "," + location.getLongitude();
        int radius = 1500;

        String url = "https://maps.googleapis.com/maps/api/place/nearbysearch/json" +
                "?location=" + locationStr +
                "&radius=" + radius +
                "&type=restaurant" +
                "&key=" + API_KEY;

        RequestQueue queue = Volley.newRequestQueue(this);
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    try {
                        restaurantArray = response.getJSONArray("results");
                        displayRandomRestaurant();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                error -> {
                    // Log or show error
                    error.printStackTrace();
                });

        queue.add(request);
    }

    private void displayRandomRestaurant() {
        if (restaurantArray != null && restaurantArray.length() > 0) {
            Random random = new Random();

            int maxTries = restaurantArray.length();
            int tries = 0;

            while (tries < maxTries) {


                int index = random.nextInt(restaurantArray.length());
                try {
                    JSONObject restaurant = restaurantArray.getJSONObject(index);
                    double restaurantRating = restaurant.optDouble("rating", 0.0);

                    // Skip if rating is higher than 3.5
                    if (restaurantRating > 3.5) {
                        tries++;
                        continue;
                    }

                    name.setText(restaurant.optString("name", "N/A"));
                    address.setText(restaurant.optString("vicinity", "N/A"));
                    rating.setText("Overall Rating: " + restaurant.optDouble("rating", 0.0));
                    otherInfo.setText("User Ratings Total: " + restaurant.optInt("user_ratings_total", 0));

                    if (restaurant.has("types")) {
                        JSONArray typesArray = restaurant.getJSONArray("types");
                        StringBuilder typesBuilder = new StringBuilder("Types: ");
                        for (int i = 0; i < 3; i++) {
                            typesBuilder.append(typesArray.getString(i));
                            if (i < typesArray.length() - 1) {
                                typesBuilder.append(", ");
                            }
                        }

                        typesView.setText(typesBuilder.toString());


                        // Load photo
                        if (restaurant.has("photos")) {
                            JSONArray photos = restaurant.getJSONArray("photos");
                            if (photos.length() > 0) {
                                JSONObject photo = photos.getJSONObject(0);
                                String photoRef = photo.getString("photo_reference");
                                String imageURL = "https://maps.googleapis.com/maps/api/place/photo?maxwidth=400&photoreference="
                                        + photoRef + "&key=AIzaSyCp_DPsej9a2x_WWTlfPE5tSVr1DrqnFw0";

                                Picasso.get().load(imageURL).into(crappleImage);
                            }
                        }
                    }

                    break;

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                tries++;
            }
        }
    }

    // Handle permission result
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getLastLocation();
            } else {
                // Permission denied
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}

