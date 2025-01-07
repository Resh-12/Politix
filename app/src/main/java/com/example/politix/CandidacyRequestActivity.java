package com.example.politix;

import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.politix.Adapters.CandidacyRequestAdapter;
import com.example.politix.models.CandidateModel;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class CandidacyRequestActivity extends AppCompatActivity {

    private RecyclerView candidateRequestsRecyclerView;
    private CandidacyRequestAdapter adapter;
    private ArrayList<CandidateModel> candidacyRequestsList;
    private DatabaseReference requestsDatabaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.candidacy_requests);

        candidateRequestsRecyclerView = findViewById(R.id.candidateRequestsRecyclerView);
        candidateRequestsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        candidateRequestsRecyclerView.setHasFixedSize(true);

        candidacyRequestsList = new ArrayList<>();
        adapter=new CandidacyRequestAdapter(candidacyRequestsList,this);
        candidateRequestsRecyclerView.setAdapter(adapter);

        requestsDatabaseReference = FirebaseDatabase.getInstance().getReference("Requests");
        loadCandidacyRequests();
//
    }

    private void loadCandidacyRequests() {
        requestsDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                candidacyRequestsList.clear();
                for (DataSnapshot requestSnapshot : snapshot.getChildren()) {
                    String name = requestSnapshot.child("name").getValue(String.class);
                    String position = requestSnapshot.child("position").getValue(String.class);
                    String email = requestSnapshot.child("email").getValue(String.class);
                    String registrationNumber = requestSnapshot.child("regno").getValue(String.class);
                    String imageUrl = requestSnapshot.child("image").getValue(String.class);

                    CandidateModel request = new CandidateModel(name, position, email, imageUrl, registrationNumber);
                    candidacyRequestsList.add(request);
                }
                adapter.notifyDataSetChanged();


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(CandidacyRequestActivity.this, "Failed to load requests.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void addCandidateToDatabase(CandidateModel request) {
        DatabaseReference candidatesDatabaseReference = FirebaseDatabase.getInstance().getReference("Candidates");

        String registrationNumber = request.getRegno();
        if (registrationNumber != null && !registrationNumber.isEmpty()) {
            candidatesDatabaseReference.child(registrationNumber).child("name").setValue(request.getName());
            candidatesDatabaseReference.child(registrationNumber).child("position").setValue(request.getPosition());
            candidatesDatabaseReference.child(registrationNumber).child("email").setValue(request.getEmail());
            candidatesDatabaseReference.child(registrationNumber).child("image").setValue(request.getImageUrl());
            candidatesDatabaseReference.child(registrationNumber).child("regno").setValue(request.getRegno());

            declineCandidate(request);
            Toast.makeText(this, "Candidate added successfully!", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Registration number is missing", Toast.LENGTH_SHORT).show();
        }
    }

    public void declineCandidate(CandidateModel request){
        DatabaseReference candidatesDatabaseReference = FirebaseDatabase.getInstance().getReference("Requests");

        String registrationNumber = request.getRegno();
        if (registrationNumber != null && !registrationNumber.isEmpty()) {
            candidatesDatabaseReference.child(registrationNumber).removeValue().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Toast.makeText(this, "Candidate declined", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Failed to decline candidate", Toast.LENGTH_SHORT).show();
                }
            });;

        } else {
            Toast.makeText(this, "Registration number is missing", Toast.LENGTH_SHORT).show();
        }
    }



}