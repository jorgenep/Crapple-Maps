package com.example.crapple_maps;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

public class Adapter extends ArrayAdapter<Favorites> {

    public Adapter(Context context, List<Favorites> favoritesList) {
        super(context, 0, favoritesList);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Favorites favorite = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_favorite, parent, false);
        }

        TextView nameTextView = convertView.findViewById(R.id.nameTextView);
        TextView addressTextView = convertView.findViewById(R.id.addressTextView);
        TextView ratingTextView = convertView.findViewById(R.id.ratingTextView);

        nameTextView.setText(favorite.getName());
        addressTextView.setText(favorite.getAddress());
        ratingTextView.setText("Rating: " + favorite.getRating());

        return convertView;
    }
}
