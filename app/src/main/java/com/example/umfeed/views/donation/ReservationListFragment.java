package com.example.umfeed.views.donation;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.umfeed.R;
import com.example.umfeed.adapters.ReservationAdapter;
import com.example.umfeed.models.reservation.Reservation;
import com.example.umfeed.viewmodels.reservation.ReservationViewModel;

import com.example.umfeed.R;

import java.util.List;

public class ReservationListFragment extends Fragment {

    private ReservationViewModel viewModel;
    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private TextView emptyView;
    private ReservationAdapter adapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = new ViewModelProvider(this).get(ReservationViewModel.class);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_reservation_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerView = view.findViewById(R.id.foodReservedRecycleView);
        progressBar = view.findViewById(R.id.progressBar);
        emptyView = view.findViewById(R.id.emptyView);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new ReservationAdapter();
        recyclerView.setAdapter(adapter);

        setupObservers();
        fetchReservations();
    }

    private void setupObservers() {
        viewModel.getReservations().observe(getViewLifecycleOwner(), this::handleReservations);
        viewModel.getIsLoading().observe(getViewLifecycleOwner(), isLoading -> {
            if (isLoading != null && isLoading) {
                progressBar.setVisibility(View.VISIBLE);
                recyclerView.setVisibility(View.GONE);
                emptyView.setVisibility(View.GONE);
            } else {
                progressBar.setVisibility(View.GONE);
            }
        });

        viewModel.getErrorMessage().observe(getViewLifecycleOwner(), errorMessage -> {
            if (errorMessage != null) {
                progressBar.setVisibility(View.GONE);
                emptyView.setText(errorMessage);
                emptyView.setVisibility(View.VISIBLE);
                recyclerView.setVisibility(View.GONE);
            }
        });
    }

    private void fetchReservations() {
        viewModel.loadUserReservations();
    }

    private void handleReservations(List<Reservation> reservations) {
        if (reservations != null && !reservations.isEmpty()) {
            adapter.setReservations(reservations);
            recyclerView.setVisibility(View.VISIBLE);
            emptyView.setVisibility(View.GONE);
        } else {
            recyclerView.setVisibility(View.GONE);
            emptyView.setVisibility(View.VISIBLE);
        }
        progressBar.setVisibility(View.GONE);
    }
}