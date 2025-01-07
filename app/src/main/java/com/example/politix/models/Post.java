package com.example.politix;

import java.util.ArrayList;
import java.util.List;

public class Post {
    private String postId;               // Unique identifier for the post
    private String username;             // Username of the post author
    private String description;          // Description of the post
    private String imageUrl;             // URL for the post image
    private long timestamp;              // Timestamp of when the post was created
    private int likeCount;               // Count of likes for the post
    private List<String> likedUsers;     // List to store users who liked the post
    private boolean isLiked;              // Track if the post is liked by the current user

    // Default constructor
    public Post() {
        this.likedUsers = new ArrayList<>(); // Initialize likedUsers list
        this.likeCount = 0;                   // Initialize like count
        this.isLiked = false;                  // Initialize isLiked to false
    }

    // Parameterized constructor
    public Post(String username, String description, String imageUrl, int likeCount, long timestamp) {
        this.username = username;
        this.description = description;
        this.imageUrl = imageUrl;
        this.likeCount = likeCount;
        this.timestamp = timestamp;
        this.likedUsers = new ArrayList<>(); // Initialize likedUsers list
        this.isLiked = false;                  // Initialize isLiked to false
    }

    // Getter and Setter methods
    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public int getLikeCount() {
        return likeCount;
    }

    public void setLikeCount(int likeCount) {
        this.likeCount = likeCount;
    }

    public List<String> getLikedUsers() {
        return likedUsers;
    }

    public void setLikedUsers(List<String> likedUsers) {
        this.likedUsers = likedUsers;
    }

    public boolean isLiked() {
        return isLiked;
    }

    public void setLiked(boolean liked) {
        isLiked = liked;
    }

    // Methods to handle likes
    public void incrementLikeCount(String userId) {
        if (!likedUsers.contains(userId)) {
            likedUsers.add(userId);
            likeCount++;
            isLiked = true; // Set isLiked to true when liked
        }
    }

    public void decrementLikeCount(String userId) {
        if (likedUsers.contains(userId)) {
            likedUsers.remove(userId);
            likeCount--; // Decrease like count
            isLiked = false; // Update liked state
        }
    }

    public boolean hasLiked(String userId) {
        return likedUsers.contains(userId);
    }
}