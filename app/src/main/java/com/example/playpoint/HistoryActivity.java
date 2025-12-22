package com.example.playpoint;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class HistoryActivity extends AppCompatActivity {

    private FirebaseFirestore db;
    private ListView listView;
    private HistoryAdapter adapter;
    private List<Transaction> transactionList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_history);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        listView = findViewById(R.id.history_list_view);
        adapter = new HistoryAdapter(this, transactionList);
        listView.setAdapter(adapter);

        db = FirebaseFirestore.getInstance();
        fetchTransactionHistory();

        // --- Menambahkan Logika Navigasi ---
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);

        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int itemId = item.getItemId();
                if (itemId == R.id.navigation_home) {
                    Toast.makeText(HistoryActivity.this, "Home selected", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(HistoryActivity.this, MainActivity.class);
                    startActivity(intent);
                    return true;
                } else if (itemId == R.id.navigation_profile) {
                    Toast.makeText(HistoryActivity.this, "Profile selected", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(HistoryActivity.this, ProfileActivity.class);
                    startActivity(intent);
                    return true;
                }
                return false;
            }
        });
    }

    private void fetchTransactionHistory() {
        db.collection("transactions")
                .orderBy("timestamp", Query.Direction.DESCENDING) // Urutkan berdasarkan waktu, terbaru di atas
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        transactionList.clear();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Transaction transaction = document.toObject(Transaction.class);
                            transactionList.add(transaction);
                        }
                        adapter.notifyDataSetChanged(); // Beri tahu adapter bahwa data telah berubah
                    } else {
                        Log.d("HistoryActivity", "Error getting documents: ", task.getException());
                    }
                });
    }
}
