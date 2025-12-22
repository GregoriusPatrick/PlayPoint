package com.example.playpoint;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.card.MaterialCardView;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class game1Activity extends AppCompatActivity {

    private FirebaseFirestore db;
    private GameItem selectedItem = null;
    private MaterialCardView selectedCardView = null;

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

        // Inisialisasi Firestore
        db = FirebaseFirestore.getInstance();

        // Data untuk item Mobile Legends
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

        for (GameItem item : items) {
            MaterialCardView cardView = createItemCard(item);
            gridLayout.addView(cardView);
        }

        // Listener untuk tombol konfirmasi pembayaran
        Button confirmButton = findViewById(R.id.confirm_payment_button);
        confirmButton.setOnClickListener(v -> saveTransactionToFirestore());
    }

    private void saveTransactionToFirestore() {
        EditText gameIdInput = findViewById(R.id.game_id_input);
        RadioGroup paymentGroup = findViewById(R.id.payment_method_group);

        String gameId = gameIdInput.getText().toString().trim();
        int selectedPaymentId = paymentGroup.getCheckedRadioButtonId();

        // Validasi input
        if (gameId.isEmpty()) {
            Toast.makeText(this, "User ID (Zone ID) tidak boleh kosong", Toast.LENGTH_SHORT).show();
            return;
        }
        if (selectedItem == null) {
            Toast.makeText(this, "Silakan pilih item terlebih dahulu", Toast.LENGTH_SHORT).show();
            return;
        }
        if (selectedPaymentId == -1) {
            Toast.makeText(this, "Silakan pilih metode pembayaran", Toast.LENGTH_SHORT).show();
            return;
        }

        RadioButton selectedPaymentButton = findViewById(selectedPaymentId);
        String paymentMethod = selectedPaymentButton.getText().toString();

        // Membuat data untuk disimpan
        Map<String, Object> transaction = new HashMap<>();
        transaction.put("game", "Mobile Legends");
        transaction.put("gameId", gameId);
        transaction.put("itemName", selectedItem.name);
        transaction.put("itemPrice", selectedItem.price);
        transaction.put("paymentMethod", paymentMethod);
        transaction.put("timestamp", FieldValue.serverTimestamp()); // Menambahkan waktu transaksi

        // Menyimpan data ke Firestore
        db.collection("transactions")
                .add(transaction)
                .addOnSuccessListener(documentReference -> {
                    new AlertDialog.Builder(game1Activity.this)
                            .setTitle("Transaction Success")
                            .setMessage("Terima kasih! Transaksi Anda telah berhasil dicatat.")
                            .setPositiveButton("OK", (dialog, which) -> {
                                // Kosongkan input setelah pengguna menekan OK
                                gameIdInput.setText("");
                                paymentGroup.clearCheck();
                                if (selectedCardView != null) {
                                    selectedCardView.setStrokeColor(Color.LTGRAY);
                                }
                                selectedItem = null;
                                selectedCardView = null;
                            })
                            .setIcon(android.R.drawable.ic_dialog_info)
                            .show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Gagal menyimpan transaksi.", Toast.LENGTH_SHORT).show();
                    Log.w("FirestoreError", "Error adding document", e);
                });
    }

    private MaterialCardView createItemCard(GameItem item) {
        MaterialCardView cardView = new MaterialCardView(this);
        // ... (kode styling card tetap sama)
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

        // Listener untuk memilih item
        cardView.setOnClickListener(v -> {
            // Reset warna card yang dipilih sebelumnya
            if (selectedCardView != null) {
                selectedCardView.setStrokeColor(Color.LTGRAY);
            }

            // Set item & card yang baru dipilih
            selectedItem = item;
            selectedCardView = cardView;
            selectedCardView.setStrokeColor(getColor(com.google.android.material.R.color.design_default_color_primary)); // Warna highlight

            Toast.makeText(game1Activity.this, item.name + " selected", Toast.LENGTH_SHORT).show();
        });

        return cardView;
    }
}
