package com.example.playpoint;

import com.google.firebase.Timestamp;

import java.text.SimpleDateFormat;
import java.util.Locale;

public class Transaction {
    private String game;
    private String gameId;
    private String itemName;
    private String itemPrice;
    private Timestamp timestamp;

    // Diperlukan konstruktor kosong untuk Firestore
    public Transaction() {}

    public String getGame() {
        return game;
    }

    public String getGameId() {
        return gameId;
    }

    public String getItemName() {
        return itemName;
    }

    public String getItemPrice() {
        return itemPrice;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public String getFormattedTimestamp() {
        if (timestamp != null) {
            SimpleDateFormat sdf = new SimpleDateFormat("dd MMMM yyyy, HH:mm", Locale.getDefault());
            return sdf.format(timestamp.toDate());
        }
        return "No date";
    }
}
