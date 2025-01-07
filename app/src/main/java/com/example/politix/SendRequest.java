package com.example.politix;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;

public class SendRequest extends AppCompatActivity {
    Spinner spin;
    EditText name,email,rno,cgpa;
    String sname,smail,srno,scgpa;
    ArrayAdapter<String> adap;
    Button submit;
    ImageView img;
    private DatabaseReference dref;
    String pos[]={"President","Vice President","Secretary"};
    public String selectedpos="";
    ProgressDialog pd;
    private  static final int galpic=1;
    private Uri imageuri;
    private String downloadurl;
    private StorageReference cand_pic;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_request);
        spin=findViewById(R.id.spinpos);
        name=findViewById(R.id.reqname);
        email=findViewById(R.id.reqemail);
        rno=findViewById(R.id.reqregno);
        cgpa=findViewById(R.id.reqcgpa);
        submit=findViewById(R.id.submitreq);
        img=findViewById(R.id.uploadimg);
        pd=new ProgressDialog(this);
        pd.setMessage("submitting");
        img.setOnClickListener(view -> {
            Intent i=new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(i,galpic);
        });
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                collect();
            }
        });
        adap=new ArrayAdapter<>(this, R.layout.spin_style,pos);
        spin.setAdapter(adap);
        adap.setDropDownViewResource(R.layout.spin_style);

        spin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                selectedpos=pos[i];
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
//                Toast.makeText(this,"Please select a position",Toast.LENGTH_LONG).show();
            }
        });
    }
    private void collect(){
        sname=name.getText().toString();
        smail=email.getText().toString();
        srno=rno.getText().toString();
        scgpa=cgpa.getText().toString();
        String epattern="^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$\n";
        if(sname.isEmpty()){
            name.setError("Enter your name");
        }else if(smail.isEmpty()){
            email.setError("Enter valid email");
        }else if(srno.isEmpty()){
            email.setError("Enter valid regno");
        }else if(scgpa.isEmpty()|| Float.parseFloat(scgpa)<7 ||Float.parseFloat(scgpa)>10){
            email.setError("cgpa range should be within(7-10)");
        }else if(imageuri==null) {
            Toast.makeText(getApplicationContext(),"Select an image to uplaod ",Toast.LENGTH_LONG).show();
        }else{
            pd.show();
            uploadImage();
        }
    }
    private void storedata(){

        DatabaseReference dref=FirebaseDatabase.getInstance().getReference().child("Requests");
        HashMap<String,Object> data=new HashMap<>();
        data.put("name",sname);
        data.put("email",smail);
        data.put("regno",srno);

        data.put("cgpa",scgpa);
        data.put("position",selectedpos);
        data.put("image",downloadurl);
        dref.child(srno).setValue(data).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
            if(task.isSuccessful()){
                pd.dismiss();
                Toast.makeText(getApplicationContext(),"Data stored",Toast.LENGTH_LONG).show();

                name.setText("");
                rno.setText("");
                email.setText("");
                cgpa.setText("");
            }
            else{
                pd.dismiss();
                Toast.makeText(getApplicationContext(),"failed",Toast.LENGTH_LONG).show();
            }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==galpic&& resultCode==RESULT_OK && data!=null && data.getData()!=null){
            imageuri=data.getData();
            img.setImageURI(imageuri);
        }
    }
    private  void uploadImage(){
        StorageReference storage=FirebaseStorage.getInstance().getReference().child("request_pics");
        StorageReference imageref=storage.child(imageuri.getLastPathSegment()+System.currentTimeMillis()+".jpg");
        imageref.putFile(imageuri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        imageref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                downloadurl=uri.toString();
                                storedata();
                            }
                        });
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getApplicationContext(),"Failed to upload image",Toast.LENGTH_LONG).show();
                        pd.dismiss();
                    }
                });
    }
}