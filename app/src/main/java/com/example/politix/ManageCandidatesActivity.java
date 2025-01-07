package com.example.politix;

import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.politix.Adapters.ManageCandidatesAdapter;
import com.example.politix.models.CandidateModel;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ManageCandidatesActivity extends AppCompatActivity {

    private RecyclerView manageCandidatesRecyclerView;
    private ManageCandidatesAdapter adapter;
    private ArrayList<CandidateModel> candidatesList;
    private DatabaseReference candidatesDatabaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_candidates);

        manageCandidatesRecyclerView = findViewById(R.id.manageCandidatesRecyclerView);
        manageCandidatesRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        candidatesList = new ArrayList<>();

        candidatesDatabaseReference = FirebaseDatabase.getInstance().getReference("Candidates");

        loadCandidates();
    }

    private void loadCandidates() {
        candidatesDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                candidatesList.clear();
                for (DataSnapshot candidateSnapshot : snapshot.getChildren()) {
                    String name = candidateSnapshot.child("name").getValue(String.class);
                    String position = candidateSnapshot.child("position").getValue(String.class);
                    String email = candidateSnapshot.child("email").getValue(String.class);
                    String registrationNumber = candidateSnapshot.child("regno").getValue(String.class);
                    String imageUrl = candidateSnapshot.child("image").getValue(String.class);

                    CandidateModel candidate = new CandidateModel(name, position, email, imageUrl, registrationNumber);
                    candidatesList.add(candidate);
                }

                adapter = new ManageCandidatesAdapter(candidatesList, candidate -> {
                    removeCandidateFromDatabase(candidate.getRegno());
                });

                manageCandidatesRecyclerView.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ManageCandidatesActivity.this, "Failed to load candidates.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void removeCandidateFromDatabase(String regno) {
        if (regno != null && !regno.isEmpty()) {
            candidatesDatabaseReference.child(regno).removeValue()
                    .addOnSuccessListener(aVoid -> Toast.makeText(ManageCandidatesActivity.this, "Candidate removed successfully!", Toast.LENGTH_SHORT).show())
                    .addOnFailureListener(e -> Toast.makeText(ManageCandidatesActivity.this, "Failed to remove candidate.", Toast.LENGTH_SHORT).show());
        } else {
            Toast.makeText(this, "Invalid registration number.", Toast.LENGTH_SHORT).show();
        }
    }
}