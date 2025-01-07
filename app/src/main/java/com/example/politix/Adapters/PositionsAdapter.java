package com.example.politix.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.politix.Fragments.VoteFragment;
import com.example.politix.R;
import com.example.politix.models.Requests;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class PositionsAdapter extends RecyclerView.Adapter<PositionsAdapter.ViewHolder> {
    VoteFragment fragment;
    List<Requests> requestslist;
    DatabaseReference ref;

    public PositionsAdapter(VoteFragment fragment, List<Requests> requestslist) {
        this.fragment = fragment;
        this.requestslist = requestslist;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(fragment.getContext()).inflate(R.layout.vote_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int i) {
        Requests requests = requestslist.get(i);

        holder.text.setText(requests.getName());
        Picasso.get().load(requests.getImage()).into(holder.img);



        holder.vote.setOnClickListener(view -> {

            fragment.VoteCandidate(requests); // Pass the full requests object
        });

        ref = FirebaseDatabase.getInstance().getReference().child("ToggleVote");
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.child("state").getValue().equals("false")) {
                    holder.vote.setClickable(false);
                    holder.vote.setText("Vote");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    @Override
    public int getItemCount() {
        return requestslist.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView text;
        public CircleImageView img;
        public Button vote;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            text = itemView.findViewById(R.id.name);
            img = itemView.findViewById(R.id.voteimg);
            vote = itemView.findViewById(R.id.votebtn);
        }
    }
}
