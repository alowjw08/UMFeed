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
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.umfeed.R;
import com.example.umfeed.viewmodels.pin.DialogSuccessViewModel;
import com.example.umfeed.viewmodels.pin.PinVerificationViewModel;

public class PinVerificationDialogFragment extends DialogFragment {

    private PinVerificationViewModel viewModel;
    private TextView numText;
    private ConstraintLayout cardView;

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
        viewModel = new ViewModelProvider(this).get(PinVerificationViewModel.class);

        // Get UI elements
        numText = view.findViewById(R.id.numText);
        cardView = view.findViewById(R.id.pinVerificationDialog);

        numText.setText("");
        Button button1 = view.findViewById(R.id.button1);
        Button button2 = view.findViewById(R.id.button2);
        Button button3 = view.findViewById(R.id.button3);
        Button button4 = view.findViewById(R.id.button4);
        Button buttonConfirm = view.findViewById(R.id.buttonConfirm);
        Button buttonCancel = view.findViewById(R.id.buttonCancel);

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

        // TODO: set up nav logic (no need else)
        // Confirm button listener
//        buttonConfirm.setOnClickListener(v -> {
//            if (viewModel.isPinValid()) {
//                String enteredPin = viewModel.getPinText().getValue();
//                if (enteredPin != null) {
//                    // Verify the pin with Firebase
//                    viewModel.verifyPin(Integer.parseInt(enteredPin));
//
//                    viewModel.getPinValid().observe(getViewLifecycleOwner(), isValid -> {
//                        if (isValid) {
//                            // Navigate to success screen
////                            NavController navController = Navigation.findNavController(view);
//                            dismiss();
////                            NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment);
////                            navController.navigate(R.id.action_pinVerificationDialog_to_dialogSuccessFragment);
////                            Navigation.findNavController(view).navigate(R.id.action_pinVerificationDialog_to_dialogSuccessFragment);
//                            DialogSuccessFragment dialogSuccessFragment = DialogSuccessFragment.newInstance();
//                            new Handler(Looper.getMainLooper()).postDelayed(dialogSuccessFragment::onDonate, 50);
//                            dialogSuccessFragment.show(getParentFragmentManager(), "DialogSuccessFragment");
//                        } else {
//                            // Show invalid PIN message
//                            numText.setText("Invalid PIN");
//                            dismiss();
//                            DialogSuccessFragment dialogSuccessFragment = DialogSuccessFragment.newInstance();
//                            new Handler(Looper.getMainLooper()).postDelayed(dialogSuccessFragment::onDonationError, 50);
//                            dialogSuccessFragment.show(getParentFragmentManager(), "DialogSuccessFragment");
//                        }
//                    });
//                }
//            } else {
////                numText.setText("Invalid PIN");
//            }
//        });

        buttonConfirm.setOnClickListener(v -> {
            if (viewModel.isPinValid()) {
                String enteredPin = viewModel.getPinText().getValue();
                viewModel.verifyPin(Integer.parseInt(enteredPin));

                // verifyPin() is an asynchronous operation, so we need to handle it accordingly
                    if (viewModel.getPinCorrect()) {
                        // PIN is correct, proceed to success screen
                        dismiss();
                        Log.d("Set On Donation: ", "Showing donation success");
                        DialogSuccessFragment dialogSuccessFragment = DialogSuccessFragment.newInstance();
                        new Handler(Looper.getMainLooper()).postDelayed(dialogSuccessFragment::onDonate, 50);
                        dialogSuccessFragment.show(getParentFragmentManager(), "DialogSuccessFragment");
                    } else {
                        // Show invalid PIN message
                        numText.setText("Incorrect PIN");
                        Log.d("Set On Donation Error: ", "Showing donation error");
                        dismiss();
                        DialogSuccessFragment dialogSuccessFragment = DialogSuccessFragment.newInstance();
                        new Handler(Looper.getMainLooper()).postDelayed(dialogSuccessFragment::onDonationError, 50);
                        dialogSuccessFragment.show(getParentFragmentManager(), "DialogSuccessFragment");
                    }
            } else {
                // Handle invalid PIN format case (if needed)
                numText.setText("Invalid PIN");
            }
        });

        // Cancel button listener
        buttonCancel.setOnClickListener(v -> cardView.setVisibility(View.GONE));
    }
}
