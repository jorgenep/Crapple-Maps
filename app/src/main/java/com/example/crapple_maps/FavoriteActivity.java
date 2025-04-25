package com.example.crapple_maps;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class FavoriteActivity extends AppCompatActivity {

    ListView listView;
    Adapter adapter;
    List<Favorites> favoritesList;
    Button backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        hideSystemUI();
        setContentView(R.layout.activity_favorite);

        listView = findViewById(R.id.listView);
        backButton = findViewById(R.id.backButton);
        favoritesList = new ArrayList<>();

        // Load favorites from the file
        loadFavoritesFromFile();

        // Set up the adapter for the ListView
        adapter = new Adapter(this, favoritesList);
        listView.setAdapter(adapter);

        backButton.setOnClickListener(v -> {
            // Go back to the main menu or home activity
            Intent intent = new Intent(FavoriteActivity.this, MainActivity.class);
            startActivity(intent);
            finish();  // Finish the current activity so the user can't go back to it
        });
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

    private void loadFavoritesFromFile() {
        try {
            // Open the favorite.json file from internal storage
            FileInputStream fileInputStream = openFileInput("favorite.json");
            BufferedReader reader = new BufferedReader(new InputStreamReader(fileInputStream));
            StringBuilder stringBuilder = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                stringBuilder.append(line);
            }
            String json = stringBuilder.toString();

            // Check if JSON is empty or malformed
            if (json.isEmpty()) {
                Toast.makeText(this, "Favorites file is empty", Toast.LENGTH_SHORT).show();
                return;
            }

            // Parse JSON data into a list of Favorites objects using Gson
            Gson gson = new Gson();
            Type listType = new TypeToken<List<Favorites>>() {}.getType();
            favoritesList = gson.fromJson(json, listType);

            // Check if favoritesList is null (e.g., if JSON couldn't be parsed properly)
            if (favoritesList == null) {
                Toast.makeText(this, "Error parsing favorites", Toast.LENGTH_SHORT).show();
                return;
            }

            // Notify the adapter that the data has changed
            adapter.notifyDataSetChanged();
        } catch (FileNotFoundException e) {
            // Handle the case where the file doesn't exist
            e.printStackTrace();
            Toast.makeText(this, "Favorites file not found", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            // Handle IO errors
            e.printStackTrace();
            Toast.makeText(this, "Error reading the favorites file", Toast.LENGTH_SHORT).show();
        } catch (JsonSyntaxException e) {
            // Handle errors during JSON parsing
            e.printStackTrace();
            Toast.makeText(this, "Error parsing the favorites JSON", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            // Catch any other unexpected errors
            e.printStackTrace();
            Toast.makeText(this, "Unexpected error loading favorites", Toast.LENGTH_SHORT).show();
        }
    }

    // Method to delete a favorite
    public void deleteFavorite(int position) {
        // Remove the item from the list
        Favorites favoriteToDelete = favoritesList.get(position);
        favoritesList.remove(position);

        // Save the updated list back to the file
        saveFavoritesToFile();

        // Notify the adapter that the data has changed
        adapter.notifyDataSetChanged();

        // Show a toast for the deleted item
        Toast.makeText(this, favoriteToDelete.getName() + " removed from favorites", Toast.LENGTH_SHORT).show();
    }

    private void saveFavoritesToFile() {
        try {
            // Convert the favorites list to JSON using Gson
            Gson gson = new Gson();
            String json = gson.toJson(favoritesList);

            // Open the favorite.json file for writing
            FileOutputStream fileOutputStream = openFileOutput("favorite.json", MODE_PRIVATE);
            fileOutputStream.write(json.getBytes());
            fileOutputStream.close();

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Error saving favorites", Toast.LENGTH_SHORT).show();
        }
    }

    // Method to add a new favorite to the list and save it
    public void addFavorite(Favorites newFavorite) {
        favoritesList.add(newFavorite);
        saveFavoritesToFile();
        adapter.notifyDataSetChanged();
        Toast.makeText(this, newFavorite.getName() + " added to favorites", Toast.LENGTH_SHORT).show();
    }
}
