package com.example.crapple_maps;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {

    Button btnStart, randLocation, filterLocations, favorites, quit;
    ImageView background;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize buttons
        btnStart = findViewById(R.id.btnStart);
        randLocation = findViewById(R.id.randLocation);
        filterLocations = findViewById(R.id.filterLocations);
        favorites = findViewById(R.id.favorites);
        quit = findViewById(R.id.quit);

        // Set listeners for each button
        btnStart.setOnClickListener(v -> {
            //startActivity(new Intent(MainActivity.this, MapsActivity.class));
        });

        randLocation.setOnClickListener(v -> {
            //startActivity(new Intent(MainActivity.this, CrappleRecommendsActivity.class));
        });

        filterLocations.setOnClickListener(v -> {
            //startActivity(new Intent(MainActivity.this, FilterActivity.class));
        });

        favorites.setOnClickListener(v -> {
            //startActivity(new Intent(MainActivity.this, LegendaryLowlightsActivity.class));
        });

        quit.setOnClickListener(v -> {
            finishAffinity(); // Closes the app
        });
    }
}
