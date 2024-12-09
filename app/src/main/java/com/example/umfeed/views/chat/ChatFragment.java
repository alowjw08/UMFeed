package com.example.umfeed.views.chat;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.LinearLayout;
import android.widget.PopupMenu;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.umfeed.R;
import com.example.umfeed.adapters.ChatMessageAdapter;
import com.example.umfeed.databinding.FragmentChatBinding;
import com.example.umfeed.models.chat.ChatMessage;
import com.example.umfeed.viewmodels.chat.ChatViewModel;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;

public class ChatFragment extends Fragment implements ChatMessageAdapter.MessageClickListener {
    private FragmentChatBinding binding;
    private ChatViewModel viewModel;
    private ChatMessageAdapter adapter;
    private static final String TAG = "ChatFragment";

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = new ViewModelProvider(this).get(ChatViewModel.class);
        adapter = new ChatMessageAdapter(this);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentChatBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupRecyclerView();
        setupMessageInput();
        setupQuickActions();
        setupToolbar();
        observeViewModel();

        Log.d("ChatFragment", "Fragment created and views setup");
    }

    private void observeViewModel() {
        viewModel.getMessages().observe(getViewLifecycleOwner(), messages -> {
            Log.d("ChatFragment", "Received messages update. Count: " +
                    (messages != null ? messages.size() : 0));

            if (messages != null && !messages.isEmpty()) {
                adapter.submitList(new ArrayList<>(messages));  // Create new list to force update
                binding.chatRecyclerView.post(() -> {
                    binding.chatRecyclerView.scrollToPosition(messages.size() - 1);
                });
            }

            // Update empty state visibility
            binding.emptyStateView.setVisibility(
                    messages == null || messages.isEmpty() ? View.VISIBLE : View.GONE);
            binding.chatRecyclerView.setVisibility(
                    messages != null && !messages.isEmpty() ? View.VISIBLE : View.GONE);
        });

        viewModel.getIsLoading().observe(getViewLifecycleOwner(), isLoading -> {
            binding.loadingIndicator.setVisibility(isLoading ? View.VISIBLE : View.GONE);
            binding.messageInput.setEnabled(!isLoading);
            binding.sendButton.setEnabled(!isLoading);
        });

        viewModel.getIsLoadingMore().observe(getViewLifecycleOwner(), isLoadingMore -> {
            binding.loadingMoreIndicator.setVisibility(isLoadingMore ? View.VISIBLE : View.GONE);
        });

        viewModel.getError().observe(getViewLifecycleOwner(), error -> {
            if (error != null) {
                Snackbar.make(binding.getRoot(), error, Snackbar.LENGTH_LONG)
                        .setAction("Dismiss", v -> {})
                        .show();
            }
        });
    }

    @Override
    public void onMessageLongClick(ChatMessage message) {
        PopupMenu popup = new PopupMenu(requireContext(), binding.getRoot());
        popup.getMenu().add("Copy");
        if (message.getType().equals("user")) {
            popup.getMenu().add("Delete");
        }

        popup.setOnMenuItemClickListener(item -> {
            if ("Copy".equals(item.getTitle())) {
                copyMessageToClipboard(message.getMessage());
                return true;
            } else if ("Delete".equals(item.getTitle())) {
                viewModel.deleteMessage(message.getMessageId());
                return true;
            }
            return false;
        });
    }

    @Override
    public void onRetryClick(ChatMessage message) {
        viewModel.retryMessage(message);
    }
    private void copyMessageToClipboard(String message) {
        ClipboardManager clipboard = (ClipboardManager)
                requireContext().getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("Chat Message", message);
        clipboard.setPrimaryClip(clip);

        Snackbar.make(binding.getRoot(), "Message copied", Snackbar.LENGTH_SHORT).show();
    }

    private void setupQuickActions() {
        binding.weeklyPlanningChip.setOnClickListener(v ->
                sendPresetMessage("Can you help me plan my meals for the week?"));
        binding.whatToEatChip.setOnClickListener(v ->
                sendPresetMessage("What should I eat now?"));
    }

    private void sendPresetMessage(String message) {
        binding.messageInput.setText(message);
        binding.sendButton.performClick();
    }

    private void setupMessageInput() {
        binding.sendButton.setOnClickListener(v -> {
            String message = binding.messageInput.getText().toString().trim();
            if (!message.isEmpty()) {
                viewModel.sendMessage(message);
                binding.messageInput.setText("");
            }
        });

        // Add enter key listener
        binding.messageInput.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEND) {
                binding.sendButton.performClick();
                return true;
            }
            return false;
        });
    }

    private void setupRecyclerView() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(requireContext());
        layoutManager.setStackFromEnd(true);
        binding.chatRecyclerView.setLayoutManager(layoutManager);
        binding.chatRecyclerView.setAdapter(adapter);

        // Add scroll listener for pagination
        binding.chatRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (!recyclerView.canScrollVertically(-1) && dy < 0) {
                    viewModel.loadMoreMessages();
                }
            }
        });

        // Auto scroll to bottom on new messages
        adapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                Log.d("ChatFragment", "New items inserted: " + itemCount);
                layoutManager.scrollToPositionWithOffset(adapter.getItemCount() - 1, 0);
            }
        });
    }

    private void setupToolbar() {
        binding.toolbar.inflateMenu(R.menu.chat_menu);
        binding.toolbar.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.action_clear_chat) {
                showClearChatDialog();
                return true;
            }
            return false;
        });
    }

    private void showClearChatDialog() {
        new MaterialAlertDialogBuilder(requireContext())
                .setTitle("Clear Chat")
                .setMessage("Are you sure you want to clear all messages?")
                .setPositiveButton("Clear", (dialog, which) -> viewModel.clearChat())
                .setNegativeButton("Cancel", null)
                .show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
