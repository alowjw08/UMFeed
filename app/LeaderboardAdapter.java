import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.card.MaterialCardView;
import com.squareup.picasso.Picasso;
import java.util.List;

public class LeaderboardAdapter extends RecyclerView.Adapter<LeaderboardAdapter.LeaderboardViewHolder> {

    private final Context context;
    private final List<User> userList;

    public LeaderboardAdapter(Context context, List<User> userList) {
        this.context = context;
        this.userList = userList;
    }

    @NonNull
    @Override
    public LeaderboardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.leaderboard_item, parent, false);
        return new LeaderboardViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull LeaderboardViewHolder holder, int position) {
        User user = userList.get(position);

        // Set user details
        holder.tvRank.setText(String.valueOf(position + 1));
        holder.tvUserName.setText(user.getFirstName() + " " + user.getLastName());
        holder.tvNumberOfDonations.setText(String.valueOf(user.getTotalDonations()));

        // Load profile picture
        Picasso.get().load(user.getProfilePictureUrl()).placeholder(R.drawable.placeholder).into(holder.ivUserProfilePicture);

        // Set card background based on rank
        if (position == 0) {
            holder.cardView.setCardBackgroundColor(Color.parseColor("#FFD700")); // Gold
        } else if (position == 1) {
            holder.cardView.setCardBackgroundColor(Color.parseColor("#C0C0C0")); // Silver
        } else if (position == 2) {
            holder.cardView.setCardBackgroundColor(Color.parseColor("#CD7F32")); // Bronze
        } else {
            holder.cardView.setCardBackgroundColor(Color.parseColor("#FFFFFF")); // White
        }
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    static class LeaderboardViewHolder extends RecyclerView.ViewHolder {
        TextView tvRank, tvUserName, tvNumberOfDonations;
        ImageView ivUserProfilePicture;
        MaterialCardView cardView;

        public LeaderboardViewHolder(@NonNull View itemView) {
            super(itemView);
            tvRank = itemView.findViewById(R.id.TVRank);
            tvUserName = itemView.findViewById(R.id.TVUserName);
            tvNumberOfDonations = itemView.findViewById(R.id.TVNumberOfDonations);
            ivUserProfilePicture = itemView.findViewById(R.id.IVUserProfilePicture);
            cardView = itemView.findViewById(R.id.UserCard);
        }
    }
}
