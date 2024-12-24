package com.example.umfeed.views.profile;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.umfeed.R;
import com.example.umfeed.views.auth.ForgotPasswordActivity;
import com.example.umfeed.views.auth.LoginActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class ProfileFragment extends Fragment {

    private TextView tvUserName, tvEmailProfile;
    private Button buttonForgotPassword, buttonLogOut, buttonSavedRecipes, buttonDonatedItems;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser currentUser;

    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(com.example.umfeed.R.layout.fragment_profile, container, false);

        // Initialize views
        tvUserName = view.findViewById(R.id.tvUserName);
        tvEmailProfile = view.findViewById(R.id.tvEmailProfile);
        buttonForgotPassword = view.findViewById(R.id.buttonForgotPassword);
        buttonLogOut = view.findViewById(R.id.buttonLogOut);
        buttonSavedRecipes = view.findViewById(R.id.buttonSavedRecipes);
        buttonDonatedItems = view.findViewById(R.id.buttonDonatedItems);

        // Initialize Firebase
        firebaseAuth = FirebaseAuth.getInstance();
        currentUser = firebaseAuth.getCurrentUser();

        if (currentUser == null) {
            Toast.makeText(getActivity(), "No user logged in!", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(getActivity(), LoginActivity.class));
            getActivity().finish();
            return view;
        }

        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        FirebaseUser currentUser = auth.getCurrentUser();

        if (currentUser != null) {
            String uid = currentUser.getUid(); // Get the UID of the logged-in user

            DocumentReference userRef = db.collection("users").document(uid);

            userRef.get().addOnSuccessListener(documentSnapshot -> {
                if (documentSnapshot.exists()) {
                    // Retrieve fields from Firestore
                    String name = documentSnapshot.getString("firstName");
                    String email = documentSnapshot.getString("email");

                    // Update UI
                    tvUserName.setText(name != null ? name : "Name not available");
                    tvEmailProfile.setText(email != null ? email : "Email not available");
                } else {
                    Toast.makeText(getActivity(), "User not found", Toast.LENGTH_SHORT).show();
                }
            }).addOnFailureListener(e -> {
                Toast.makeText(getActivity(), "Error fetching data: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            });
        } else {
            Toast.makeText(getActivity(), "No user is logged in", Toast.LENGTH_SHORT).show();
        }



        // Fetch data from Firebase Database
//        String userId = currentUser.getUid();
//        userRef = FirebaseDatabase.getInstance().getReference("users/users").child(userId);

//        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                if (snapshot.exists()) {
//                    String name = snapshot.child("firstName").getValue(String.class);
//                    String email = currentUser.getEmail(); // Get from FirebaseAuth
//
//                    tvUserName.setText(name != null ? name : "Name not available");
//                    tvEmailProfile.setText(email != null ? email : "Email not available");
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//                Toast.makeText(getActivity(), "Error fetching data", Toast.LENGTH_SHORT).show();
//            }
//        });

        // Forgot Password Button
        buttonForgotPassword.setOnClickListener(v -> startActivity(new Intent(getActivity(), ForgotPasswordActivity.class)));

        // Logout Button
        buttonLogOut.setOnClickListener(v -> {
            firebaseAuth.signOut();
            Toast.makeText(getActivity(), "Logged out", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(getActivity(), LoginActivity.class));
            getActivity().finish();
        });

        buttonSavedRecipes.setOnClickListener(v ->
                Navigation.findNavController(view).navigate(R.id.action_profileFragment_to_savedRecipesFragment)
        );

        buttonDonatedItems.setOnClickListener(v ->
                Navigation.findNavController(view).navigate(R.id.action_profileFragment_to_donatedItemsFragment)
        );

        return view;
    }
}

