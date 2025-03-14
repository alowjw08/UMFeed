package com.example.umfeed.views.profile;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.bumptech.glide.Glide;
import com.example.umfeed.R;
import com.example.umfeed.views.auth.ForgotPasswordActivity;
import com.example.umfeed.views.auth.LoginActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.UUID;

public class ProfileFragment extends Fragment {

    private static final int PICK_IMAGE_REQUEST = 1;

    private TextView tvUserName, tvEmailProfile;
    private Button buttonForgotPassword, buttonLogOut, buttonSavedRecipes, buttonDonatedItems, buttonUploadPicture, buttonLeaderboardProfile;
    private ImageView ivProfilePicture;

    private FirebaseAuth firebaseAuth;
    private FirebaseUser currentUser;
    private FirebaseFirestore db;
    private FirebaseStorage storage;
    private StorageReference storageReference;

    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        // Initialize views
        tvUserName = view.findViewById(R.id.tvUserName);
        tvEmailProfile = view.findViewById(R.id.tvEmailProfile);
        ivProfilePicture = view.findViewById(R.id.ivProfilePicture);
        buttonForgotPassword = view.findViewById(R.id.buttonForgotPassword);
        buttonLogOut = view.findViewById(R.id.buttonLogOut);
        buttonSavedRecipes = view.findViewById(R.id.buttonSavedRecipes);
        buttonDonatedItems = view.findViewById(R.id.buttonDonatedItems);
        buttonUploadPicture = view.findViewById(R.id.buttonUploadPicture);
        buttonLeaderboardProfile = view.findViewById(R.id.buttonLeaderboardProfile);

        // Initialize Firebase
        firebaseAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        currentUser = firebaseAuth.getCurrentUser();

        if (currentUser == null) {
            Toast.makeText(getActivity(), "No user logged in!", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(getActivity(), LoginActivity.class));
            getActivity().finish();
            return view;
        }

        // Load user data
        String uid = currentUser.getUid();
        DocumentReference userRef = db.collection("users").document(uid);

        userRef.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                // Fetch user details
                String name = documentSnapshot.getString("firstName");
                String email = documentSnapshot.getString("email");
                String profilePicture = documentSnapshot.getString("profilePicture");

                // Display user details
                tvUserName.setText(name != null ? name : "Name not available");
                tvEmailProfile.setText(email != null ? email : "Email not available");

                if (profilePicture != null) {
                    Glide.with(this).load(profilePicture).into(ivProfilePicture);
                }
            } else {
                Toast.makeText(getActivity(), "User not found", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(e -> {
            Toast.makeText(getActivity(), "Error fetching data: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        });

        // Set up button actions
        buttonForgotPassword.setOnClickListener(v -> startActivity(new Intent(getActivity(), ForgotPasswordActivity.class)));
        buttonLogOut.setOnClickListener(v -> {
            firebaseAuth.signOut();
            Toast.makeText(getActivity(), "Logged out", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(getActivity(), LoginActivity.class));
            getActivity().finish();
        });
        buttonSavedRecipes.setOnClickListener(v -> Navigation.findNavController(view).navigate(R.id.action_profileFragment_to_savedRecipesFragment));
        buttonDonatedItems.setOnClickListener(v -> Navigation.findNavController(view).navigate(R.id.action_profileFragment_to_donatedItemsFragment));

        // Upload picture button
        buttonUploadPicture.setOnClickListener(v -> openImageChooser());

        buttonLeaderboardProfile.setOnClickListener(v -> {
            Bundle bundle = new Bundle();
            bundle.putString("email", currentUser.getEmail()); // Pass the user's email
            Navigation.findNavController(view).navigate(R.id.action_profileFragment_to_leaderboardProfileFragment, bundle);
        });

        return view;
    }

    private void openImageChooser() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null && data.getData() != null) {
            Uri imageUri = data.getData();
            uploadImageToFirebase(imageUri);
        }
    }

    private void uploadImageToFirebase(Uri imageUri) {
        if (imageUri == null) {
            Toast.makeText(getActivity(), "Image URI is null", Toast.LENGTH_SHORT).show();
            return;
        }

        // Convert the selected image URI to a download URL
        String downloadUrl = imageUri.toString();

        // Save the download URL to Firestore
        saveImageUrlToFirestore(downloadUrl);
    }

    private void saveImageUrlToFirestore(String downloadUrl) {
        String uid = currentUser.getUid();
        DocumentReference userRef = db.collection("users").document(uid);

        // Update Firestore with the new profile picture URL
        userRef.update("profilePicture", downloadUrl)
                .addOnSuccessListener(aVoid -> {
                    // Update UI with the new profile picture
                    Glide.with(this).load(downloadUrl).into(ivProfilePicture);
                    Toast.makeText(getActivity(), "Profile picture updated successfully", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Log.e("Cloud Firestore", "Failed to update Firestore: " + e.getMessage());
                    Toast.makeText(getActivity(), "Failed to update profile picture", Toast.LENGTH_SHORT).show();
                });
    }
}
