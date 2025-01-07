package com.example.politix.Fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.politix.ChangePassword;
import com.example.politix.EditProfile;
import com.example.politix.HelpAndSupportActivity;
import com.example.politix.LoginActivity; // Import your LoginActivity
import com.example.politix.MyVotes;
import com.example.politix.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileFragment extends Fragment {

    RelativeLayout editProfileLayout;
    RelativeLayout changePasswordLayout;
    RelativeLayout myVotesLayout;
    RelativeLayout supportLayout;
    RelativeLayout logoutLayout;
    TextView usernameTextView; // TextView for showing the user's name
    CircleImageView profileImageView; // ImageView for displaying the user's profile image

    private FirebaseAuth mAuth;
    private DatabaseReference usersRef;
    private StorageReference storageRef;

    private static final int PICK_IMAGE_REQUEST = 1; // For image picker

    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialize Firebase Auth and Database reference to 'Users' table
        mAuth = FirebaseAuth.getInstance();
        usersRef = FirebaseDatabase.getInstance().getReference("Users");
        storageRef = FirebaseStorage.getInstance().getReference("profileImages"); // Reference for profile images
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        // Initialize the RelativeLayouts and ImageView
        editProfileLayout = view.findViewById(R.id.edit_profile_layout);
        changePasswordLayout = view.findViewById(R.id.changep_layout);
        myVotesLayout = view.findViewById(R.id.myvotes_layout);
        supportLayout = view.findViewById(R.id.support_layout);
        logoutLayout = view.findViewById(R.id.logout_layout);
        usernameTextView = view.findViewById(R.id.username); // Initialize username TextView
        profileImageView = view.findViewById(R.id.uploadimg); // Initialize ImageView

        // Get current user's email and fetch name and profile image from Firebase
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            String currentUserEmail = currentUser.getEmail();
            fetchUserNameAndProfileImage(currentUserEmail);
        }

        // Set onClickListeners for each RelativeLayout
        editProfileLayout.setOnClickListener(v -> {
            Toast.makeText(getActivity(), "Edit Profile clicked", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(getActivity(), EditProfile.class); // Create an intent to start EditProfile activity
            startActivity(intent); // Start the activity
        });

        changePasswordLayout.setOnClickListener(v -> {
            Toast.makeText(getActivity(), "Change Password clicked", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(getActivity(), ChangePassword.class); // Update this to your ChangedPassword activity class
            startActivity(intent); // Start the activity
        });

        myVotesLayout.setOnClickListener(v -> {
            Toast.makeText(getActivity(), "My Votes clicked", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(getActivity(), MyVotes.class); // Create an intent to start MyVotes activity
            startActivity(intent); // Start the activity
        });

        supportLayout.setOnClickListener(v -> {
            Toast.makeText(getActivity(), "Support and Help clicked", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(getActivity(), HelpAndSupportActivity.class);
            startActivity(intent);
        });

        logoutLayout.setOnClickListener(v -> {
            // Sign out the user
            mAuth.signOut();

            // Show a toast message for confirmation
            Toast.makeText(getActivity(), "Logged out successfully", Toast.LENGTH_SHORT).show();

            // Start the login activity
            Intent intent = new Intent(getActivity(), LoginActivity.class); // Replace with your LoginActivity class
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK); // Clear activity stack
            startActivity(intent);
            getActivity().finish(); // Finish the current activity to prevent going back to the profile fragment
        });

        // Allow user to upload a new profile image
        profileImageView.setOnClickListener(v -> openImageChooser());

        return view;
    }

    // Method to fetch the user's name and profile image from the Firebase database using their email
    private void fetchUserNameAndProfileImage(String email) {
        usersRef.orderByChild("email").equalTo(email).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                        String userName = userSnapshot.child("name").getValue(String.class);
                        usernameTextView.setText(userName); // Set the user's name in the TextView

                        // Fetch the profile image URL
                        String profileImageUrl = userSnapshot.child("profileImageUrl").getValue(String.class);
                        if (profileImageUrl != null && !profileImageUrl.isEmpty()) {
                            // Load the profile image using Picasso
                            Picasso.get().load(profileImageUrl).into(profileImageView);
                        } else {
                            // Set a default image if no profile image exists
                            profileImageView.setImageResource(R.drawable.upload);
                        }
                    }
                } else {
                    Toast.makeText(getActivity(), "User not found", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(getActivity(), "Error retrieving user data", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Method to open image chooser
    private void openImageChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Profile Image"), PICK_IMAGE_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == getActivity().RESULT_OK && data != null && data.getData() != null) {
            Uri imageUri = data.getData();
            uploadProfileImage(imageUri); // Call the method to upload the image
        }
    }

    // Method to upload the selected profile image to Firebase
    private void uploadProfileImage(Uri imageUri) {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            String currentUserEmail = currentUser.getEmail();
            String currentUserId = currentUser.getUid();
            StorageReference fileReference = storageRef.child(currentUserId + ".jpg"); // Create a reference for the image

            fileReference.putFile(imageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                    if (task.isSuccessful()) {
                        fileReference.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                            @Override
                            public void onComplete(@NonNull Task<Uri> task) {
                                if (task.isSuccessful()) {
                                    String downloadUrl = task.getResult().toString();
                                    updateProfileImageInDatabase(currentUserEmail, downloadUrl);
                                } else {
                                    Toast.makeText(getActivity(), "Error getting download URL", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    } else {
                        Toast.makeText(getActivity(), "Error uploading image", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    // Method to update the profile image URL in the Firebase database
    private void updateProfileImageInDatabase(String email, String imageUrl) {
        usersRef.orderByChild("email").equalTo(email).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                        userSnapshot.getRef().child("profileImageUrl").setValue(imageUrl).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    // Load the new image into the ImageView
                                    Picasso.get().load(imageUrl).into(profileImageView);
                                    Toast.makeText(getActivity(), "Profile image updated successfully", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(getActivity(), "Error updating profile image", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                } else {
                    Toast.makeText(getActivity(), "User not found", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(getActivity(), "Error updating profile image", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
