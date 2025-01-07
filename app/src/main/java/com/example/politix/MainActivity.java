package com.example.politix;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;

import com.example.politix.Fragments.HomeFragment;
import com.example.politix.Fragments.ProfileFragment;
import com.example.politix.Fragments.VoteFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

public class MainActivity extends AppCompatActivity implements NavigationBarView

        .OnItemSelectedListener{

    BottomNavigationView  nav;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        nav=findViewById(R.id.nav);
        loadFragment(new HomeFragment());
        //statusbar
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(Color.parseColor("#BF0c0c68")); // 80 is the alpha for opacity
        }
        Toolbar myToolbar = findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        nav.setOnItemSelectedListener(this);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        Fragment fragment=null;
        int id=item.getItemId();
        if(id==R.id.home){
            fragment=new HomeFragment();
            getSupportActionBar().setTitle("Politix");
        }else if(id==R.id.vote){
            fragment=new VoteFragment();
            getSupportActionBar().setTitle("Vote");
        }else if(id==R.id.profile){
            fragment=new ProfileFragment();
            getSupportActionBar().setTitle("My Profile");
        }
        return loadFragment(fragment);
    }
    public boolean loadFragment(Fragment fragment){
        if(fragment!=null){
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,fragment).commit();
            return true;
        }
        return false;
    }
}