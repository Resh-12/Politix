package com.example.politix;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class EditProfile extends AppCompatActivity {

    private EditText etName, etEmail;
    private Button btnSubmit;

    private FirebaseAuth mAuth;
    private DatabaseReference usersRef;
    private FirebaseUser currentUser;

    private static final String TAG = "EditProfile";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile2);

        // Initialize Firebase Auth and Database reference
        mAuth = FirebaseAuth.getInstance();
        usersRef = FirebaseDatabase.getInstance().getReference("Users");
        currentUser = mAuth.getCurrentUser();

        // Initialize UI elements
        etName = findViewById(R.id.etName);
        etEmail = findViewById(R.id.etEmail);
        btnSubmit = findViewById(R.id.btnSubmit);

        // Check if a user is logged in
        if (currentUser != null) {
            // Log and fetch the current user's details

            fetchUserNameAndEmail(currentUser.getEmail()); // Call the modified method
        } else {
            // Redirect to login if no user is logged in
            Log.d(TAG, "No user is logged in; redirecting to Login.");
            startActivity(new Intent(EditProfile.this, LoginActivity.class));
            finish();  // Finish current activity
        }

        // Set up the button click listener for submitting profile updates
        btnSubmit.setOnClickListener(view -> {
            String name = etName.getText().toString().trim();
            String email = etEmail.getText().toString().trim();

            // Simple validation checks
            if (name.isEmpty()) {
                Toast.makeText(EditProfile.this, "Please enter your name", Toast.LENGTH_SHORT).show();
            } else if (email.isEmpty()) {
                Toast.makeText(EditProfile.this, "Please enter your email", Toast.LENGTH_SHORT).show();
            } else {
                // Update profile in Firebase
                updateProfile(name, email);
            }
        });
    }

    // Method to fetch user name and email
    private void fetchUserNameAndEmail(String email) {
        usersRef.orderByChild("email").equalTo(email).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                        String userName = userSnapshot.child("name").getValue(String.class);
                        String userEmail = userSnapshot.child("email").getValue(String.class);
                        if (userName != null && userEmail != null) {
                            etName.setText(userName);  // Set the name in EditText
                            etEmail.setText(userEmail);  // Set the email in EditText
                            Log.d(TAG, "User details loaded successfully.");
                        } else {
                            Toast.makeText(EditProfile.this, "Incomplete profile data", Toast.LENGTH_SHORT).show();
                            Log.d(TAG, "Incomplete profile data for email: " + email);
                        }
                    }
                } else {
                    Log.d(TAG, "User not found in database for email: " + email);
                    Toast.makeText(EditProfile.this, "User not found in database", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e(TAG, "Error retrieving user data: " + databaseError.getMessage());
                Toast.makeText(EditProfile.this, "Error retrieving user data", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Method to update profile information
    private void updateProfile(String name, String email) {
        if (currentUser != null) {
            // Update user details in the database
            usersRef.child(currentUser.getUid()).child("name").setValue(name).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Log.d(TAG, "Name updated in database.");

                    // Check if the email is different before updating in FirebaseAuth
                    if (!currentUser.getEmail().equals(email)) {
                        currentUser.updateEmail(email).addOnCompleteListener(emailTask -> {
                            if (emailTask.isSuccessful()) {
                                // Update the email field in the database as well
                                usersRef.child(currentUser.getUid()).child("email").setValue(email).addOnCompleteListener(updateTask -> {
                                    if (updateTask.isSuccessful()) {

                                        Toast.makeText(EditProfile.this, "Profile updated successfully", Toast.LENGTH_SHORT).show();
                                    } else {

                                    }
                                });
                            } else {

                                Toast.makeText(EditProfile.this, "Error updating email: " + emailTask.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    } else {
                        Toast.makeText(EditProfile.this, "Profile updated successfully", Toast.LENGTH_SHORT).show();
                    }
                } else {

                    Toast.makeText(EditProfile.this, "Error updating name: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}
