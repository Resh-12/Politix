package com.example.politix.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.politix.CandidacyRequestActivity;
import com.example.politix.R;
import com.example.politix.models.CandidateModel;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class CandidacyRequestAdapter extends RecyclerView.Adapter<CandidacyRequestAdapter.ViewHolder> {

    private ArrayList<CandidateModel> candidateModels;
    Context context;

    public CandidacyRequestAdapter(ArrayList<CandidateModel> candidateModels, Context context) {
        this.candidateModels = candidateModels;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.candidacy_request_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        CandidateModel request = candidateModels.get(position);
        Picasso.get().load(request.getImageUrl()).into(holder.candidateImageView);
        holder.nameTextView.setText(request.getName());
        holder.positionTextView.setText(request.getPosition());

        if (request.isApproved()) {
            holder.approveButton.setVisibility(View.GONE);
            holder.declineButton.setVisibility(View.GONE);
            holder.approvedButton.setVisibility(View.VISIBLE);
        } else {
            holder.approveButton.setVisibility(View.VISIBLE);
            holder.declineButton.setVisibility(View.VISIBLE);
            holder.approvedButton.setVisibility(View.GONE);
        }

        holder.approveButton.setOnClickListener(v -> {
            ((CandidacyRequestActivity) context).addCandidateToDatabase(request);
            holder.approveButton.setText("Approved");
            holder.declineButton.setVisibility(View.GONE);
        });
        holder.declineButton.setOnClickListener(view -> {
            ((CandidacyRequestActivity)context).declineCandidate(request);
        });
    }

    @Override
    public int getItemCount() {
        return candidateModels.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView nameTextView, positionTextView;
        Button approveButton, declineButton, approvedButton;
        de.hdodenhof.circleimageview.CircleImageView candidateImageView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.candidateName);
            positionTextView = itemView.findViewById(R.id.candidateRole);
            approveButton = itemView.findViewById(R.id.approveButton);
            declineButton = itemView.findViewById(R.id.declineButton);
            approvedButton = itemView.findViewById(R.id.approvedButton);
            candidateImageView = itemView.findViewById(R.id.candidateImage);
        }
    }
}



//
//    @Override
//    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
//        CandidateModel request = candidateModels.get(position);
//        Picasso.get().load(request.getImageUrl()).into(holder.candidateImageView);
//        holder.nameTextView.setText(request.getName());
//        holder.positionTextView.setText(request.getPosition());
//
//        if (request.isApproved()) {
//            holder.approveButton.setVisibility(View.GONE);
//            holder.declineButton.setVisibility(View.GONE);
//            holder.approvedButton.setVisibility(View.VISIBLE);
//        } else {
//            holder.approveButton.setVisibility(View.VISIBLE);
//            holder.declineButton.setVisibility(View.VISIBLE);
//            holder.approvedButton.setVisibility(View.GONE);
//        }
//
//        holder.approveButton.setOnClickListener(v -> {
//            request.approve();
//            onApproveListener.onApprove(request);
//            notifyItemChanged(position);
//        });
//        holder.declineButton.setOnClickListener(view -> {
//
//        });
//    }
//
//    @Override
//    public int getItemCount() {
//        return candidateModels.size();
//    }
//
//    public static class ViewHolder extends RecyclerView.ViewHolder {
//        TextView nameTextView, positionTextView;
//        Button approveButton, declineButton, approvedButton;
//        de.hdodenhof.circleimageview.CircleImageView candidateImageView;
//
//        public ViewHolder(@NonNull View itemView) {
//            super(itemView);
//            nameTextView = itemView.findViewById(R.id.candidateName);
//            positionTextView = itemView.findViewById(R.id.candidateRole);
//            approveButton = itemView.findViewById(R.id.approveButton);
//            declineButton = itemView.findViewById(R.id.declineButton);
//            approvedButton = itemView.findViewById(R.id.approvedButton);
//            candidateImageView = itemView.findViewById(R.id.candidateImage);
//        }
//    }
//}