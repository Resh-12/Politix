package com.example.politix.Fragments;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.politix.Adapters.PositionsAdapter;
import com.example.politix.SendRequest;
import com.example.politix.Notifications;
import com.example.politix.PastRecords;
import com.example.politix.R;
import com.example.politix.models.Requests;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;

public class VoteFragment extends Fragment {

    ArrayList<Requests> Preslist, ViceList, SecrList;
    RecyclerView recyclerView, vicerecycler, secr_recycler;
    PositionsAdapter Presadapter, Viceadapter, SecrAdapter;
    DatabaseReference data, vote, toggle;
    RelativeLayout preslayout, vicelayout, secrlayout;
    TextView voteclose;
    boolean isToggle = false;
    FirebaseFirestore db;
    String userId;

    public VoteFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        db = FirebaseFirestore.getInstance();
        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.vote_menu, menu);
        for (int i = 0; i < menu.size(); i++) {
            MenuItem item = menu.getItem(i);
            SpannableString spannableString = new SpannableString(item.getTitle());
            spannableString.setSpan(new ForegroundColorSpan(Color.BLACK), 0, spannableString.length(), 0);
            item.setTitle(spannableString);
        }
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.past_record) {
            startActivity(new Intent(getContext(), PastRecords.class));
        } else if (id == R.id.request) {
            startActivity(new Intent(getContext(), SendRequest.class));
        } else if (id == R.id.notif) {
            startActivity(new Intent(getContext(), Notifications.class));
        } else if (id == R.id.eligib) {
            showDialog();
        }
        return super.onOptionsItemSelected(item);
    }

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_vote, container, false);
        recyclerView = view.findViewById(R.id.Pres_recycle);
        vicerecycler = view.findViewById(R.id.VicePres_recycle);
        secr_recycler = view.findViewById(R.id.Secretary_recycle);
        preslayout = view.findViewById(R.id.contain);
        vicelayout = view.findViewById(R.id.vice_contain);
        secrlayout = view.findViewById(R.id.secretary_contain);
        voteclose = view.findViewById(R.id.close);


        isEnable();

        // RecyclerView setup
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        vicerecycler.setHasFixedSize(true);
        vicerecycler.setLayoutManager(new LinearLayoutManager(getActivity()));
        secr_recycler.setHasFixedSize(true);
        secr_recycler.setLayoutManager(new LinearLayoutManager(getActivity()));

        // Initialize lists and adapters
        Preslist = new ArrayList<>();
        ViceList = new ArrayList<>();
        SecrList = new ArrayList<>();
        data = FirebaseDatabase.getInstance().getReference().child("Candidates");

        Presadapter = new PositionsAdapter(this, Preslist);
        recyclerView.setAdapter(Presadapter);
        Viceadapter = new PositionsAdapter(this, ViceList);
        vicerecycler.setAdapter(Viceadapter);
        SecrAdapter = new PositionsAdapter(this, SecrList);
        secr_recycler.setAdapter(SecrAdapter);
        loadtdata();

        return view;
    }

    private void loadtdata() {
        data.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Preslist.clear();
                ViceList.clear();
                SecrList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Requests model = dataSnapshot.getValue(Requests.class);

                    // Set the id manually if it's not correctly mapped
                    if (model != null && model.getId() == null) {
                        model.setId(dataSnapshot.getKey());  // Using Firebase key as id if missing
                    }

                    if (model != null) {
                        switch (model.getPosition()) {
                            case "President":
                                Preslist.add(model);
                                break;
                            case "Vice President":
                                ViceList.add(model);
                                break;
                            case "Secretary":
                                SecrList.add(model);
                                break;
                        }
                    }
                }
                Presadapter.notifyDataSetChanged();
                Viceadapter.notifyDataSetChanged();
                SecrAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }


    public void VoteCandidate(Requests candidate) {
        String candidateId = candidate.getId();
        String candidateName = candidate.getName();
        String position = candidate.getPosition();

        // Log the candidateId to check if it's correctly fetched


        if (candidateId == null || candidateId.isEmpty()) {
            Toast.makeText(getContext(), "Error: Candidate ID is missing for " + position, Toast.LENGTH_SHORT).show();
            return;  // Exit if candidateId is missing
        }

        vote = FirebaseDatabase.getInstance().getReference().child("VoteState").child(userId);
        vote.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.child(position + "Voted").exists()) {
                    
                    Toast.makeText(getContext(), "You have already voted for " + position, Toast.LENGTH_LONG).show();
                } else {
                    HashMap<String, Object> data = new HashMap<>();
                    data.put(position + "Voted", true);
                    vote.updateChildren(data).addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            saveVoteToFirestore(candidateId, candidateName, position);
                            saveVoteToVoteState(candidateId, position);  // Save candidate ID and Voted status
                        } else {
                            Toast.makeText(getContext(), "Error submitting vote. Please try again.", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    private void saveVoteToVoteState(String candidateId, String position) {
        DatabaseReference voteRef = FirebaseDatabase.getInstance().getReference("VoteState").child(userId);

        // Initialize a HashMap to hold both the candidate ID and "Voted" status
        HashMap<String, Object> voteData = new HashMap<>();


        switch (position) {
            case "President":
                voteData.put("PresidentID", candidateId);
                voteData.put("PresidentVoted", true);
                break;
            case "Vice President":
                voteData.put("VicePresidentID", candidateId);
                voteData.put("VicePresidentVoted", true);
                break;
            case "Secretary":
                voteData.put("SecretaryID", candidateId);
                voteData.put("SecretaryVoted", true);
                break;
            default:
                Toast.makeText(getContext(), "Unknown position: " + position, Toast.LENGTH_SHORT).show();
                return;  // Exit if the position is invalid
        }

        // Update the VoteState document in Realtime Database with the voteData map
        voteRef.updateChildren(voteData).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(getContext(), "Voted for " + position , Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getContext(), "Error updating vote in VoteState: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(e ->
                Toast.makeText(getContext(), "Error updating vote in VoteState: " + e.getMessage(), Toast.LENGTH_SHORT).show()
        );
    }

    private void saveVoteToFirestore(String candidateId, String candidateName, String position) {
        VoteState voteState = new VoteState(candidateId, candidateName, position, userId);
        db.collection("VoteState")
                .document(candidateId + "_" + userId)
                .set(voteState)
                .addOnSuccessListener(aVoid -> Toast.makeText(getContext(), "Vote recorded successfully!", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> Toast.makeText(getContext(), "Error saving vote: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    private void showDialog() {
        LayoutInflater li = getLayoutInflater();
        View dialogue = li.inflate(R.layout.eligibility, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setView(dialogue);
        builder.setPositiveButton("Okay", (dialog, which) -> dialog.dismiss());
        AlertDialog dialog1 = builder.create();
        dialog1.show();
    }

    private void isEnable() {
        toggle = FirebaseDatabase.getInstance().getReference().child("ToggleVote");
        toggle.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.child("state").getValue().equals("false")) {
                    voteclose.setVisibility(View.VISIBLE);
                    preslayout.setAlpha(0.6f);
                    vicelayout.setAlpha(0.6f);
                    secrlayout.setAlpha(0.6f);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    public class VoteState {
        private String candidateId;
        private String candidateName;
        private String position;
        private String userId;

        public VoteState() {
        }

        public VoteState(String candidateId, String candidateName, String position, String userId) {
            this.candidateId = candidateId;
            this.candidateName = candidateName;
            this.position = position;
            this.userId = userId;
        }

        public String getCandidateId() { return candidateId; }
        public String getCandidateName() { return candidateName; }
        public String getPosition() { return position; }
        public String getUserId() { return userId; }
    }
}