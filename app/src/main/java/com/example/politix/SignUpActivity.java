package com.example.politix;

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
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SignUpActivity extends AppCompatActivity {
    EditText name,mail,regno,pass,cpass;
    String sname,smail,sregno,spass,scpass;
    Button sign;
    DatabaseReference ref;
    FirebaseAuth auth;
    ProgressDialog dialog;
    String passwordPattern = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{6,}$";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_sign_up);
       name=findViewById(R.id.name);
       mail=findViewById(R.id.mail);
       regno=findViewById(R.id.regno);
       pass=findViewById(R.id.pass);
       cpass=findViewById(R.id.cpass);
       sign=findViewById(R.id.sign);
        ref= FirebaseDatabase.getInstance().getReference().child("Users");
        auth=FirebaseAuth.getInstance();
       dialog=new ProgressDialog(this);
       findViewById(R.id.no_acc).setOnClickListener(view -> {
           startActivity(new Intent(SignUpActivity.this,LoginActivity.class));
           finish();
       });
        sign.setOnClickListener(view->{
            sname=name.getText().toString();
            smail=mail.getText().toString();
            spass=pass.getText().toString();
            scpass=cpass.getText().toString();
            if (sname.isEmpty()) {
                name.setError("Enter name");
            } else if (smail.isEmpty()) {
                mail.setError("Enter valid email");
            } else if (!spass.matches(passwordPattern)) {
                pass.setError("Password should contain atleast 6 characters with 1 digit,1 lowercase letter,1 uppercase letter,1 symbol");
            } else if (!spass.equals(scpass)) {
                cpass.setError("Password does not match");
            } else {
                if (Patterns.EMAIL_ADDRESS.matcher(smail).matches()) {
                    dialog.setTitle("Sign Up");
                    dialog.setMessage("Signing up");
                    dialog.setCanceledOnTouchOutside(false);
                    dialog.show();
                    createUser(smail,spass,sname);
                }else{
                    mail.setError("Enter valid email");
                }

            }

        });
    }
    void createUser(String email,String pword,String name){
        auth.createUserWithEmailAndPassword(email,pword).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                User user=new User(name,email);
                if(task.isSuccessful()){
                    ref.push().setValue(user);
                    dialog.dismiss();
                    Toast.makeText(SignUpActivity.this,"User created",Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(SignUpActivity.this,LoginActivity.class));
                    finish();
                }else{
                    dialog.dismiss();
                    Toast.makeText(SignUpActivity.this,""+task.getException(),Toast.LENGTH_SHORT).show();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                dialog.dismiss();
                Toast.makeText(SignUpActivity.this,"Failed",Toast.LENGTH_SHORT).show();
            }
        });
    }
}
class User {
    private String name, email,regno;

    public User(String name, String email) {
        this.name = name;
        this.email = email;
        //this.regno=regno;
    }

//    public String getRegno() {
//        return regno;
//    }
//
//    public void setRegno(String regno) {
//        this.regno = regno;
//    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}