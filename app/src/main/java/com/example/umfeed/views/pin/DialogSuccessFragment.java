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
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.umfeed.R;
import com.example.umfeed.viewmodels.pin.DialogSuccessViewModel;

public class DialogSuccessFragment extends DialogFragment {

    public static final String TAG = "DialogSuccessFragment";
    private DialogSuccessViewModel viewModel;
    private TextView mainTextView;
    private TextView helperTextView;
    private ImageView imageView;
    private ImageButton buttonCancel;

    public String getMainTextView() {
        return mainTextView.getText().toString();
    }

    public static DialogSuccessFragment newInstance() {
        return new DialogSuccessFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_dialog_success, container, false);

        // Initialize the views
        mainTextView = rootView.findViewById(R.id.mainText);
        helperTextView = rootView.findViewById(R.id.helperText);
        imageView = rootView.findViewById(R.id.statusDisplay);
//        buttonCancel = rootView.findViewById(R.id.cancelButton);

        // Add touch listener to detect taps outside the CardView
        rootView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // If the touch is outside the CardView, dismiss the dialog
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    // Check if the touch is outside the CardView
                    int[] location = new int[2];
                    View cardView = rootView.findViewById(R.id.dialogSuccessCard); // The ID of your CardView
                    cardView.getLocationOnScreen(location);
                    int cardViewX = location[0];
                    int cardViewY = location[1];
                    int cardViewWidth = cardView.getWidth();
                    int cardViewHeight = cardView.getHeight();

                    if (event.getRawX() < cardViewX || event.getRawX() > cardViewX + cardViewWidth ||
                            event.getRawY() < cardViewY || event.getRawY() > cardViewY + cardViewHeight) {
                        dismiss(); // Dismiss the dialog
                    }
                }
                return true;  // Consume the touch event
            }
        });
//        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(0x00ffffff));
        getDialog().getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
//        viewModel = new ViewModelProvider(this).get(DialogSuccessViewModel.class);
        viewModel = new ViewModelProvider(requireActivity()).get(DialogSuccessViewModel.class);

//        buttonCancel = view.findViewById(R.id.cancelButton);
//        buttonCancel.setOnClickListener(v -> dismissAllowingStateLoss());

        // Observe changes to the mainText, helperText, and imageResource
        viewModel.getMainText().observe(getViewLifecycleOwner(), mainText -> {
            if (mainTextView != null) {
                mainTextView.setText(mainText);
            }
        });
        viewModel.getHelperText().observe(getViewLifecycleOwner(), helperText -> {
            if (helperTextView != null) {
                helperTextView.setText(helperText);
            }
        });
        viewModel.getImageResource().observe(getViewLifecycleOwner(), imageRes -> {
            if (imageView != null) {
                imageView.setImageResource(imageRes);
            }
        });
    }

//@Override
//public Dialog onCreateDialog(Bundle savedInstanceState) {
//    return new AlertDialog.Builder(getActivity())
//            .setView(R.layout.fragment_dialog_success)
//            .create();
//}

@Override
public Dialog onCreateDialog(Bundle savedInstanceState) {
    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity())
            .setView(R.layout.fragment_dialog_success);

    // Create the dialog
    AlertDialog dialog = builder.create();

    // Use onPrepareDialog() to update the message before the dialog is displayed
    dialog.setOnShowListener(d -> {
        onPrepareDialog(dialog);
    });

    return dialog;
}
    public void onPrepareDialog(Dialog dialog) {
        TextView mainTextView = dialog.findViewById(R.id.mainText);
        TextView helperTextView = dialog.findViewById(R.id.helperText);
        ImageView imageView = dialog.findViewById(R.id.statusDisplay);

        viewModel.getMainText().observe(getViewLifecycleOwner(), mainText -> {
            if (mainTextView != null) {
                mainTextView.setText(mainText);
                Log.d("Main Text:", mainText);
            }
        });
        viewModel.getHelperText().observe(getViewLifecycleOwner(), helperText -> {
            if (helperTextView != null) {
                helperTextView.setText(helperText);
                Log.d("Helper Text:", helperText);
            }
        });
        viewModel.getImageResource().observe(getViewLifecycleOwner(), imageRes -> {
            if (imageView != null) {
                imageView.setImageResource(imageRes);
            }
        });
    }

    // TODO: create logic for this (if donate, onDonate. if reserve, onReserve) (put at donate&reserve button onClickListeners)
    public void onDonate() {
        viewModel.setDonationMessage();
    }
    public void onReserve() {
        viewModel.setReservationMessage();
    }
    public void onDonationError() {
        viewModel.setDonationErrorMessage();
    }
    public void onReservationError() {
        viewModel.setReservationErrorMessage();
    }
    public void onCollect() {
        viewModel.setCollectionMessage();
    }
    public void onCollectionError() {
        viewModel.setCollectionErrorMessage();
    }
}