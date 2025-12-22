package com.example.playpoint;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ProfileActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_profile);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Tampilkan Nama Pengguna atau "Guest"
        TextView usernameText = findViewById(R.id.username_text);
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        if (currentUser != null) {
            String name = currentUser.getDisplayName();
            if (name != null && !name.isEmpty()) {
                usernameText.setText(name);
            } else {
                usernameText.setText(currentUser.getEmail());
            }
        } else {
            usernameText.setText("Guest User");
        }

        // Navigasi ke Settings
        findViewById(R.id.settings_profile).setOnClickListener(v -> {
            startActivity(new Intent(ProfileActivity.this, SettingActivity.class));
        });

        // Navigasi ke Redeem Voucher (contoh)
        findViewById(R.id.voucher_code).setOnClickListener(v -> {
            startActivity(new Intent(ProfileActivity.this, RedeemCodeActivity.class));
        });

        findViewById(R.id.customer_servicer).setOnClickListener(v -> {
            startActivity(new Intent(ProfileActivity.this, CsActivity.class));


        });


        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.navigation_profile);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.navigation_home) {
                startActivity(new Intent(ProfileActivity.this, MainActivity.class));
                return true;
            } else if (itemId == R.id.navigation_history) {
                startActivity(new Intent(ProfileActivity.this, HistoryActivity.class));
                return true;
            }
            return false;
        });
    }
}
