package com.example.playpoint;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;

public class CsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cs);

        Spinner gameSpinner = findViewById(R.id.game_spinner);
        TextInputEditText complaintInput = findViewById(R.id.complaintInput);
        Button submitButton = findViewById(R.id.submitButton);

        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.games_array, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        gameSpinner.setAdapter(adapter);

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String selectedGame = gameSpinner.getSelectedItem().toString();
                String complaint = complaintInput.getText().toString();
                if (complaint.isEmpty()) {
                    Toast.makeText(CsActivity.this, "Harap isi keluhan Anda", Toast.LENGTH_SHORT).show();
                } else {
                    // TODO: Implement submission logic
                    Toast.makeText(CsActivity.this, "Keluhan untuk " + selectedGame + " telah dikirim", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}