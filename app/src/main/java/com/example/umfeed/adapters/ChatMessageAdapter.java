package com.example.umfeed.adapters;

import android.graphics.Color;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.example.umfeed.R;
import com.example.umfeed.models.chat.ChatMessage;

import io.noties.markwon.AbstractMarkwonPlugin;
import io.noties.markwon.Markwon;
import io.noties.markwon.core.MarkwonTheme;

public class ChatMessageAdapter extends ListAdapter<ChatMessage, RecyclerView.ViewHolder> {
    private static final int VIEW_TYPE_USER = 1;
    private static final int VIEW_TYPE_BOT = 2;
    private final MessageClickListener clickListener;

    public interface MessageClickListener {
        void onMessageLongClick(ChatMessage message);
        void onRetryClick(ChatMessage message);
    }
    public ChatMessageAdapter(MessageClickListener clickListener) {
        super(new DiffUtil.ItemCallback<ChatMessage>() {
            @Override
            public boolean areItemsTheSame(@NonNull ChatMessage oldItem, @NonNull ChatMessage newItem) {
                return oldItem.getMessageId().equals(newItem.getMessageId());
            }

            @Override
            public boolean areContentsTheSame(@NonNull ChatMessage oldItem, @NonNull ChatMessage newItem) {
                return oldItem.equals(newItem) &&
                        oldItem.hasError() == newItem.hasError();
            }
        });
        this.clickListener = clickListener;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        if (viewType == VIEW_TYPE_USER) {
            View view = inflater.inflate(R.layout.item_user_message, parent, false);
            return new UserMessageViewHolder(view, clickListener);
        } else {
            View view = inflater.inflate(R.layout.item_bot_message, parent, false);
            return new BotMessageViewHolder(view, clickListener);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ChatMessage message = getItem(position);
        if (holder instanceof UserMessageViewHolder) {
            ((UserMessageViewHolder) holder).bind(message);
        } else if (holder instanceof BotMessageViewHolder) {
            ((BotMessageViewHolder) holder).bind(message);
        }
    }

    @Override
    public int getItemViewType(int position) {
        ChatMessage message = getItem(position);
        return message.getType().equals("user") ? VIEW_TYPE_USER : VIEW_TYPE_BOT;
    }

    class UserMessageViewHolder extends RecyclerView.ViewHolder {
        private final TextView messageText;
        private final TextView timestampText;
        private final ImageButton retryButton;
        private final MessageClickListener listener;

        UserMessageViewHolder(@NonNull View itemView, MessageClickListener listener) {
            super(itemView);
            this.listener = listener;
            messageText = itemView.findViewById(R.id.messageText);
            timestampText = itemView.findViewById(R.id.timestampText);
            retryButton = itemView.findViewById(R.id.retryButton);

            itemView.setOnLongClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION && listener != null) {
                    listener.onMessageLongClick(getItem(position));
                    return true;
                }
                return false;
            });
        }

        void bind(ChatMessage message) {
            itemView.setTag(message);
            messageText.setText(message.getMessage());

            retryButton.setVisibility(message.hasError() ? View.VISIBLE : View.GONE);

            if (message.hasError()) {
                messageText.setAlpha(0.5f);
            } else {
                messageText.setAlpha(1.0f);
            }

            retryButton.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onRetryClick(message);
                }
            });

            // Format and show timestamp
            if (message.getTimestamp() != null) {
                long timestamp = message.getTimestamp().getSeconds() * 1000L;
                String formattedTime = DateUtils.getRelativeTimeSpanString(
                        timestamp,
                        System.currentTimeMillis(),
                        DateUtils.MINUTE_IN_MILLIS,
                        DateUtils.FORMAT_ABBREV_RELATIVE
                ).toString();
                timestampText.setText(formattedTime);
                timestampText.setVisibility(View.VISIBLE);
            } else {
                timestampText.setVisibility(View.GONE);
            }

            messageText.post(() -> {
                if (messageText.getLineCount() > 0) {
                    messageText.setMaxLines(messageText.getLineCount());
                }
            });
        }
    }

    class BotMessageViewHolder extends RecyclerView.ViewHolder {
        private final TextView messageText;
        private final TextView timestampText;
        private final Markwon markwon;

        BotMessageViewHolder(@NonNull View itemView, MessageClickListener listener) {
            super(itemView);
            messageText = itemView.findViewById(R.id.messageText);
            timestampText = itemView.findViewById(R.id.timestampText);

            markwon = Markwon.builder(itemView.getContext())
                    .usePlugin(new AbstractMarkwonPlugin() {
                        @Override
                        public void configureTheme(@NonNull MarkwonTheme.Builder builder) {
                            builder
                                    .codeTextColor(Color.BLACK)
                                    .codeBackgroundColor(Color.parseColor("#f5f5f5"));
                        }
                    })
                    .build();

            itemView.setOnLongClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION && listener != null) {
                    listener.onMessageLongClick(getItem(position));
                    return true;
                }
                return false;
            });
        }

        void bind(ChatMessage message) {
            String markdown = message.getMessage();
            markwon.setMarkdown(messageText, markdown);

            messageText.setLinkTextColor(ContextCompat.getColor(itemView.getContext(), R.color.colorPrimary));

            if (message.getTimestamp() != null) {
                long timestamp = message.getTimestamp().getSeconds() * 1000L;
                String formattedTime = DateUtils.getRelativeTimeSpanString(
                        timestamp,
                        System.currentTimeMillis(),
                        DateUtils.MINUTE_IN_MILLIS,
                        DateUtils.FORMAT_ABBREV_RELATIVE
                ).toString();
                timestampText.setText(formattedTime);
                timestampText.setVisibility(View.VISIBLE);
            } else {
                timestampText.setVisibility(View.GONE);
            }

            messageText.post(() -> {
                if (messageText.getLineCount() > 0) {
                    messageText.setMaxLines(messageText.getLineCount());
                }
            });
        }
    }
}
