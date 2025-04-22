package com.example.crapple_maps;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.squareup.picasso.Picasso;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.Random;

public class crapple_reccomends extends AppCompatActivity {


    //ui parts
    private TextView name, address, rating, otherInfo, types;
    private Button getRecommendation;
    private JSONArray restaurantArray;
    private ImageView crappleImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.crapple_reccomends);

        // Set up UI components
        name = findViewById(R.id.nameView);
        address = findViewById(R.id.addressView);
        rating = findViewById(R.id.ratingView);
        otherInfo = findViewById(R.id.infoView);
        types = findViewById(R.id.typesView);
        getRecommendation = findViewById(R.id.btnRec);
        crappleImage = findViewById(R.id.crappleImage);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // get restaurant data from API
        fetchRestaurants();

        // button listener
        getRecommendation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                displayRandomRestaurant();
            }
        });
    }

    //the
    private void fetchRestaurants() {
        String url = "please put the URL here";
        RequestQueue queue = Volley.newRequestQueue(this);
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        restaurantArray = response;
                        displayRandomRestaurant();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // Handle error (e.g., show a Toast or log the error)
                    }
                });
        queue.add(jsonArrayRequest);
    }

    //to display random restaurant from array that we create from API/JSON
    private void displayRandomRestaurant() {
        if (restaurantArray != null && restaurantArray.length() > 0) {
            Random random = new Random();
            int randomIndex = random.nextInt(restaurantArray.length());
            try {
                JSONObject restaurant = restaurantArray.getJSONObject(randomIndex);

                // Set name
                name.setText(restaurant.optString("name", "Name unavailable"));

                // Set address (vicinity)
                address.setText(restaurant.optString("vicinity", "Address unavailable"));

                // Set rating
                if (restaurant.has("rating")) {
                    rating.setText("Rating: " + restaurant.getDouble("rating"));
                } else {
                    rating.setText("Rating not available");
                }

                // Set open/closed status
                if (restaurant.has("opening_hours")) {
                    JSONObject openingHours = restaurant.getJSONObject("opening_hours");
                    boolean isOpen = openingHours.optBoolean("open_now", false);
                    otherInfo.setText("Currently: " + (isOpen ? "Open" : "Closed"));
                } else {
                    otherInfo.setText("Opening hours not available");
                }

                // Set types
                if (restaurant.has("types")) {
                    JSONArray typesArray = restaurant.getJSONArray("types");
                    StringBuilder typesBuilder = new StringBuilder("Types: ");
                    for (int i = 0; i < typesArray.length(); i++) {
                        typesBuilder.append(typesArray.getString(i));
                        if (i < typesArray.length() - 1) {
                            typesBuilder.append(", ");
                        }
                    }

                    types.setText(typesBuilder.toString());

                }

                // Load photo
                if (restaurant.has("photos")) {
                    JSONArray photos = restaurant.getJSONArray("photos");
                    if (photos.length() > 0) {
                        JSONObject photo = photos.getJSONObject(0);
                        String photoRef = photo.getString("photo_reference");
                        String imageURL = "https://maps.googleapis.com/maps/api/place/photo?maxwidth=400&photoreference="
                                + photoRef + "&key=YOUR_API_KEY";

                        Picasso.get().load(imageURL).into(crappleImage);
                    }
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}