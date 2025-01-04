package com.example.umfeed.adapters;

import android.content.Context;
import android.graphics.Color;;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.umfeed.R;
import com.example.umfeed.models.user.User;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public class UserAdapter extends ListAdapter<User, UserAdapter.UserViewHolder> {
    private final Context context;
    private OnItemClickListener onItemClickListener;

    public UserAdapter(Context context) {
        super(User.DIFF_CALLBACK);
        this.context = context;
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.item_user_card, parent, false);
        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        User user = getItem(position);
        holder.bind(user, position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.onItemClickListener = listener;
    }

    class UserViewHolder extends RecyclerView.ViewHolder {
        private final ImageView userProfilePicture;
        private final TextView userNameTextView;
        private final TextView userRankTextView;
        private final TextView totalDonationsTextView;

        public UserViewHolder(@NonNull View itemView) {
            super(itemView);

            userProfilePicture = itemView.findViewById(R.id.IVUserProfilePicture);
            userNameTextView = itemView.findViewById(R.id.TVUserName);
            userRankTextView = itemView.findViewById(R.id.TVRank);
            totalDonationsTextView = itemView.findViewById(R.id.TVNumberOfDonations);

            itemView.setOnClickListener(v -> {
                if (onItemClickListener != null && getAdapterPosition() != RecyclerView.NO_POSITION) {
                    User user = getItem(getAdapterPosition());
                    onItemClickListener.onItemClick(user);
                }
            });
        }

        public void bind(User user, int position) {
            // Set user details
            userNameTextView.setText(user.getFirstName() + " " + user.getLastName());
            totalDonationsTextView.setText(String.valueOf(user.getTotalDonations()));

            // Initialize Firebase
            FirebaseFirestore db = FirebaseFirestore.getInstance();

            // Fetch the user's email
            String userEmail = user.getEmail();  // Ensure that your User class has a getEmail() method

            if (userEmail != null && !userEmail.isEmpty()) {
                // Query Firestore to get the user document by email
                db.collection("users")
                        .whereEqualTo("email", userEmail)  // Query using the user's email
                        .get()
                        .addOnSuccessListener(queryDocumentSnapshots -> {
                            if (!queryDocumentSnapshots.isEmpty()) {
                                // Assuming there is only one document with this email
                                DocumentSnapshot documentSnapshot = queryDocumentSnapshots.getDocuments().get(0);
                                String profilePictureUrl = documentSnapshot.getString("profilePicture");

                                if (profilePictureUrl != null) {
                                    // Log the profile picture URL to debug
                                    Log.d("ProfilePic", "Profile Picture URL: " + profilePictureUrl);

                                    // Convert the content:// URI string to Uri object
                                    Uri profilePictureUri = Uri.parse(profilePictureUrl);

                                    // Log the Uri to check if it's valid
                                    Log.d("ProfilePic", "Parsed Uri: " + profilePictureUri.toString());

                                    // Use ContentResolver to get an InputStream from the URI
                                    try {
                                        InputStream inputStream = itemView.getContext().getContentResolver().openInputStream(profilePictureUri);
                                        if (inputStream != null) {
                                            // Glide can load images from InputStream
                                            Glide.with(itemView.getContext())
                                                    .load(inputStream)  // Pass InputStream to Glide
                                                    .into(userProfilePicture);
                                            inputStream.close();  // Close the InputStream after use
                                        }
                                    } catch (IOException e) {
                                        Log.e("ProfilePic", "Error opening InputStream: " + e.getMessage());
                                        // Load default profile picture if error occurs
                                        Glide.with(itemView.getContext())
                                                .load(R.drawable.default_profile)  // Set a default image
                                                .into(userProfilePicture);
                                    }
                                } else {
                                    // Handle the case when no profile picture is set
                                    Log.d("ProfilePic", "No profile picture URL found for user.");
                                    // Load default profile picture if no URL
                                    Glide.with(itemView.getContext())
                                            .load(R.drawable.default_profile)  // Set a default image
                                            .into(userProfilePicture);
                                }
                            } else {
                                // Handle case when no user is found with this email
                                Log.d("ProfilePic", "No user found with the email: " + userEmail);
                                // Load default profile picture if user is not found
                                Glide.with(itemView.getContext())
                                        .load(R.drawable.default_profile)  // Set a default image
                                        .into(userProfilePicture);
                            }
                        })
                        .addOnFailureListener(e -> {
                            // Handle any errors in fetching the profile picture
                            Log.e("ProfilePic", "Error fetching profile picture: " + e.getMessage());
                            // Load default profile picture in case of failure
                            Glide.with(itemView.getContext())
                                    .load(R.drawable.default_profile)  // Set a default image
                                    .into(userProfilePicture);
                        });
            } else {
                // Handle case when the email is null or empty
                Log.d("ProfilePic", "User email is null or empty.");
                // Load default profile picture if email is invalid
                Glide.with(itemView.getContext())
                        .load(R.drawable.default_profile)  // Set a default image
                        .into(userProfilePicture);
            }

            // Rank logic based on total donations (adjusted for ties)
            List<User> users = getCurrentList();
            int rank = 1;

            for (int i = 0; i < users.size(); i++) {
                if (users.get(i).getTotalDonations() > user.getTotalDonations()) {
                    rank++;
                } else if (users.get(i).getTotalDonations() == user.getTotalDonations() && i != position) {
                    // Adjust rank if there are ties
                    rank = rank;  // Keep the same rank
                    break;
                }
            }

            userRankTextView.setText(String.valueOf(rank));

            // Set card background color based on rank
            switch (rank) {
                case 1:
                    itemView.setBackgroundColor(Color.parseColor("#FFD700")); // Gold for rank #1
                    break;
                case 2:
                    itemView.setBackgroundColor(Color.parseColor("#C0C0C0")); // Silver for rank #2
                    break;
                case 3:
                    itemView.setBackgroundColor(Color.parseColor("#CD7F32")); // Bronze for rank #3
                    break;
                default:
                    itemView.setBackgroundColor(Color.WHITE); // Default for rank 4 and onwards
                    break;
            }
        }}

        public interface OnItemClickListener {
        void onItemClick(User user);
    }
}

