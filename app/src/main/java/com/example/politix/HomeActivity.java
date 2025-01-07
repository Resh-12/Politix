package com.example.politix;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class HomeActivity extends AppCompatActivity {
    FirebaseAuth auth;
    ToggleButton toggle;
    DatabaseReference ref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_activity);

        Intent new_intent = getIntent();
        String username = new_intent.getStringExtra("username");

        // Candidacy Requests Option
        TextView candidacyRequestsText = findViewById(R.id.can_req);
        ImageView candidacyRequestsImage = findViewById(R.id.can_req_img);
        ImageView candidacyRequestsArrow = findViewById(R.id.can_req_arrow);
        toggle=findViewById(R.id.toggle);
        ref= FirebaseDatabase.getInstance().getReference().child("ToggleVote");
        auth= FirebaseAuth.getInstance();
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.child("state").getValue().equals("true")){
                    toggle.setChecked(true);

                }else{
                    toggle.setChecked(false);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        toggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(toggle.isChecked()){
                    ref.child("state").setValue("true");
                }else{
                    ref.child("state").setValue("false");
                }
            }
        });
        findViewById(R.id.candidacy_requests_section).setOnClickListener(view -> {
            Intent intent = new Intent(HomeActivity.this, CandidacyRequestActivity.class);
            startActivity(intent);
        });
        findViewById(R.id.manage_candidates_section).setOnClickListener(view -> {
            Intent intent = new Intent(HomeActivity.this, ManageCandidatesActivity.class);
            startActivity(intent);
        });
        findViewById(R.id.vote_count_section).setOnClickListener(view -> {
            Intent intent = new Intent(HomeActivity.this, VoteCountActivity.class);
            startActivity(intent);
        });
        findViewById(R.id.notify_section).setOnClickListener(view -> {
            Intent intent = new Intent(HomeActivity.this, Notify.class);
            startActivity(intent);
        });
        findViewById(R.id.logout_section).setOnClickListener(view -> {
            Intent intent = new Intent(HomeActivity.this, LoginActivity.class);
            startActivity(intent);
        });






        // Disable Voting Button

        // Floating Action Button
        FloatingActionButton floatingActionButton = findViewById(R.id.floatingActionButton);
        floatingActionButton.setOnClickListener(v -> {
            String name="admin";
            Intent intent = new Intent(HomeActivity.this, Post_Campaign.class);
            intent.putExtra("username", name);
            startActivity(intent);        });
    }
}
