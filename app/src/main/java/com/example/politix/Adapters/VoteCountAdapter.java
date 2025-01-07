package com.example.politix.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.politix.R;
import com.example.politix.models.VoteCount;

import java.util.ArrayList;

public class VoteCountAdapter extends RecyclerView.Adapter<VoteCountAdapter.ViewHolder> {

    private ArrayList<VoteCount> voteCounts;

    public VoteCountAdapter(ArrayList<VoteCount> voteCounts) {
        this.voteCounts = voteCounts;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.vote_count_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        VoteCount voteCount = voteCounts.get(position);

        // Set candidate name and vote count in the respective TextViews
        holder.candidateNameTextView.setText(voteCount.getCandidateName());
        holder.voteCountTextView.setText(String.valueOf(voteCount.getVoteCount()));
    }

    @Override
    public int getItemCount() {
        return voteCounts.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView candidateNameTextView, voteCountTextView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            candidateNameTextView = itemView.findViewById(R.id.candidate_name_1);
            voteCountTextView = itemView.findViewById(R.id.candidate_votes_1);
        }
    }
}