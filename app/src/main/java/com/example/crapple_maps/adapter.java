package com.example.crapple_maps;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

public class Adapter extends ArrayAdapter<Favorite> {

    public Adapter(Context context, List<Favorite> favorites) {
        super(context, 0, favorites);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Favorite favorite = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.adapter_item, parent, false);
        }


        return convertView;
    }
}
