package com.example.umfeed.adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.example.umfeed.R;
import com.example.umfeed.models.user.User;
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
        private final TextView userNameTextView;
        private final TextView userRankTextView;
        private final TextView totalDonationsTextView;

        public UserViewHolder(@NonNull View itemView) {
            super(itemView);

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

