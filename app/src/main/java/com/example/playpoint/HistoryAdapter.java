package com.example.playpoint;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

public class HistoryAdapter extends ArrayAdapter<Transaction> {

    public HistoryAdapter(@NonNull Context context, @NonNull List<Transaction> transactions) {
        super(context, 0, transactions);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        // Dapatkan data item untuk posisi ini
        Transaction transaction = getItem(position);

        // Cek apakah view yang ada digunakan kembali, jika tidak, inflate view baru
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.history_item, parent, false);
        }

        // Lookup view untuk pengisian data
        TextView gameName = convertView.findViewById(R.id.history_game_name);
        TextView gameId = convertView.findViewById(R.id.history_game_id);
        TextView itemDetails = convertView.findViewById(R.id.history_item_details);
        TextView timestamp = convertView.findViewById(R.id.history_timestamp);

        // Isi data ke dalam template view menggunakan objek data
        if (transaction != null) {
            gameName.setText(transaction.getGame());
            gameId.setText("Game ID: " + transaction.getGameId());
            itemDetails.setText("Item: " + transaction.getItemName() + " (" + transaction.getItemPrice() + ")");
            timestamp.setText(transaction.getFormattedTimestamp());
        }

        // Kembalikan view yang sudah lengkap untuk dirender di layar
        return convertView;
    }
}
