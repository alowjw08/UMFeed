package com.example.umfeed.views.auth;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.example.umfeed.R;
import com.example.umfeed.databinding.FragmentVerifyCodeBinding;
import com.example.umfeed.viewmodels.auth.VerifyCodeViewModel;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

public class VerifyCodeFragment extends Fragment {
    private FragmentVerifyCodeBinding binding;
    private VerifyCodeViewModel viewModel;
    private EditText[] digitInputs;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentVerifyCodeBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(this).get(VerifyCodeViewModel.class);

        String email = VerifyCodeFragmentArgs.fromBundle(getArguments()).getEmail();
        viewModel.setUserEmail(email);

        setupOTPInputs();
        setupViews();
        observeViewModel();
    }

    private void setupOTPInputs() {
        digitInputs = new EditText[]{
                binding.digit1, binding.digit2, binding.digit3, binding.digit4
        };

        for (int i = 0; i < digitInputs.length; i++) {
            final int index = i;
            digitInputs[i].addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if (s.length() == 1 && index < digitInputs.length - 1) {
                        digitInputs[index + 1].requestFocus();
                    }
                }

                @Override
                public void afterTextChanged(Editable s) {}
            });
        }
    }

    private void setupViews() {
        binding.verifyButton.setOnClickListener(v -> verifyCode());
        binding.resendCode.setOnClickListener(v -> viewModel.resendCode());
    }

    private void observeViewModel() {
        viewModel.getIsLoading().observe(getViewLifecycleOwner(), isLoading -> {
            binding.progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
            binding.verifyButton.setEnabled(!isLoading);
        });

        viewModel.getErrorMessage().observe(getViewLifecycleOwner(), error -> {
            if (error != null) {
                showErrorDialog(error);
            }
        });

        viewModel.getIsVerified().observe(getViewLifecycleOwner(), isVerified -> {
            if (isVerified) {
                Navigation.findNavController(requireView())
                        .navigate(R.id.action_verifyCode_to_createPassword);
            }
        });
    }

    private void verifyCode() {
        StringBuilder code = new StringBuilder();
        for (EditText input : digitInputs) {
            code.append(input.getText().toString());
        }
        viewModel.verifyCode(code.toString());
    }

    private void showErrorDialog(String message) {
        new MaterialAlertDialogBuilder(requireContext())
                .setTitle("Error")
                .setMessage(message)
                .setPositiveButton("OK", (dialog, which) -> {
                    if (message.equals("Invalid verification code")) {
                        for (EditText input : digitInputs) {
                            input.setText("");
                        }
                        digitInputs[0].requestFocus();
                    }
                })
                .show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}

