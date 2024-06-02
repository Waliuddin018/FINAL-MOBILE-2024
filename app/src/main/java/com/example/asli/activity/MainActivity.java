package com.example.asli.activity;

import android.os.Bundle;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.asli.fragment.FavoriteFragment;
import com.example.asli.R;
import com.example.asli.fragment.DestinationFragment;
import com.example.asli.fragment.ProfileFragment;
import com.example.asli.fragment.SearchFragment;

public class MainActivity extends AppCompatActivity {
    ImageView iv_home,iv_search,iv_profile,iv_favorite;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        iv_home = findViewById(R.id.IV_Home);
        iv_search = findViewById(R.id.IV_Search);
        iv_profile = findViewById(R.id.IV_Profile);
        iv_favorite = findViewById(R.id.IV_Favorite);

        FragmentManager fragmentManager = getSupportFragmentManager();
        DestinationFragment destinationFragment = new DestinationFragment();
        Fragment fragment = fragmentManager.findFragmentByTag(DestinationFragment.class.getSimpleName());

        if (!(fragment instanceof DestinationFragment)){
            fragmentManager
                    .beginTransaction()
                    .add(R.id.frame_container, destinationFragment)
                    .commit();
        }
        iv_home.setOnClickListener(v ->
           fragmentManager
                .beginTransaction()
                .replace(R.id.frame_container, destinationFragment)
                .addToBackStack(null)
                .commit());
        iv_profile.setOnClickListener(v -> {
            ProfileFragment profileFragment = new ProfileFragment();
            fragmentManager
                    .beginTransaction()
                    .replace(R.id.frame_container, profileFragment)
                    .addToBackStack(null)
                    .commit();
        });

        iv_search.setOnClickListener(v -> {
            SearchFragment searchFragment = new SearchFragment();
            fragmentManager
                    .beginTransaction()
                    .replace(R.id.frame_container, searchFragment)
                    .addToBackStack(null)
                    .commit();
        });

        iv_favorite.setOnClickListener(v -> {
            FavoriteFragment favoriteFragment = new FavoriteFragment();
            fragmentManager
                    .beginTransaction()
                    .replace(R.id.frame_container, favoriteFragment)
                    .addToBackStack(null)
                    .commit();
        });
    }

    public void updateFavoriteFragment() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        Fragment currentFragment = fragmentManager.findFragmentById(R.id.frame_container);
        if (currentFragment instanceof FavoriteFragment) {
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.detach(currentFragment);
            fragmentTransaction.attach(currentFragment);
            fragmentTransaction.commit();
        }
    }
}
