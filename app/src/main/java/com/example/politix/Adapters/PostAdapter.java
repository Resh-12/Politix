package com.example.politix.Adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.politix.R;
import com.example.politix.Post;

import java.util.List;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.PostViewHolder> {

    private Context context;
    private List<Post> postList;
    private OnPostClickListener listener;
    private String currentUserId; // Store the current user's ID

    public interface OnPostClickListener {
        void onPostClick(View view, int position);
    }

    public PostAdapter(Context context, List<Post> postList, String userId, OnPostClickListener listener) {
        this.context = context;
        this.postList = postList;
        this.currentUserId = userId; // Initialize with the current user's ID
        this.listener = listener;
    }

    @NonNull
    @Override
    public PostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_post, parent, false);
        return new PostViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PostViewHolder holder, @SuppressLint("RecyclerView") int position) {
        Post post = postList.get(position);
        holder.textViewUsername.setText(post.getUsername());
        holder.textViewDescription.setText(post.getDescription());
        holder.textViewLikesCount.setText(String.valueOf(post.getLikeCount())); // Display like count
        Glide.with(holder.imageViewPost.getContext())
                .load(post.getImageUrl())
                .into(holder.imageViewPost);

        // Update like button based on whether the user has liked the post
        holder.imageViewLike.setImageResource(post.hasLiked(currentUserId) ? R.drawable.likeon : R.drawable.likeoff);

        // Handle like button click
        holder.imageViewLike.setOnClickListener(v -> {
            listener.onPostClick(holder.itemView, position);
        });
    }

    @Override
    public int getItemCount() {
        return postList.size();
    }

    static class PostViewHolder extends RecyclerView.ViewHolder {
        TextView textViewUsername, textViewDescription, textViewLikesCount;
        ImageView imageViewLike, imageViewPost;

        public PostViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewUsername = itemView.findViewById(R.id.textViewUsername);
            textViewDescription = itemView.findViewById(R.id.textViewDescription);
            textViewLikesCount = itemView.findViewById(R.id.textViewLikesCount);
            imageViewLike = itemView.findViewById(R.id.imageViewLike);
            imageViewPost = itemView.findViewById(R.id.imageViewPost);
        }
    }
}
