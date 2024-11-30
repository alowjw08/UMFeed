package com.example.umfeed.views.auth;

import android.os.Bundle;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import com.example.umfeed.databinding.ActivityLoginBinding;
import com.example.umfeed.viewmodels.auth.LoginViewModel;
import com.example.umfeed.views.MainActivity;

import android.content.Intent;

public class LoginActivity extends AppCompatActivity {
    private ActivityLoginBinding binding;
    private LoginViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        viewModel = new ViewModelProvider(this).get(LoginViewModel.class);
        setupViews();
        observeViewModel();
    }

    private void setupViews() {
        binding.loginButton.setOnClickListener(v -> attemptLogin());

        binding.registerLink.setOnClickListener(v -> {
            Intent intent = new Intent(this, RegisterActivity.class);
            startActivity(intent);
        });

        binding.forgotPasswordLink.setOnClickListener(v -> {
            Intent intent = new Intent(this, ForgotPasswordActivity.class);
            startActivity(intent);
        });
    }

    private void observeViewModel() {
        viewModel.getIsLoading().observe(this, isLoading -> {
            binding.progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
            binding.loginButton.setEnabled(!isLoading);
        });

        viewModel.getErrorMessage().observe(this, error -> {
            binding.errorText.setText(error);
            binding.errorText.setVisibility(error != null ? View.VISIBLE : View.GONE);
        });

        viewModel.getIsLoggedIn().observe(this, isLoggedIn -> {
            if (isLoggedIn) {
                startActivity(new Intent(this, MainActivity.class));
                finish();
            }
        });
    }

    private void attemptLogin() {
        String email = binding.emailInput.getText().toString().trim();
        String password = binding.passwordInput.getText().toString().trim();
        viewModel.loginWithEmail(email, password);
    }
}