package com.example.politix.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.politix.R;
import com.example.politix.models.CandidateModel;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class ManageCandidatesAdapter extends RecyclerView.Adapter<ManageCandidatesAdapter.ViewHolder> {

    private ArrayList<CandidateModel> candidatesList;
    private OnRemoveListener onRemoveListener;

    public interface OnRemoveListener {
        void onRemove(CandidateModel candidate);
    }

    public ManageCandidatesAdapter(ArrayList<CandidateModel> candidatesList, OnRemoveListener onRemoveListener) {
        this.candidatesList = candidatesList;
        this.onRemoveListener = onRemoveListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.accepted_candidate_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        CandidateModel candidate = candidatesList.get(position);
        Picasso.get().load(candidate.getImageUrl()).into(holder.candidateImageView);
        holder.nameTextView.setText(candidate.getName());
        holder.positionTextView.setText(candidate.getPosition());

        holder.removeButton.setOnClickListener(v -> {
            if (onRemoveListener != null) {
                onRemoveListener.onRemove(candidate);
            }
        });
    }

    @Override
    public int getItemCount() {
        return candidatesList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView nameTextView, positionTextView;
        Button removeButton;
        de.hdodenhof.circleimageview.CircleImageView candidateImageView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.candidateName);
            positionTextView = itemView.findViewById(R.id.candidateRole);
            removeButton = itemView.findViewById(R.id.removeButton);
            candidateImageView = itemView.findViewById(R.id.candidateImage);
        }
    }
}
