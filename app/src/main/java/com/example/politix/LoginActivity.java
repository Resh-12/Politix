package com.example.politix;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class LoginActivity extends AppCompatActivity {
    EditText usermail, userpass;
    Button btn;
    TextView txt, forgot;
    ProgressDialog progress;
    FirebaseAuth mAuth;
    DatabaseReference ref;
    boolean isAdmin = false;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);
        usermail = findViewById(R.id.mail);
        userpass = findViewById(R.id.password);
        btn = findViewById(R.id.login);
        txt = findViewById(R.id.no_acc);
        ref = FirebaseDatabase.getInstance().getReference().child("Admin");

        txt.setOnClickListener(view -> {
            startActivity(new Intent(LoginActivity.this, SignUpActivity.class));
        });

        forgot = findViewById(R.id.forgot);
        forgot.setOnClickListener(view -> {
            startActivity(new Intent(LoginActivity.this,ForgotPassword.class));
            finish();
        });

        mAuth = FirebaseAuth.getInstance();
        progress = new ProgressDialog(this);

        btn.setOnClickListener(view -> {
            if (!usermail.getText().toString().trim().isEmpty() && emailChecker(usermail.getText().toString().trim())) {
                if (!userpass.getText().toString().isEmpty()) {
                    progress.setTitle("Login");
                    progress.setMessage("Logging in");
                    progress.setCanceledOnTouchOutside(false);
                    progress.show();
                    login(usermail.getText().toString().trim(), userpass.getText().toString().trim());
                } else {
                    Toast.makeText(LoginActivity.this, "Enter valid password", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(LoginActivity.this, "Enter valid email", Toast.LENGTH_SHORT).show();
            }
        });
    }

    boolean emailChecker(String email) {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    void login(String email, String password) {
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                // Check if the admin credentials match
                if (snapshot.child("email").getValue(String.class).equals(email)
                        && snapshot.child("password").getValue(String.class).equals(password)) {
                    isAdmin = true;
                    progress.dismiss();
                    String username = "admin";
                    Toast.makeText(LoginActivity.this, "Admin login successful", Toast.LENGTH_SHORT).show();

                    // Pass username, email and candidate status to MainActivity via Intent
                    Intent new_intent = new Intent(LoginActivity.this, MainActivity.class);
                    new_intent.putExtra("username", username);
                    new_intent.putExtra("email", email); // Send email in intent
                    new_intent.putExtra("isCandidate", "no"); // Default to no for admin
                    startActivity(new Intent(LoginActivity.this,HomeActivity.class));
                    finish();
                }

                // If not admin, proceed to user login
                if (!isAdmin) {
                    UserLogin(email, password);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                progress.dismiss();
            }
        });
    }

    void UserLogin(String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    progress.dismiss();
                    String username = email.split("@")[0]; // Extract username from email for simplicity
                    Toast.makeText(LoginActivity.this, "Login successful", Toast.LENGTH_SHORT).show();

                    // Check if user is a candidate by verifying email presence in the "Candidates" table
                    DatabaseReference candidateRef = FirebaseDatabase.getInstance().getReference("Candidates");
                    candidateRef.orderByChild("email").equalTo(email).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            String candidateStatus = snapshot.exists() ? "yes" : "no";

                            // Pass username, email and candidate status to MainActivity via Intent
                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                            intent.putExtra("username", username);
                            intent.putExtra("email", email); // Send email in intent
                            intent.putExtra("isCandidate", candidateStatus); // Pass candidate status
                            startActivity(intent);
                            finish();
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            progress.dismiss();
                        }
                    });
                } else {
                    progress.dismiss();
                    Toast.makeText(LoginActivity.this, "Failed", Toast.LENGTH_SHORT).show();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progress.dismiss();
                Toast.makeText(LoginActivity.this, "Fail..", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
