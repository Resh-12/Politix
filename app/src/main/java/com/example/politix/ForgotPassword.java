package com.example.politix;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class ForgotPassword extends AppCompatActivity {
EditText mail;
FirebaseAuth auth;
ProgressDialog pd;
String smail;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_forgot_password);
        pd=new ProgressDialog(this);
        auth=FirebaseAuth.getInstance();
        pd.setMessage("Loading");
        mail=findViewById(R.id.fmail);
        findViewById(R.id.submit).setOnClickListener(view -> {
            smail=mail.getText().toString();
            if(smail.isEmpty())
            {
                mail.setError("Enter email");

            }
            else{
                pd.setMessage("Loading");
                pd.setCanceledOnTouchOutside(false);
                pd.show();
                auth.sendPasswordResetEmail(smail).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful()){
                        pd.dismiss();
                        Toast.makeText(ForgotPassword.this,"Mail has been sent successfully",Toast.LENGTH_LONG).show();
                        startActivity(new Intent(ForgotPassword.this,LoginActivity.class));
                        finish();
                    }else{
                        pd.dismiss();
                        Toast.makeText(ForgotPassword.this,""+task.getException().getMessage(),Toast.LENGTH_SHORT).show();
                    }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(ForgotPassword.this,"Failed",Toast.LENGTH_LONG).show();
                    }
                });
            }
        });
    }
}