package com.example.umfeed.views.pin;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.umfeed.R;
import com.example.umfeed.viewmodels.donation.DonationViewModel;
import com.example.umfeed.viewmodels.pin.PinVerificationViewModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class PinVerificationDialogFragment extends DialogFragment {

    private PinVerificationViewModel viewModel;
    private TextView numText;
    private ConstraintLayout cardView;
    private Button buttonClear;
    private boolean isDialogShown = false;

    public static PinVerificationDialogFragment newInstance() {
        return new PinVerificationDialogFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_pin_verification, container, false);
    }

    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        if (dialog.getWindow() != null) {
            dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT)); // Remove default dialog background
        }
        return dialog;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize ViewModel
//        viewModel = new ViewModelProvider(this).get(PinVerificationViewModel.class);

        viewModel = new ViewModelProvider(requireActivity()).get(PinVerificationViewModel.class);

        // Get UI elements
        numText = view.findViewById(R.id.numText);
        cardView = view.findViewById(R.id.pinVerificationDialog);

        numText.setText("");
        Button button1 = view.findViewById(R.id.button1);
        Button button2 = view.findViewById(R.id.button2);
        Button button3 = view.findViewById(R.id.button3);
        Button button4 = view.findViewById(R.id.button4);
        Button buttonConfirm = view.findViewById(R.id.buttonConfirm);
        Button buttonClear = view.findViewById(R.id.buttonClear);
//        ImageButton buttonCancel = view.findViewById(R.id.buttonCancel);

        // Set up button listeners for number input
        View.OnClickListener numberClickListener = v -> {
            String digit = ((Button) v).getText().toString();
            viewModel.appendDigit(digit);
        };
        button1.setOnClickListener(numberClickListener);
        button2.setOnClickListener(numberClickListener);
        button3.setOnClickListener(numberClickListener);
        button4.setOnClickListener(numberClickListener);

        // Observe PIN text changes
        viewModel.getPinText().observe(getViewLifecycleOwner(), numText::setText);
        String source = getArguments() != null ? getArguments().getString("source") : "donation"; // Default to "donation"
        String foodBankId = getArguments() != null ? getArguments().getString("foodBankId") : null;
        String foodName = getArguments() != null ? getArguments().getString("foodName") : null;
        String category = getArguments() != null ? getArguments().getString("category") : null;
        String location = getArguments() != null ? getArguments().getString("location") : null;
        Boolean vegetarian = getArguments() != null ? getArguments().getBoolean("vegetarian") : null;
        int quantity = getArguments() != null ? getArguments().getInt("quantity") : 0;
        String reservationId = getArguments() != null ? getArguments().getString("reservationId") : null;
        viewModel.setFoodBankId(foodBankId);

        buttonConfirm.setOnClickListener(v -> {
            if (viewModel.isPinValid()) {
                String enteredPin = viewModel.getPinText().getValue();
                viewModel.verifyPin(Integer.parseInt(enteredPin));

                // Add a one-time observer for isPinCorrect
                viewModel.getPinCorrect().observe(this, new Observer<Boolean>() {
                    @Override
                    public void onChanged(Boolean isPinCorrect) {
                        if (isPinCorrect == null) return; // Skip null (Firebase call still in progress)

                        DialogSuccessFragment dialogSuccessFragment = DialogSuccessFragment.newInstance();
                        Log.d("FinalPinVerification", "Pin Correct: " + isPinCorrect);

                        if (isPinCorrect) {
                            dismiss();
                            if ("donation".equals(source)) {
                                new ViewModelProvider(requireActivity())
                                        .get(DonationViewModel.class)
                                        .submitDonation(category, vegetarian, quantity, location);

                                new Handler(Looper.getMainLooper()).postDelayed(dialogSuccessFragment::onDonate, 50);
                            } else if ("reservation".equals(source)) {
                                // Update the reservation as "collected"
                                FirebaseFirestore db = FirebaseFirestore.getInstance();
                                String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
                                db.collection("users")
                                        .document(userId)
                                        .collection("reservations")
                                        .document(reservationId)
                                        .update("status", "collected")
                                        .addOnSuccessListener(aVoid -> Log.d("PinVerification", "Reservation collected"))
                                        .addOnFailureListener(e -> Log.e("PinVerification", "Error updating reservation", e));

                                new Handler(Looper.getMainLooper()).postDelayed(dialogSuccessFragment::onReserve, 50);
                            }
                            dialogSuccessFragment.show(getParentFragmentManager(), "DialogSuccessFragment");
                        } else {
                            numText.setText("Incorrect PIN");
                            dismiss();
                            if ("donation".equals(source)) {
                                new Handler(Looper.getMainLooper()).postDelayed(dialogSuccessFragment::onDonationError, 50);
                            } else if ("reservation".equals(source)) {
                                new Handler(Looper.getMainLooper()).postDelayed(dialogSuccessFragment::onReservationError, 50);
                            }
                            dialogSuccessFragment.show(getParentFragmentManager(), "DialogSuccessFragment");
                        }

                        // Remove this observer after the first trigger
                        viewModel.getPinCorrect().removeObserver(this);
                    }
                });
            } else {
                numText.setText("Invalid PIN");
            }
        });

        viewModel.clearPin();
        // Clear button listener
        buttonClear.setOnClickListener(v -> {
            viewModel.clearPin();
        });
//        buttonCancel.setOnClickListener(v -> dismiss());
    }
}

