package com.example.politix;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class Post_Campaign extends AppCompatActivity {

    private EditText description;
    private ImageView postImage;
    private Button postButton;
    private Uri imageUri;
    private String username;
    private ProgressDialog progressDialog;

    private DatabaseReference postsRef, likesUsersRef;
    private StorageReference storageRef;

    private static final int PICK_IMAGE_REQUEST = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_campaign);

        description = findViewById(R.id.description);
        postImage = findViewById(R.id.postImage);
        postButton = findViewById(R.id.postButton);
        progressDialog = new ProgressDialog(this);

        postsRef = FirebaseDatabase.getInstance().getReference("posts");
        likesUsersRef = FirebaseDatabase.getInstance().getReference("likesUsers");
        storageRef = FirebaseStorage.getInstance().getReference("post_images");

        Intent intent = getIntent();
        username = intent.getStringExtra("username");

        postImage.setOnClickListener(v -> openImageChooser());

        postButton.setOnClickListener(v -> {
            String desc = description.getText().toString().trim();
            if (desc.isEmpty() || imageUri == null) {
                Toast.makeText(this, "Please provide a description and an image.", Toast.LENGTH_SHORT).show();
                return;
            }
            uploadCampaign(desc);
        });
    }

    private void openImageChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Image"), PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
                postImage.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void uploadCampaign(String desc) {
        progressDialog.setTitle("Posting Campaign");
        progressDialog.setMessage("Please wait...");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();

        // Generate a unique post ID and get the current timestamp
        String postId = postsRef.push().getKey();
        String timestamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());

        // Check if imageUri is not null before proceeding
        if (imageUri != null) {
            StorageReference fileRef = storageRef.child(postId + ".jpg");
            Log.d("Post_Campaign", "Uploading image for post ID: " + postId);

            // Start uploading the image
            fileRef.putFile(imageUri)
                    .addOnSuccessListener(taskSnapshot -> {
                        // Get the download URL after successful upload
                        fileRef.getDownloadUrl()
                                .addOnSuccessListener(uri -> {
                                    String imageUrl = uri.toString();
                                    savePostToDatabase(postId, desc, imageUrl, timestamp);
                                })
                                .addOnFailureListener(e -> {
                                    // Handle the failure to get the download URL
                                    progressDialog.dismiss();
                                    Log.e("Post_Campaign", "Failed to get download URL: " + e.getMessage());
                                    Toast.makeText(Post_Campaign.this, "Failed to retrieve image URL", Toast.LENGTH_SHORT).show();
                                });
                    })
                    .addOnFailureListener(e -> {
                        // Handle the failure of the upload
                        progressDialog.dismiss();
                        Log.e("Post_Campaign", "Image upload failed: " + e.getMessage());
                        Toast.makeText(Post_Campaign.this, "Image upload failed", Toast.LENGTH_SHORT).show();
                    });
        } else {
            // Handle the case where imageUri is null
            progressDialog.dismiss();
            Log.e("Post_Campaign", "Image URI is null.");
            Toast.makeText(this, "Please select an image to upload.", Toast.LENGTH_SHORT).show();
        }
    }


    private void savePostToDatabase(String postId, String desc, String imageUrl, String timestamp) {
        Map<String, Object> postMap = new HashMap<>();
        postMap.put("postId", postId);
        postMap.put("username", username);
        postMap.put("description", desc);
        postMap.put("imageUrl", imageUrl);
        postMap.put("timestamp", timestamp);
        postMap.put("likeCount", 0);

        postsRef.child(postId).setValue(postMap).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                // Initialize likes for the current user for this post
                Map<String, Object> likeStatusMap = new HashMap<>();
                likeStatusMap.put("isLiked", false);
                likesUsersRef.child(username).child(postId).setValue(likeStatusMap);

                progressDialog.dismiss();
                Toast.makeText(Post_Campaign.this, "Campaign posted successfully", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                progressDialog.dismiss();
                Toast.makeText(Post_Campaign.this, "Failed to post campaign", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
