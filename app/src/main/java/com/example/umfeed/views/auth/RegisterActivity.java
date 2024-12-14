package com.example.umfeed.views.auth;

import android.os.Bundle;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import com.example.umfeed.databinding.ActivityRegisterBinding;
import com.example.umfeed.viewmodels.auth.RegisterViewModel;
import com.example.umfeed.views.MainActivity;

import android.content.Intent;

public class RegisterActivity extends AppCompatActivity {
    private ActivityRegisterBinding binding;
    private RegisterViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRegisterBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        viewModel = new ViewModelProvider(this).get(RegisterViewModel.class);
        setupViews();
        observeViewModel();
    }

    private void setupViews() {
        binding.registerButton.setOnClickListener(v -> attemptRegistration());
        binding.loginLink.setOnClickListener(v -> finish());
    }

    private void observeViewModel() {
        viewModel.getIsLoading().observe(this, isLoading -> {
            binding.progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
            binding.registerButton.setEnabled(!isLoading);
        });

        viewModel.getErrorMessage().observe(this, error -> {
            binding.errorText.setText(error);
            binding.errorText.setVisibility(error != null ? View.VISIBLE : View.GONE);
        });

        viewModel.getIsRegistered().observe(this, isRegistered -> {
            if (isRegistered) {
                startActivity(new Intent(this, MainActivity.class));
                finishAffinity();
            }
        });
    }

    private void attemptRegistration() {
        String firstName = binding.firstNameInput.getText().toString().trim();
        String lastName = binding.lastNameInput.getText().toString().trim();
        String email = binding.emailInput.getText().toString().trim();
        String password = binding.passwordInput.getText().toString().trim();
        String confirmPassword = binding.confirmPasswordInput.getText().toString().trim();

        viewModel.register(firstName, lastName, email, password, confirmPassword);
    }
}