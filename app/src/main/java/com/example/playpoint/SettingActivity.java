package com.example.playpoint;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.WriteBatch;

public class SettingActivity extends AppCompatActivity {

    private FirebaseFirestore db;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_setting);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        // Setup Back Button
        ImageButton backButton = findViewById(R.id.back_button);
        backButton.setOnClickListener(v -> finish());

        Button clearHistoryButton = findViewById(R.id.clear_history_button);
        TextView signOutText = findViewById(R.id.sign_out_text);
        TextView emailText = findViewById(R.id.email_text);
        TextView usernameText = findViewById(R.id.username_text);

        // Update data profil di settings
        if (currentUser != null) {
            emailText.setText(currentUser.getEmail());
            usernameText.setText(currentUser.getDisplayName() != null ? currentUser.getDisplayName() : "User");
            signOutText.setVisibility(View.VISIBLE);
        } else {
            emailText.setText("Guest");
            usernameText.setText("Guest");
            signOutText.setVisibility(View.GONE);
        }

        clearHistoryButton.setOnClickListener(v -> showClearHistoryConfirmation());

        signOutText.setOnClickListener(v -> {
            mAuth.signOut();
            Toast.makeText(SettingActivity.this, "Signed out successfully", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(SettingActivity.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });
    }

    private void showClearHistoryConfirmation() {
        new AlertDialog.Builder(this)
                .setTitle("Clear History")
                .setMessage("Are you sure you want to delete all transaction history? This action cannot be undone.")
                .setPositiveButton("Clear", (dialog, which) -> clearAllTransactions())
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void clearAllTransactions() {
        db.collection("transactions")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        WriteBatch batch = db.batch();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            batch.delete(document.getReference());
                        }

                        batch.commit().addOnCompleteListener(batchTask -> {
                            if (batchTask.isSuccessful()) {
                                Toast.makeText(SettingActivity.this, "History successfully cleared", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(SettingActivity.this, "Failed to clear history", Toast.LENGTH_SHORT).show();
                            }
                        });
                    } else {
                        Log.w("FirestoreError", "Error getting documents for deletion", task.getException());
                        Toast.makeText(SettingActivity.this, "Failed to access database", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
