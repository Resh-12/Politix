package com.example.politix;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

public class HelpAndSupportActivity extends AppCompatActivity {

    private TextView contactNumber, emailAddress;
    private ImageView phoneImage, emailImage;

    TextView teamName1, teamMail1;
    ImageView teamImg1;

    TextView teamName2, teamMail2;
    ImageView teamImg2;

    TextView teamName3, teamMail3;
    ImageView teamImg3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_help_and_support);
        // Initialize UI elements for contact section
        contactNumber = findViewById(R.id.number);  // "+91 9902574384"
        emailAddress = findViewById(R.id.mail);  // "help@college.com"
        phoneImage = findViewById(R.id.phoneimg);  // Phone icon
        emailImage = findViewById(R.id.mailimg);  // Email icon

        // Initialize UI elements for team section
        teamName1 = findViewById(R.id.teamname1);
        teamMail1 = findViewById(R.id.teammail1);
        teamImg1 = findViewById(R.id.teamimg1);

        teamName2 = findViewById(R.id.teamname2);
        teamMail2 = findViewById(R.id.teammail2);
        teamImg2 = findViewById(R.id.teamimg2);

        teamName3 = findViewById(R.id.teamname3);
        teamMail3 = findViewById(R.id.teammail3);
        teamImg3 = findViewById(R.id.teamimg3);
    }
}