package com.example.umfeed.views.auth;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.IntentSenderRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import com.example.umfeed.databinding.ActivityLoginBinding;
import com.example.umfeed.viewmodels.auth.LoginViewModel;
import com.example.umfeed.views.MainActivity;
import com.google.android.gms.auth.api.identity.SignInCredential;
import com.google.android.gms.common.api.ApiException;

import android.content.Intent;

public class LoginActivity extends AppCompatActivity {
    private ActivityLoginBinding binding;
    private LoginViewModel viewModel;
    private static final int REQ_ONE_TAP = 2;
    private boolean showOneTapUI = true;

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
        binding.googleSignInButton.setOnClickListener(v -> beginSignIn());
    }

    private void beginSignIn() {
        viewModel.getOneTapClient().beginSignIn(viewModel.getSignInRequest())
                .addOnSuccessListener(this, result -> {
                    try {
                        IntentSenderRequest intentSenderRequest =
                                new IntentSenderRequest.Builder(result.getPendingIntent().getIntentSender()).build();
                        signInLauncher.launch(intentSenderRequest);
                    } catch (Exception e) {
                        Log.e("LoginActivity", "Couldn't start One Tap UI", e);
                    }
                })
                .addOnFailureListener(this, e -> {
                    // Handle failure or try traditional Google Sign In
                    Log.e("LoginActivity", "One Tap UI failed", e);
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

    private final ActivityResultLauncher<IntentSenderRequest> signInLauncher =
            registerForActivityResult(new ActivityResultContracts.StartIntentSenderForResult(), result -> {
                if (result.getResultCode() == RESULT_OK) {
                    try {
                        SignInCredential credential = viewModel.getOneTapClient().getSignInCredentialFromIntent(result.getData());
                        String idToken = credential.getGoogleIdToken();
                        if (idToken != null) {
                            viewModel.signInWithGoogle(idToken);
                        }
                    } catch (ApiException e) {
                        Log.e("LoginActivity", "Google sign in failed", e);
                    }
                }
            });
}