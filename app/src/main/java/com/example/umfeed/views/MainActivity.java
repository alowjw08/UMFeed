package com.example.umfeed.views;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.customview.widget.Openable;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;

import com.example.umfeed.R;
import com.example.umfeed.utils.RecipeImportUtil;
import com.example.umfeed.viewmodels.auth.LoginViewModel;
import com.example.umfeed.views.auth.LoginActivity;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {

    private NavController navController;
    private BottomNavigationView bottomNavigationView;
    private FirebaseAuth auth;
    private LoginViewModel loginViewModel;
    private RecipeImportUtil recipeImportUtil;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        auth = FirebaseAuth.getInstance();
        GoogleApiAvailability.getInstance().makeGooglePlayServicesAvailable(this);

        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);
        navController = navHostFragment.getNavController();

        if (auth.getCurrentUser() == null) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        if (!isFinishing()) {
            FirebaseApp.initializeApp(this);
        }

        setupNavigation();
        observeAuthState();
    }

    private void observeAuthState() {
        FirebaseAuth.getInstance().addAuthStateListener(firebaseAuth -> {
            if (firebaseAuth.getCurrentUser() == null) {
                startActivity(new Intent(this, LoginActivity.class));
                finish();
            }
        });
    }

    private void setupNavigation() {
        NavHostFragment navHostFragment =
                (NavHostFragment) getSupportFragmentManager()
                        .findFragmentById(R.id.nav_host_fragment);
        navController = navHostFragment.getNavController();

        bottomNavigationView = findViewById(R.id.bottom_navigation);
        NavigationUI.setupWithNavController(bottomNavigationView, navController);

        bottomNavigationView.setOnItemReselectedListener(item -> {
            if (item.getItemId() == R.id.homeFragment) {
                navController.popBackStack(R.id.homeFragment, false);
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        return NavigationUI.navigateUp(navController, (Openable) null) || super.onSupportNavigateUp();
    }

    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        NavHostFragment navHostFragment =
                (NavHostFragment) getSupportFragmentManager()
                        .findFragmentById(R.id.nav_host_fragment);
        if (navHostFragment != null) {
            navHostFragment.getNavController()
                    .handleDeepLink(intent);
        }
    }
}