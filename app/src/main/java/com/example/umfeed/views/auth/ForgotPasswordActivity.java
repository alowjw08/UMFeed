package com.example.umfeed.views.auth;

import android.os.Bundle;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import com.example.umfeed.R;
import com.example.umfeed.databinding.ActivityForgotPasswordBinding;
import com.example.umfeed.viewmodels.auth.ForgotPasswordViewModel;

public class ForgotPasswordActivity extends AppCompatActivity {
    private ActivityForgotPasswordBinding binding;
    private ForgotPasswordViewModel viewModel;
    private NavController navController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityForgotPasswordBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        viewModel = new ViewModelProvider(this).get(ForgotPasswordViewModel.class);

        NavHostFragment navHostFragment =
                (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);
        if (navHostFragment != null) {
            navController = navHostFragment.getNavController();
        }

        setupViews();
        observeViewModel();
    }

    private void setupViews() {
        binding.resetButton.setOnClickListener(v -> attemptReset());
        binding.backToLoginLink.setOnClickListener(v -> finish());
    }

    private void observeViewModel() {
        viewModel.getIsLoading().observe(this, isLoading -> {
            binding.progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
            binding.resetButton.setEnabled(!isLoading);
        });

        viewModel.getErrorMessage().observe(this, error -> {
            binding.errorText.setText(error);
            binding.errorText.setVisibility(error != null ? View.VISIBLE : View.GONE);
            binding.successText.setVisibility(View.GONE);
        });

        viewModel.getIsEmailSent().observe(this, isEmailSent -> {
            if (isEmailSent) {
                binding.successText.setVisibility(View.VISIBLE);
                binding.emailInput.setEnabled(false);
                binding.resetButton.setEnabled(false);

                String email = binding.emailInput.getText().toString().trim();
                navigateToVerifyCode(email);
            }
        });
    }

    private void navigateToVerifyCode(String email) {
        if (navController != null) {
            Bundle args = new Bundle();
            args.putString("email", email);
            navController.navigate(R.id.verifyCodeFragment, args);
        }
    }

    private void attemptReset() {
        String email = binding.emailInput.getText().toString().trim();
        viewModel.resetPassword(email);
    }
}