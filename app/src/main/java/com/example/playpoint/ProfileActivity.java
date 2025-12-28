package com.example.playpoint;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;
import java.util.Map;

public class ProfileActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private FirebaseStorage storage;
    private TextView usernameText;
    private ImageButton editUsernameButton, editImageButton;
    private ImageView profileImage;
    private Uri imageUri;

    // Launcher untuk memilih gambar dari galeri
    private final ActivityResultLauncher<Intent> pickImageLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    imageUri = result.getData().getData();
                    uploadImageToFirebase();
                }
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_profile);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        usernameText = findViewById(R.id.username_text);
        editUsernameButton = findViewById(R.id.edit_username_profile);
        editImageButton = findViewById(R.id.edit_profile_image_button);
        profileImage = findViewById(R.id.profile_image);

        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser != null) {
            loadUserProfile(currentUser);
            editUsernameButton.setVisibility(View.VISIBLE);
            editImageButton.setVisibility(View.VISIBLE);
        } else {
            usernameText.setText("Guest User");
            editUsernameButton.setVisibility(View.GONE);
            editImageButton.setVisibility(View.GONE);
        }

        editImageButton.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            pickImageLauncher.launch(intent);
        });

        editUsernameButton.setOnClickListener(v -> showEditUsernameDialog());

        findViewById(R.id.settings_profile).setOnClickListener(v -> startActivity(new Intent(ProfileActivity.this, SettingActivity.class)));
        findViewById(R.id.voucher_code).setOnClickListener(v -> startActivity(new Intent(ProfileActivity.this, RedeemCodeActivity.class)));
        findViewById(R.id.customer_servicer).setOnClickListener(v -> startActivity(new Intent(ProfileActivity.this, CsActivity.class)));

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.navigation_profile);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.navigation_home) {
                startActivity(new Intent(ProfileActivity.this, MainActivity.class));
                finish();
                return true;
            } else if (itemId == R.id.navigation_history) {
                startActivity(new Intent(ProfileActivity.this, HistoryActivity.class));
                finish();
                return true;
            }
            return false;
        });
    }

    private void loadUserProfile(FirebaseUser user) {
        String name = user.getDisplayName();
        usernameText.setText(name != null && !name.isEmpty() ? name : user.getEmail());

        if (user.getPhotoUrl() != null) {
            Glide.with(this)
                    .load(user.getPhotoUrl())
                    .placeholder(R.drawable.ic_profile)
                    .into(profileImage);
        }
    }

    private void uploadImageToFirebase() {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user == null || imageUri == null) return;

        Toast.makeText(this, "Uploading image...", Toast.LENGTH_SHORT).show();
        StorageReference fileRef = storage.getReference().child("profile_images/" + user.getUid() + ".jpg");

        fileRef.putFile(imageUri).addOnSuccessListener(taskSnapshot -> {
            fileRef.getDownloadUrl().addOnSuccessListener(uri -> {
                updateProfilePicture(uri);
            });
        }).addOnFailureListener(e -> {
            Toast.makeText(ProfileActivity.this, "Upload failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        });
    }

    private void updateProfilePicture(Uri uri) {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user == null) return;

        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                .setPhotoUri(uri)
                .build();

        user.updateProfile(profileUpdates).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Glide.with(ProfileActivity.this).load(uri).into(profileImage);
                Toast.makeText(ProfileActivity.this, "Profile picture updated", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showEditUsernameDialog() {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user == null) return;

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Change Username");

        final EditText input = new EditText(this);
        input.setHint("Enter new username");
        input.setText(user.getDisplayName());
        builder.setView(input);

        builder.setPositiveButton("Save", (dialog, which) -> {
            String newUsername = input.getText().toString().trim();
            if (!newUsername.isEmpty()) {
                updateUsername(newUsername);
            }
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
        builder.show();
    }

    private void updateUsername(String newUsername) {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user == null) return;

        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                .setDisplayName(newUsername)
                .build();

        user.updateProfile(profileUpdates).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                db.collection("users").document(user.getUid())
                        .update("username", newUsername)
                        .addOnSuccessListener(aVoid -> {
                            usernameText.setText(newUsername);
                            Toast.makeText(ProfileActivity.this, "Username updated", Toast.LENGTH_SHORT).show();
                        });
            }
        });
    }
}
