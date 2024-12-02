package com.example.umfeed.views.auth;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.umfeed.databinding.FragmentCreatePasswordBinding;
import com.example.umfeed.viewmodels.auth.CreateNewPasswordViewModel;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

public class CreateNewPasswordFragment extends Fragment {
    private FragmentCreatePasswordBinding binding;
    private CreateNewPasswordViewModel viewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentCreatePasswordBinding.inflate(inflater,container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(this).get(CreateNewPasswordViewModel.class);

        String email = CreateNewPasswordFragmentArgs.fromBundle(getArguments()).getEmail();
        viewModel.setEmail(email);

        setupViews();
        observeViewModel();
    }

    private void setupViews() {
        binding.saveButton.setOnClickListener(v -> updatePassword());
    }

    private void observeViewModel() {
        viewModel.getIsLoading().observe(getViewLifecycleOwner(), isLoading -> {
            binding.progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
            binding.saveButton.setEnabled(!isLoading);
            binding.passwordInput.setEnabled(!isLoading);
            binding.confirmPasswordInput.setEnabled(!isLoading);
        });

        viewModel.getErrorMessage().observe(getViewLifecycleOwner(), error -> {
            if (error != null) {
                showErrorDialog(error);
            }
        });

        viewModel.getIsPasswordUpdated().observe(getViewLifecycleOwner(), isUpdated -> {
            if (isUpdated) {
                showSuccessDialog();
            }
        });
    }
    private void updatePassword() {
        String newPassword = binding.passwordInput.getText().toString().trim();
        String confirmPassword = binding.confirmPasswordInput.getText().toString().trim();
        viewModel.updatePassword(newPassword, confirmPassword);
    }

    private void showErrorDialog(String message) {
        new MaterialAlertDialogBuilder(requireContext())
                .setTitle("Error")
                .setMessage(message)
                .setPositiveButton("OK", null)
                .show();
    }

    private void showSuccessDialog() {
        new MaterialAlertDialogBuilder(requireContext())
                .setTitle("Success")
                .setMessage("Your password has been updated successfully")
                .setPositiveButton("OK", (dialog, which) -> {
                    // Navigate to Login Activity after success
                    Intent loginIntent = new Intent(requireActivity(), LoginActivity.class);
                    // Clear any previous activities from stack
                    loginIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(loginIntent);
                    requireActivity().finish();
                })
                .setCancelable(false)
                .show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
