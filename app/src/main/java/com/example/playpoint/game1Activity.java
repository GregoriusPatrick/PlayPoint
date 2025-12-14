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

public class game1Activity extends AppCompatActivity {

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
        setContentView(R.layout.activity_game1);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // 1. Data untuk item Mobile Legends (10 item)
        List<GameItem> items = new ArrayList<>();
        items.add(new GameItem("86 Diamonds", "Rp 25.000"));
        items.add(new GameItem("172 Diamonds", "Rp 50.000"));
        items.add(new GameItem("257 Diamonds", "Rp 75.000"));
        items.add(new GameItem("344 Diamonds", "Rp 100.000"));
        items.add(new GameItem("429 Diamonds", "Rp 125.000"));
        items.add(new GameItem("514 Diamonds", "Rp 150.000"));
        items.add(new GameItem("706 Diamonds", "Rp 200.000"));
        items.add(new GameItem("1412 Diamonds", "Rp 400.000"));
        items.add(new GameItem("2195 Diamonds", "Rp 600.000"));
        items.add(new GameItem("3688 Diamonds", "Rp 1.000.000"));

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
            Toast.makeText(game1Activity.this, item.name + " selected", Toast.LENGTH_SHORT).show();
            // (Opsional) Tambahkan logika untuk menyorot item yang dipilih
        });

        return cardView;
    }
}
