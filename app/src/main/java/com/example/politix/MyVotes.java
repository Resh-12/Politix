package com.example.politix;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class MyVotes extends AppCompatActivity {

    TextView pastAction, presName, presRole, vpName, vpRole, secName, secRole;
    de.hdodenhof.circleimageview.CircleImageView presImage, vpImage, secImage;
    RelativeLayout presLayout23, vpLayout23, secLayout23;
    DatabaseReference voteStateRef;
    String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_votes);

        pastAction = findViewById(R.id.pastaction);
        presLayout23 = findViewById(R.id.preslayout23);
        presImage = findViewById(R.id.pimg23);
        presName = findViewById(R.id.pname23);
        presRole = findViewById(R.id.pres23);
        vpLayout23 = findViewById(R.id.vplayout23);
        vpImage = findViewById(R.id.vpimg23);
        vpName = findViewById(R.id.vpname23);
        vpRole = findViewById(R.id.vpres23);
        secLayout23 = findViewById(R.id.seclayout23);
        secImage = findViewById(R.id.secimg23);
        secName = findViewById(R.id.secname23);
        secRole = findViewById(R.id.sec23);

        pastAction.setText("My Votes");

        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        voteStateRef = FirebaseDatabase.getInstance().getReference("VoteState").child(userId);

        loadVotedCandidates();
    }

    private void loadVotedCandidates() {
        voteStateRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    // Load President data
                    if (snapshot.child("PresidentID").exists()) {
                        String presidentId = snapshot.child("PresidentID").getValue(String.class);
                        loadCandidateDetails(presidentId, presName, presRole, presImage, "President");
                    }

                    // Load Vice President data
                    if (snapshot.child("VicePresidentID").exists()) {
                        String vicePresidentId = snapshot.child("VicePresidentID").getValue(String.class);
                        loadCandidateDetails(vicePresidentId, vpName, vpRole, vpImage, "Vice President");
                    }

                    // Load Secretary data
                    if (snapshot.child("SecretaryID").exists()) {
                        String secretaryId = snapshot.child("SecretaryID").getValue(String.class);
                        loadCandidateDetails(secretaryId, secName, secRole, secImage, "Secretary");
                    }
                } else {
                    Toast.makeText(MyVotes.this, "No votes found for this user.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(MyVotes.this, "Failed to load votes: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Helper method to load candidate details by ID
    private void loadCandidateDetails(String candidateId, TextView nameView, TextView roleView, ImageView imageView, String role) {
        DatabaseReference candidateRef = FirebaseDatabase.getInstance().getReference("Candidates").child(candidateId);
        candidateRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String name = snapshot.child("name").getValue(String.class);
                    String imageUrl = snapshot.child("image").getValue(String.class);

                    nameView.setText(name);
                    roleView.setText(role);
                    if (imageUrl != null && !imageUrl.isEmpty()) {
                        Picasso.get().load(imageUrl).into(imageView);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(MyVotes.this, "Failed to load candidate details: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
