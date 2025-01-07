package com.example.politix;

import android.content.Intent;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Date;
import java.util.HashMap;

public class Notify extends AppCompatActivity {

    private EditText descriptionInput;
    private Button postButton;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notify);

        // Initialize UI elements
        descriptionInput = findViewById(R.id.descriptionInput);
        postButton = findViewById(R.id.postButton);

        // Initialize Firebase Database reference
        databaseReference = FirebaseDatabase.getInstance().getReference("notifications");

        // Set click listener for post button
        postButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveNotification();
            }
        });
    }

    private void saveNotification() {
        String notificationText = descriptionInput.getText().toString().trim();

        // Check if input is empty
        if (notificationText.isEmpty()) {
            Toast.makeText(this, "Please enter a notification", Toast.LENGTH_SHORT).show();
            return;
        }

        // Get current timestamp
        String timestamp = DateFormat.format("yyyy-MM-dd HH:mm:ss", new Date()).toString();
        HashMap<String, Object> notificationMap = new HashMap<>();
        notificationMap.put("text", notificationText);
        notificationMap.put("timestamp", timestamp);



        // Push notification to Firebase Database
        databaseReference.push().setValue(notificationMap)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(Notify.this, "Notification sent", Toast.LENGTH_SHORT).show();
                        descriptionInput.setText(""); // Clear input
                        navigateToHome(); // Navigate back to MainActivity
                    } else {
                        Toast.makeText(Notify.this, "Failed to send notification", Toast.LENGTH_SHORT).show();
                        Log.e("Notify", "Error: " + task.getException().getMessage());
                    }
                });
    }

    private void navigateToHome() {
        Intent intent = new Intent(Notify.this, HomeActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish(); // Close current activity
    }
}
