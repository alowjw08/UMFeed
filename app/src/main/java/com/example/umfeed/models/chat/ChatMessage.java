package com.example.umfeed.models.chat;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.PropertyName;

import java.util.Objects;

public class ChatMessage {
    private String messageId;
    private String message;
    private String type;  // "user" or "bot"
    private Timestamp timestamp;

    private boolean isSending;
    private boolean hasError;
    private String errorMessage;
    public ChatMessage() {}

    public ChatMessage(String message, String type) {
        this.message = message;
        this.type = type;
        this.timestamp = Timestamp.now();
        this.isSending = false;
        this.hasError = false;
    }

    @PropertyName("messageId")
    public String getMessageId() {
        return messageId;
    }

    @PropertyName("messageId")
    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    @PropertyName("message")
    public String getMessage() {
        return message;
    }

    @PropertyName("message")
    public void setMessage(String message) {
        this.message = message;
    }

    @PropertyName("type")
    public String getType() {
        return type;
    }

    @PropertyName("type")
    public void setType(String type) {
        this.type = type;
    }

    @PropertyName("timestamp")
    public Timestamp getTimestamp() {
        return timestamp;
    }

    @PropertyName("timestamp")
    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChatMessage that = (ChatMessage) o;
        return Objects.equals(messageId, that.messageId) &&
                Objects.equals(message, that.message) &&
                Objects.equals(type, that.type) &&
                Objects.equals(timestamp, that.timestamp);
    }

    @Override
    public int hashCode() {
        return Objects.hash(messageId, message, type, timestamp);
    }

    @Override
    public String toString() {
        return "ChatMessage{" +
                "messageId='" + messageId + '\'' +
                ", message='" + message + '\'' +
                ", type='" + type + '\'' +
                ", timestamp=" + timestamp +
                '}';
    }

    public boolean isSending() {
        return isSending;
    }

    public void setSending(boolean sending) {
        isSending = sending;
    }

    public boolean hasError() {
        return hasError;
    }

    public void setHasError(boolean hasError) {
        this.hasError = hasError;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
        this.hasError = errorMessage != null;
    }
}