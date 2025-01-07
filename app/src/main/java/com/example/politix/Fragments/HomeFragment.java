package com.example.politix.Fragments;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.politix.Post;
import com.example.politix.Adapters.PostAdapter;
import com.example.politix.Post_Campaign;
import com.example.politix.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    private RecyclerView postRecyclerView;
    private PostAdapter postAdapter;
    private List<Post> postList;
    private DatabaseReference databaseReference;
    private DatabaseReference likesReference; // New reference for likes
    private String username; // Store the logged-in username
    private String isCandidate; // Store if the user is a candidate

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        // Retrieve the username and candidate status from the Intent
        if (getActivity() != null && getActivity().getIntent() != null) {
            username = getActivity().getIntent().getStringExtra("username");
            isCandidate = getActivity().getIntent().getStringExtra("isCandidate");
        }

        // Set up the RecyclerView for displaying posts
        postRecyclerView = view.findViewById(R.id.postRecyclerView);
        postRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Initialize post list and adapter
        postList = new ArrayList<>();
        postAdapter = new PostAdapter(getContext(), postList, username, this::onPostClick);
        postRecyclerView.setAdapter(postAdapter);

        // Set up the Floating Action Button
        FloatingActionButton fab = view.findViewById(R.id.floatingbutton);
        setupFloatingActionButton(fab);

        // Initialize the database references
        databaseReference = FirebaseDatabase.getInstance().getReference("posts");
        likesReference = FirebaseDatabase.getInstance().getReference("likes"); // Reference to likes

        // Retrieve posts and liked posts from Firebase and update the RecyclerView
        fetchPostsFromFirebase();

        return view;
    }

    private void setupFloatingActionButton(FloatingActionButton fab) {
        // Check if the user is a candidate
        if ("yes".equals(isCandidate)) {
            fab.setEnabled(true);
            fab.setVisibility(View.VISIBLE);
            fab.setOnClickListener(v -> {
                Intent intent = new Intent(getActivity(), Post_Campaign.class);
                intent.putExtra("username", username);
                startActivity(intent);
            });
        } else {
            fab.setEnabled(false);
            fab.setVisibility(View.GONE);
        }
    }

    private void fetchPostsFromFirebase() {
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                postList.clear(); // Clear previous posts

                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    String postId = dataSnapshot.getKey();
                    if (postId == null) {

                        continue; // Skip this iteration if postId is null
                    }

                    String postUsername = dataSnapshot.child("username").getValue(String.class);
                    String description = dataSnapshot.child("description").getValue(String.class);
                    String imageUrl = dataSnapshot.child("imageUrl").getValue(String.class);

                    // Retrieve timestamp
                    Long timestampValue = null;
                    // Try getting it as Long first
                    try {
                        timestampValue = dataSnapshot.child("timestamp").getValue(Long.class);
                    } catch (Exception e) {
                        Log.e("HomeFragment", "Error retrieving timestamp as Long: " + e.getMessage());
                    }

                    // If timestampValue is still null, try getting it as String and convert
                    if (timestampValue == null) {
                        String timestampStr = dataSnapshot.child("timestamp").getValue(String.class);
                        if (timestampStr != null) {
                            try {
                                timestampValue = Long.parseLong(timestampStr);
                            } catch (NumberFormatException e) {
                                Log.e("HomeFragment", "Error parsing timestamp: " + timestampStr);
                                timestampValue = 0L; // Default to 0 if parsing fails
                            }
                        } else {
                            timestampValue = 0L; // Default to 0 if no timestamp found
                        }
                    }

                    // Retrieve like count
                    Integer likeCountValue = null;
                    // Try getting it as Integer first
                    try {
                        likeCountValue = dataSnapshot.child("likeCount").getValue(Integer.class);
                    } catch (Exception e) {
                        Log.e("HomeFragment", "Error retrieving likeCount as Integer: " + e.getMessage());
                    }

                    // If likeCountValue is still null, try getting it as String and convert
                    if (likeCountValue == null) {
                        String likeCountStr = dataSnapshot.child("likeCount").getValue(String.class);
                        if (likeCountStr != null) {
                            try {
                                likeCountValue = Integer.parseInt(likeCountStr);
                            } catch (NumberFormatException e) {
                                Log.e("HomeFragment", "Error parsing likeCount: " + likeCountStr);
                                likeCountValue = 0; // Default to 0 if parsing fails
                            }
                        } else {
                            likeCountValue = 0; // Default to 0 if no likeCount found
                        }
                    }

                    // Log post details for debugging


                    // Create the Post object
                    Post post = new Post(postUsername, description, imageUrl, likeCountValue, timestampValue);
                    post.setPostId(postId); // Set post ID for later reference

                    // Fetch liked status
                    post.setLiked(false); // Default to not liked
                    checkIfLiked(postId, post); // Check if the user has liked this post

                    postList.add(post);
                }

                // Notify adapter of data changes
                postAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("HomeFragment", "Error fetching posts: " + error.getMessage());
            }
        });
    }



    private void checkIfLiked(String postId, Post post) {
        likesReference.child(username).child(postId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    post.setLiked(true); // Mark as liked
                }
                postAdapter.notifyDataSetChanged(); // Refresh UI
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("HomeFragment", "Error checking liked status: " + error.getMessage());
            }
        });
    }

    private void onPostClick(View view, int position) {
        Post post = postList.get(position);
        String postId = post.getPostId(); // Get the actual post ID

        // Update like count and liked status
        if (post.isLiked()) {
            post.decrementLikeCount(username);
            removeLikeFromDatabase(postId);
        } else {
            post.incrementLikeCount(username);
            addLikeToDatabase(postId);
        }

        // Update Firebase
        databaseReference.child(postId).child("likeCount").setValue(post.getLikeCount())
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        postAdapter.notifyItemChanged(position); // Refresh the like button state
                    } else {
                        // Handle failure
                        Toast.makeText(getContext(), "Failed to update like count", Toast.LENGTH_SHORT).show();
                    }
                });
    }


    private void addLikeToDatabase(String postId) {
        likesReference.child(username).child(postId).setValue(true);
    }

    private void removeLikeFromDatabase(String postId) {
        likesReference.child(username).child(postId).removeValue();
    }
}