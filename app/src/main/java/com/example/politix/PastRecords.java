package com.example.politix;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.politix.Adapters.RecordsAdapter;
import com.example.politix.models.PastRecord;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class PastRecords extends AppCompatActivity {
    List<PastRecord> list23,list22,list21;
    RecordsAdapter adap23,adap22,adap21;
    DatabaseReference ref;
    RecyclerView r23,r22,r21;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_past_records);
        //status bar
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(Color.parseColor("#BF0c0c68")); // 80 is the alpha for opacity
        }
        r23=findViewById(R.id.recycle23);
        r22=findViewById(R.id.recycle22);
        r21=findViewById(R.id.recycle21);
        r23.setHasFixedSize(true);
        r23.setLayoutManager(new LinearLayoutManager(this));
        r22.setHasFixedSize(true);
        r22.setLayoutManager(new LinearLayoutManager(this));
        r21.setHasFixedSize(true);
        r21.setLayoutManager(new LinearLayoutManager(this));
        list23=new ArrayList<>();
        list22=new ArrayList<>();
        list21=new ArrayList<>();
        adap23=new RecordsAdapter(this,list23);
        adap22=new RecordsAdapter(this,list22);
        adap21=new RecordsAdapter(this,list21);
        r23.setAdapter(adap23);
        r22.setAdapter(adap22);
        r21.setAdapter(adap21);
        ref= FirebaseDatabase.getInstance().getReference().child("PastRecords");
        ref.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                list23.clear();
                list22.clear();
                list21.clear();
                for(DataSnapshot ds:snapshot.getChildren()){
                    PastRecord model=ds.getValue(PastRecord.class);
                    if(ds.child("year").getValue(Long.class)==2023){
                        list23.add(model);
                    }
                     if(ds.child("year").getValue(Long.class)==2022){
                        list22.add(model);
                    }
                     if(ds.child("year").getValue(Long.class)==2021){
                        list21.add(model);
                    }

                }
                adap23.notifyDataSetChanged();
                adap22.notifyDataSetChanged();
                adap21.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}