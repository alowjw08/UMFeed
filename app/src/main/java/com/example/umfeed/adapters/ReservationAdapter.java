package com.example.umfeed.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.umfeed.R;
import com.example.umfeed.models.reservation.Reservation;
import com.example.umfeed.utils.CategoryImageUtil;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ReservationAdapter extends RecyclerView.Adapter<ReservationAdapter.ReservationViewHolder> {

    private List<Reservation> reservations = new ArrayList<>();
    private OnCollectClickListener onCollectClickListener;

    public void setReservations(List<Reservation> reservations) {
        this.reservations = reservations;
        notifyDataSetChanged();
    }

    public void setOnCollectClickListener(OnCollectClickListener listener) {
        this.onCollectClickListener = listener;
    }

    @NonNull
    @Override
    public ReservationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_food_reserved, parent, false);
        return new ReservationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReservationViewHolder holder, int position) {
        Reservation reservation = reservations.get(position);
        holder.bind(reservation);

        // Use CategoryImageUtil for the image
        int imageResId = CategoryImageUtil.getImageResourceByCategory(reservation.getCategory());
        holder.foodImage.setImageResource(imageResId);

        holder.collectButton.setOnClickListener(v -> {
            if (onCollectClickListener != null) {
                onCollectClickListener.onCollectClick(reservation);
            }
        });
    }

    //remove item if successfully collected
    public void removeReservation(Reservation reservation) {
        int position = reservations.indexOf(reservation);
        if (position != -1) {
            reservations.remove(position);
            notifyItemRemoved(position);
        }
    }

    @Override
    public int getItemCount() {
        return reservations.size();
    }

    public interface OnCollectClickListener {
        void onCollectClick(Reservation reservation);
    }

    public static class ReservationViewHolder extends RecyclerView.ViewHolder {

        private final TextView categoryText, quantityText, foodBankText, reservationDateText;
        private final ImageView foodImage;

        private final Button collectButton;

        public ReservationViewHolder(@NonNull View itemView) {
            super(itemView);
            categoryText = itemView.findViewById(R.id.foodCategory);
            quantityText = itemView.findViewById(R.id.foodQuantity);
            foodBankText = itemView.findViewById(R.id.foodBank);
            reservationDateText = itemView.findViewById(R.id.reservationDateText);
            foodImage = itemView.findViewById(R.id.foodImage);
            collectButton = itemView.findViewById(R.id.btnCollect);
        }

        public void bind(Reservation reservation) {
            categoryText.setText(reservation.getCategory());
            quantityText.setText(String.format(Locale.getDefault(), "%d pack(s)", reservation.getQuantity()));
            foodBankText.setText(String.format("Food Bank: %s", reservation.getFoodBankId()));

            SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault());
            reservationDateText.setText(String.format("Reserved: %s",
                    dateFormat.format(reservation.getReservationDate().toDate())));
        }
    }
}