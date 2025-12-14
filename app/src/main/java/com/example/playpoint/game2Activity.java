package com.example.playpoint;

import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.card.MaterialCardView;

import java.util.ArrayList;
import java.util.List;

public class game2Activity extends AppCompatActivity {

    // Kelas sederhana untuk menyimpan data item
    private static class GameItem {
        String name;
        String price;

        GameItem(String name, String price) {
            this.name = name;
            this.price = price;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_game2);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // 1. Data untuk item Roblox (8 item)
        List<GameItem> items = new ArrayList<>();
        items.add(new GameItem("80 Robux", "Rp 15.000"));
        items.add(new GameItem("400 Robux", "Rp 75.000"));
        items.add(new GameItem("800 Robux", "Rp 150.000"));
        items.add(new GameItem("1,700 Robux", "Rp 300.000"));
        items.add(new GameItem("4,500 Robux", "Rp 750.000"));
        items.add(new GameItem("10,000 Robux", "Rp 1.500.000"));
        items.add(new GameItem("22,500 Robux", "Rp 3.000.000"));
        items.add(new GameItem("Weekly Premium", "Rp 75.000"));

        GridLayout gridLayout = findViewById(R.id.item_selection_grid);

        // 2. Loop melalui data dan buat CardView untuk setiap item
        for (GameItem item : items) {
            MaterialCardView cardView = createItemCard(item);
            gridLayout.addView(cardView);
        }
    }

    private MaterialCardView createItemCard(GameItem item) {
        // Membuat CardView secara programatis
        MaterialCardView cardView = new MaterialCardView(this);
        GridLayout.LayoutParams params = new GridLayout.LayoutParams();
        params.width = 0;
        params.setMargins(8, 8, 8, 8);
        params.columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f);
        cardView.setLayoutParams(params);
        cardView.setRadius(16);
        cardView.setStrokeWidth(2);
        cardView.setStrokeColor(Color.LTGRAY);
        cardView.setClickable(true);
        cardView.setFocusable(true);

        // LinearLayout di dalam CardView
        LinearLayout linearLayout = new LinearLayout(this);
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        linearLayout.setPadding(32, 32, 32, 32);
        linearLayout.setGravity(Gravity.CENTER);

        TextView itemName = new TextView(this);
        itemName.setText(item.name);
        itemName.setTextAppearance(android.R.style.TextAppearance_Material_Body1);
        itemName.setGravity(Gravity.CENTER);

        TextView itemPrice = new TextView(this);
        itemPrice.setText(item.price);
        itemPrice.setGravity(Gravity.CENTER);
        itemPrice.setPadding(0, 8, 0, 0);

        linearLayout.addView(itemName);
        linearLayout.addView(itemPrice);
        cardView.addView(linearLayout);

        // Menambahkan listener untuk memberikan feedback saat di-klik
        cardView.setOnClickListener(v -> {
            Toast.makeText(game2Activity.this, item.name + " selected", Toast.LENGTH_SHORT).show();
            // (Opsional) Tambahkan logika untuk menyorot item yang dipilih
        });

        return cardView;
    }
}
