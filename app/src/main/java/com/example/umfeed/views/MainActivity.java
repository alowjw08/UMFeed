package com.example.umfeed.views;

import static android.content.ContentValues.TAG;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.customview.widget.Openable;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;

import com.example.umfeed.R;
import com.example.umfeed.views.auth.LoginActivity;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class MainActivity extends AppCompatActivity {

    private NavController navController;
    private BottomNavigationView bottomNavigationView;
    private FirebaseAuth auth;

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

        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(new OnCompleteListener<>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "Fetching FCM registration token failed", task.getException());
                            return;
                        }

                        // Get new FCM registration token
                        String token = task.getResult();

                        // Log and toast
                        String msg = "FCM Token: " + token;
                        Log.d(TAG, msg);
                        Toast.makeText(MainActivity.this, "Notifications enabled for this app", Toast.LENGTH_SHORT).show();
                    }
                });

        setupNavigation();
        observeAuthState();
        askNotificationPermission();
//        askStoragePermission();
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
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.nav_host_fragment);
        navController = navHostFragment.getNavController();
        bottomNavigationView = findViewById(R.id.bottom_navigation);

        // Setup bottom navigation with NavController
        NavigationUI.setupWithNavController(bottomNavigationView, navController);

        // Create set of top level destinations
        Set<Integer> topLevelDestinations = new HashSet<>(Arrays.asList(
                R.id.homeFragment,
                R.id.chatFragment,
                R.id.profileFragment
        ));

        // Configure bottom navigation behavior
        bottomNavigationView.setOnItemSelectedListener(item -> {
            // Pop back stack to start destination before navigating
            if (topLevelDestinations.contains(item.getItemId())) {
                navController.popBackStack(navController.getGraph().getStartDestinationId(), false);
            }
            return NavigationUI.onNavDestinationSelected(item, navController);
        });

        // Handle reselection
        bottomNavigationView.setOnItemReselectedListener(item -> {
            int currentDestinationId = navController.getCurrentDestination().getId();
            if (topLevelDestinations.contains(currentDestinationId)) {
                // Pop to the start of the current tab's back stack
                navController.popBackStack(currentDestinationId, false);
            }
        });

        // Configure NavController behavior
        navController.addOnDestinationChangedListener((controller, destination, arguments) -> {
            // Show bottom navigation only for top-level destinations
            if (topLevelDestinations.contains(destination.getId())) {
                bottomNavigationView.setVisibility(View.VISIBLE);
            } else {
                bottomNavigationView.setVisibility(View.VISIBLE); // Keep visible but consider hiding for certain screens
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        return NavigationUI.navigateUp(navController, (Openable) null)
                || super.onSupportNavigateUp();
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

    private final ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    // FCM SDK (and your app) can post notifications.
                    Log.d("Permission", "Notification permission granted.");
                } else {
                    Log.d("Permission", "Notification permission denied.");
                    // Show a message or UI to explain why notifications are necessary
                    Toast.makeText(this, "Notifications will be disabled.", Toast.LENGTH_SHORT).show();
                }
            });


    private void askNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) {
                // FCM SDK (and UMFeed) can post notifications.
                Log.d("Permission", "Permission already granted.");
            } else if (shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS)) {
                // Show an educational UI explaining the need for notification permission.
                new AlertDialog.Builder(this)
                        .setTitle("Notification Permission Required")
                        .setMessage("We need your permission to send you important updates about new menus and other features. Notifications will help you stay informed.")
                        .setPositiveButton("OK", (dialog, which) -> {
                            // If the user agrees, request the permission.
                            requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS);
                        })
                        .setNegativeButton("No thanks", (dialog, which) -> {
                            // Allow the user to continue without notifications.
                            Log.d("Permission", "User declined notifications.");
                            Toast.makeText(this, "Notifications are disabled.", Toast.LENGTH_SHORT).show();
                        })
                        .create()
                        .show();
            } else {
                // Directly ask for the permission
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS);
            }
        }
    }

//    private final ActivityResultLauncher<String> requestStoragePermissionLauncher =
//            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
//                if (isGranted) {
//                    // Permission granted for storage
//                    Toast.makeText(this, "Storage permission granted.", Toast.LENGTH_SHORT).show();
//                } else {
//                    Toast.makeText(this, "Storage permission denied. Images may not load.", Toast.LENGTH_SHORT).show();
//                }
//            });
//
//    private void askStoragePermission() {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
//            // For Android 13 and above, request READ_MEDIA_IMAGES for accessing media
//            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_IMAGES)
//                    == PackageManager.PERMISSION_GRANTED) {
//                // Permission granted
//                Toast.makeText(this, "Storage permission granted for images.", Toast.LENGTH_SHORT).show();
//            } else {
//                // Show rationale if needed and request permission
//                new AlertDialog.Builder(this)
//                        .setTitle("Storage Permission Required")
//                        .setMessage("We need access to your images to load media.")
//                        .setPositiveButton("OK", (dialog, which) -> {
//                            requestStoragePermissionLauncher.launch(Manifest.permission.READ_MEDIA_IMAGES);
//                        })
//                        .setNegativeButton("Cancel", (dialog, which) -> {
//                            Toast.makeText(this, "Storage permission is required to load images.", Toast.LENGTH_SHORT).show();
//                        })
//                        .create()
//                        .show();
//            }
//        } else {
//            // For devices lower than Android 13, fallback to older permissions
//            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
//                    == PackageManager.PERMISSION_GRANTED) {
//                // Permission granted
//                Toast.makeText(this, "Storage permission granted for images.", Toast.LENGTH_SHORT).show();
//            } else {
//                // Show rationale and request permission
//                new AlertDialog.Builder(this)
//                        .setTitle("Storage Permission Required")
//                        .setMessage("We need access to your storage to load images.")
//                        .setPositiveButton("OK", (dialog, which) -> {
//                            requestStoragePermissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE);
//                        })
//                        .setNegativeButton("Cancel", (dialog, which) -> {
//                            Toast.makeText(this, "Storage permission is required to load images.", Toast.LENGTH_SHORT).show();
//                        })
//                        .create()
//                        .show();
//            }
//        }
//    }

}