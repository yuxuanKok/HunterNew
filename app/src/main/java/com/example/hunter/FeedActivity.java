package com.example.hunter;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.example.hunter.ui.addPost.AddPostFragment;
import com.example.hunter.ui.home.HomeFragment;
import com.example.hunter.ui.rewards.RewardsFragment;
import com.example.hunter.ui.search.SearchFragment;
import com.example.hunter.ui.profile.ProfileFragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import com.google.firebase.firestore.FirebaseFirestore;

import androidx.appcompat.app.AppCompatActivity;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import java.util.ArrayList;

public class FeedActivity extends AppCompatActivity{

    BottomNavigationView navView;
    String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed);
        navView = findViewById(R.id.nav_view);
        navView.setOnNavigationItemSelectedListener(navListener);
        userId=getIntent().getStringExtra("userId");
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new HomeFragment(),"HomeFrag").commit();
    }

    private BottomNavigationView.OnNavigationItemSelectedListener navListener=
            menuItem -> {
                Fragment selectedFragment = null;
                switch (menuItem.getItemId()){
                    case R.id.post:
                        selectedFragment=new AddPostFragment();
                        break;
                    case R.id.newsFeed:
                        selectedFragment=new HomeFragment();
                        break;
                    case R.id.search:
                        selectedFragment=new SearchFragment();
                        break;
                    case R.id.profile:
                        selectedFragment=ProfileFragment.newInstance(userId);
                        break;
                    case R.id.rewards:
                        selectedFragment=new RewardsFragment();
                }
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.fragment_container,selectedFragment);
                transaction.addToBackStack(null);
                transaction.commit();

                return true;
            };
}
