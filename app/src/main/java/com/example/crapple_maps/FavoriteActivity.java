package com.example.crapple_maps;

import android.os.Bundle;
import android.view.View;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

public class FavoriteActivity extends AppCompatActivity {

    ListView listView;
    Adapter adapter;
    List<Favorites> favoritesList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        hideSystemUI();
        setContentView(R.layout.activity_favorite);

        listView = findViewById(R.id.listView);
        favoritesList = new ArrayList<>();

        //favoritesList.add(new Favorites("Greasy Spoon", "123 Grease Ave", "1.2"));
        //favoritesList.add(new Favorites("Wok Disaster", "456 Soggy Blvd", "0.8"));
        //favoritesList.add(new Favorites("McYuck's", "789 Boring Rd", "1.0"));
        //favoritesList.add(new Favorites("Pasta Panic", "321 Noodle St", "1.5"));
        //favoritesList.add(new Favorites("Burger Broke", "654 Crumb Ln", "1.1"));

        adapter = new Adapter(this, favoritesList);
        listView.setAdapter(adapter);
    }

    private void hideSystemUI() {
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_IMMERSIVE
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
        );
    }
}
