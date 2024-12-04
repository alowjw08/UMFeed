package com.example.umfeed.views.auth;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.IntentSenderRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.umfeed.R;
import com.example.umfeed.databinding.ActivityLoginBinding;
import com.example.umfeed.viewmodels.auth.LoginViewModel;
import com.example.umfeed.views.MainActivity;
import com.google.android.gms.auth.api.identity.BeginSignInRequest;
import com.google.android.gms.auth.api.identity.SignInCredential;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInStatusCodes;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;

import android.content.Intent;

public class LoginActivity extends AppCompatActivity {
    private ActivityLoginBinding binding;
    private LoginViewModel viewModel;
    private static final int REQ_ONE_TAP = 2;
    private boolean showOneTapUI = true;
    private static final int RC_SIGN_IN = 9001;

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
        // Configure sign-in request with additional options
        BeginSignInRequest signInRequest = BeginSignInRequest.builder()
                .setGoogleIdTokenRequestOptions(BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
                        .setSupported(true)
                        // Request ID token with full access
                        .setServerClientId(getString(R.string.default_web_client_id))
                        .setFilterByAuthorizedAccounts(false)  // Show all Google accounts
                        .build())
                .setAutoSelectEnabled(true)
                .build();

        // Start sign-in flow with error handling
        viewModel.getOneTapClient().beginSignIn(signInRequest)
                .addOnSuccessListener(this, result -> {
                    try {
                        IntentSenderRequest intentSenderRequest =
                                new IntentSenderRequest.Builder(result.getPendingIntent().getIntentSender())
                                        .build();
                        signInLauncher.launch(intentSenderRequest);
                    } catch (Exception e) {
                        Log.e("LoginActivity", "Couldn't start One Tap UI: " + e.getMessage());
                        fallbackToTraditionalSignIn();
                    }
                })
                .addOnFailureListener(this, e -> {
                    Log.e("LoginActivity", "One Tap UI failed", e);
                    fallbackToTraditionalSignIn();
                });
    }

    private void fallbackToTraditionalSignIn() {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        GoogleSignInClient signInClient = GoogleSignIn.getClient(this, gso);
        Intent signInIntent = signInClient.getSignInIntent();

        try {
            startActivityForResult(signInIntent, RC_SIGN_IN);
        } catch (Exception e) {
            Log.e("LoginActivity", "Traditional sign-in failed", e);
            // Show error to user
            binding.errorText.setText("Google Sign-In is currently unavailable. Please try again later.");
            binding.errorText.setVisibility(View.VISIBLE);
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            try {
                Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
                GoogleSignInAccount account = task.getResult(ApiException.class);
                viewModel.signInWithGoogle(account.getIdToken());
            } catch (ApiException e) {
                Log.e("LoginActivity", "Google sign in failed", e);
                // Show appropriate error message to user
                String message = "Sign in failed: " + getSignInErrorMessage(e.getStatusCode());
                binding.errorText.setText(message);
                binding.errorText.setVisibility(View.VISIBLE);
            }
        }
    }

    private String getSignInErrorMessage(int statusCode) {
        switch (statusCode) {
            case GoogleSignInStatusCodes.SIGN_IN_CANCELLED:
                return "Sign in cancelled";
            case GoogleSignInStatusCodes.NETWORK_ERROR:
                return "Network error occurred";
            case GoogleSignInStatusCodes.SIGN_IN_CURRENTLY_IN_PROGRESS:
                return "Sign in already in progress";
            default:
                return "Unknown error occurred";
        }
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