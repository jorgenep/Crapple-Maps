package com.example.crapple_maps;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

public class FavoritesAdapter extends BaseAdapter {

    private Context context;
    private List<Favorites> favoritesList;

    // Constructor
    public FavoritesAdapter(Context context, List<Favorites> favoritesList) {
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
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.favorite_item, null);
        }

        TextView nameView = convertView.findViewById(R.id.favorite_name);
        TextView addressView = convertView.findViewById(R.id.favorite_address);
        TextView ratingView = convertView.findViewById(R.id.favorite_rating);

        Favorites favorite = favoritesList.get(position);
        nameView.setText(favorite.getName());
        addressView.setText(favorite.getAddress());
        ratingView.setText(favorite.getRating());

        return convertView;
    }
}
//this comment is for commiting and pushing. m