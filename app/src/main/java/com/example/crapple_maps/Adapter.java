package com.example.crapple_maps;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import java.io.FileOutputStream;
import java.util.List;

public class Adapter extends BaseAdapter {

    private Context context;
    private List<Favorites> favoritesList;

    public Adapter(Context context, List<Favorites> favoritesList) {
        this.context = context;
        this.favoritesList = favoritesList;
    }

    @Override
    public int getCount() {
        return favoritesList.size();
    }

    @Override
    public Object getItem(int position) {
        return favoritesList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.item_favorite, parent, false);
        }

        // Get the favorite item at the current position
        Favorites favorite = favoritesList.get(position);

        // Bind data to the view
        TextView nameTextView = view.findViewById(R.id.nameTextView);
        nameTextView.setText(favorite.getName());

        // Set up the Delete button for the item
        //Button deleteButton = view.findViewById(R.id.deleteButton);
        //deleteButton.setOnClickListener(v -> {
            // Remove the item from the list
        //    deleteFavorite(position);
        //});

        return view;
    }

    private void deleteFavorite(int position) {
        // Remove the item from the list
        Favorites favoriteToDelete = favoritesList.get(position);
        favoritesList.remove(position);

        // Save the updated list back to the file
        saveFavoritesToFile();

        // Notify the adapter that the data has changed
        notifyDataSetChanged();

        // Show a toast for the deleted item
        Toast.makeText(context, favoriteToDelete.getName() + " removed from favorites", Toast.LENGTH_SHORT).show();
    }

    private void saveFavoritesToFile() {
        try {
            // Convert the favorites list to JSON using Gson
            Gson gson = new Gson();
            String json = gson.toJson(favoritesList);

            // Open the favorite.json file for writing
            FileOutputStream fileOutputStream = context.openFileOutput("favorite.json", Context.MODE_PRIVATE);
            fileOutputStream.write(json.getBytes());
            fileOutputStream.close();

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(context, "Error saving favorites", Toast.LENGTH_SHORT).show();
        }
    }
}
