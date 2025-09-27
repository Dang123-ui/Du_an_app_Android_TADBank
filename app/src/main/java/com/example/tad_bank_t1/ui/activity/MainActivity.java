package com.example.tad_bank_t1.ui.activity;


import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.tad_bank_t1.R;
import com.example.tad_bank_t1.databinding.ActivityMainBinding;
import com.example.tad_bank_t1.ui.fragment.BankTransferFragment;
import com.example.tad_bank_t1.ui.fragment.HomeCustomerFragment;
import com.example.tad_bank_t1.ui.fragment.SettingFragment;
import com.example.tad_bank_t1.util.FragmentUtil;
import com.google.android.material.navigation.NavigationBarView;

public class MainActivity extends AppCompatActivity {
    ActivityMainBinding mainBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        mainBinding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(mainBinding.getRoot());
        replaceFragment(new HomeCustomerFragment());
//        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        mainBinding.bottomNav.setVisibility(View.VISIBLE);
        mainBinding.bottomNav.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int itemId = item.getItemId(); // Get the ID once

                if (itemId == R.id.nav_home) {
                    FragmentUtil.replaceFragment(new HomeCustomerFragment(), getSupportFragmentManager(), R.id.frame_main_container);
                } else if (itemId == R.id.nav_transfer) {
                    FragmentUtil.replaceFragment(new BankTransferFragment(), getSupportFragmentManager(), R.id.frame_main_container);
                } else if (itemId == R.id.nav_setting) {
                    FragmentUtil.replaceFragment(new SettingFragment(), getSupportFragmentManager(), R.id.frame_main_container);
                } else {
                    return false; // Or handle as appropriate
                }
                return true;
            }
        });
    }


    private void replaceFragment(Fragment fragment){
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_main_container, fragment);
        fragmentTransaction.commit();
    }

    // Inside MainActivity.java
    // su dung de toggle hien thi Navigation
    public void setBottomNavigationVisibility(int visibility) {
        // You can also add an animation here if you want
        mainBinding.bottomNav.setVisibility(visibility);
    }
}