package com.example.crapple_maps;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class FilterTrashActivity extends AppCompatActivity {

    CheckBox star1, star2, star3;
    CheckBox pizza, sushi, burgers, mexican, chinese, healthy;
    RadioGroup distanceGroup;
    CheckBox price1, price2, price3, price4;
    Button applyFiltersButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter_trash);

        // Star rating checkboxes
        star1 = findViewById(R.id.star1);
        star2 = findViewById(R.id.star2);
        star3 = findViewById(R.id.star3);

        // Cuisine type checkboxes
        pizza = findViewById(R.id.pizza);
        sushi = findViewById(R.id.sushi);
        burgers = findViewById(R.id.burgers);
        mexican = findViewById(R.id.mexican);
        chinese = findViewById(R.id.chinese);
        healthy = findViewById(R.id.healthy);

        // Distance
        distanceGroup = findViewById(R.id.distanceGroup);

        // Price
        price1 = findViewById(R.id.price1);
        price2 = findViewById(R.id.price2);
        price3 = findViewById(R.id.price3);
        price4 = findViewById(R.id.price4);

        // Button
        applyFiltersButton = findViewById(R.id.applyFiltersButton);

        applyFiltersButton.setOnClickListener(v -> {
            Intent intent = new Intent(FilterTrashActivity.this, MainActivity.class); // Replace with your target activity

            // ⭐ Star rating filters
            ArrayList<Integer> selectedStars = new ArrayList<>();
            if (star1.isChecked()) selectedStars.add(1);
            if (star2.isChecked()) selectedStars.add(2);
            if (star3.isChecked()) selectedStars.add(3);
            intent.putIntegerArrayListExtra("selectedStars", selectedStars);

            // 🍽 Cuisine filters
            ArrayList<String> selectedCuisines = new ArrayList<>();
            if (pizza.isChecked()) selectedCuisines.add("Pizza");
            if (sushi.isChecked()) selectedCuisines.add("Sushi");
            if (burgers.isChecked()) selectedCuisines.add("Burgers");
            if (mexican.isChecked()) selectedCuisines.add("Mexican");
            if (chinese.isChecked()) selectedCuisines.add("Chinese");
            if (healthy.isChecked()) selectedCuisines.add("Healthy");
            intent.putStringArrayListExtra("selectedCuisines", selectedCuisines);

            int selectedDistanceId = distanceGroup.getCheckedRadioButtonId();
            if (selectedDistanceId != -1) {
                RadioButton selectedDistance = findViewById(selectedDistanceId);
                intent.putExtra("selectedDistance", selectedDistance.getText().toString());
            }

            ArrayList<String> selectedPrices = new ArrayList<>();
            if (price1.isChecked()) selectedPrices.add("$");
            if (price2.isChecked()) selectedPrices.add("$$");
            if (price3.isChecked()) selectedPrices.add("$$$");
            if (price4.isChecked()) selectedPrices.add("$$$$");
            intent.putStringArrayListExtra("selectedPrices", selectedPrices);

            startActivity(intent);
        });
    }
}
