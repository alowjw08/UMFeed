package com.example.umfeed.views.pin;

import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;

import android.app.AlertDialog;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import com.example.umfeed.R;
import com.example.umfeed.databinding.FragmentDialogSuccessBinding;
import com.example.umfeed.viewmodels.pin.DialogSuccessViewModel;

public class DialogSuccessFragment extends DialogFragment {

    public static final String TAG = "DialogSuccessFragment";

    // Define action constants
    private static final String ACTION_RESERVE = "RESERVE";
    private static final String ACTION_RESERVE_ERROR = "RESERVE_ERROR";
    private static final String ACTION_DONATE = "DONATE";
    private static final String ACTION_DONATE_ERROR = "DONATE_ERROR";
    private static final String ACTION_COLLECT = "COLLECT";
    private static final String ACTION_COLLECT_ERROR = "COLLECT_ERROR";

    private DialogSuccessViewModel viewModel;
    private FragmentDialogSuccessBinding binding;
    private String pendingAction = null;

    // Single method to handle all actions
    private synchronized void executeAction(String action) {
        Log.d(TAG, "Executing action: " + action);

        if (viewModel == null) {
            Log.d(TAG, "ViewModel not ready, queueing action: " + action);
            pendingAction = action;
            return;
        }

        try {
            switch (action) {
                case ACTION_RESERVE:
                    viewModel.setReservationMessage();
                    break;
                case ACTION_RESERVE_ERROR:
                    viewModel.setReservationErrorMessage();
                    break;
                case ACTION_DONATE:
                    viewModel.setDonationMessage();
                    break;
                case ACTION_DONATE_ERROR:
                    viewModel.setDonationErrorMessage();
                    break;
                case ACTION_COLLECT:
                    viewModel.setCollectionMessage();
                case ACTION_COLLECT_ERROR:
                    viewModel.setCollectionErrorMessage();
                default:
                    Log.e(TAG, "Unknown action: " + action);
            }
            Log.d(TAG, "Action executed successfully: " + action);
        } catch (Exception e) {
            Log.e(TAG, "Error executing action: " + action, e);
        }
    }

    // Public methods all follow same pattern
    public void onReserve() {
        executeAction(ACTION_RESERVE);
    }

    public void onReservationError() {
        executeAction(ACTION_RESERVE_ERROR);
    }

    public void onDonate() {
        executeAction(ACTION_DONATE);
    }

    public void onDonationError() {
        executeAction(ACTION_DONATE_ERROR);
    }

    public void onCollect() {
        executeAction(ACTION_COLLECT);
    }

    public void onCollectError() {
        executeAction(ACTION_COLLECT_ERROR);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = new ViewModelProvider(requireActivity()).get(DialogSuccessViewModel.class);

        // Execute any pending action
        if (pendingAction != null) {
            String action = pendingAction;
            pendingAction = null; // Clear before executing to avoid loops
            executeAction(action);
        }
    }

    public static DialogSuccessFragment newInstance() {
        return new DialogSuccessFragment();
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity(), R.style.TransparentDialog);
        binding = FragmentDialogSuccessBinding.inflate(getLayoutInflater());
        builder.setView(binding.getRoot());

        // Set up observers first
        setupObservers();

        Dialog dialog = builder.create();
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.getWindow().setLayout(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
            );
        }

        return dialog;
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Setup touch listener for dismissing on outside tap
        view.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                View cardView = binding.dialogSuccessCard;
                int[] location = new int[2];
                cardView.getLocationOnScreen(location);
                float x = event.getRawX();
                float y = event.getRawY();

                if (x < location[0] || x > location[0] + cardView.getWidth() ||
                        y < location[1] || y > location[1] + cardView.getHeight()) {
                    dismiss();
                    return true;
                }
            }
            return false;
        });

        // Observe ViewModel states
        viewModel.getMainText().observe(getViewLifecycleOwner(), text -> {
            if (binding.mainText != null && text != null) {
                binding.mainText.setText(text);
            }
        });

        viewModel.getHelperText().observe(getViewLifecycleOwner(), text -> {
            if (binding.helperText != null && text != null) {
                binding.helperText.setText(text);
            }
        });

        viewModel.getImageResource().observe(getViewLifecycleOwner(), resId -> {
            if (binding.statusDisplay != null && resId != null) {
                binding.statusDisplay.setImageResource(resId);
            }
        });
    }

    private void setupObservers() {
        Log.d(TAG, "Setting up observers");
        viewModel.getMainText().observe(this, text -> {
            Log.d(TAG, "Received main text update: " + text);
            if (binding != null && binding.mainText != null && text != null) {
                binding.mainText.setText(text);
                binding.mainText.setVisibility(View.VISIBLE);
            }
        });

        viewModel.getHelperText().observe(this, text -> {
            Log.d(TAG, "Received helper text update: " + text);
            if (binding != null && binding.helperText != null && text != null) {
                binding.helperText.setText(text);
                binding.helperText.setVisibility(View.VISIBLE);
            }
        });

        viewModel.getImageResource().observe(this, resId -> {
            Log.d(TAG, "Received image update");
            if (binding != null && binding.statusDisplay != null && resId != null) {
                binding.statusDisplay.setImageResource(resId);
                binding.statusDisplay.setVisibility(View.VISIBLE);
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}