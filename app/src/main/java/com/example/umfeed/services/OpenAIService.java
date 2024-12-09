package com.example.umfeed.services;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class OpenAIService {
    private static final String TAG = "OpenAIService";
    private static final String API_URL = "https://api.openai.com/v1/chat/completions";
    private static final MediaType JSON = MediaType.get("application/json; charset=utf-8");
    private static final int MAX_RETRIES = 3;
    private static final int TIMEOUT_SECONDS = 30;
    private static final int MAX_HISTORY = 10;
    private final OkHttpClient client;
    private final String apiKey;
    private final List<JSONObject> messageHistory;
    private final JSONObject systemMessage = new JSONObject();


    public OpenAIService(String apiKey) {
        this.apiKey = apiKey;
        this.messageHistory = new ArrayList<>();

        this.client = new OkHttpClient.Builder()
                .connectTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
                .readTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
                .writeTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
                .addInterceptor(new RetryInterceptor(MAX_RETRIES))
                .build();

        validateApiKey();

        try {
            systemMessage.put("role", "system");
            systemMessage.put("content",
                    "You are Makan Buddy, a friendly AI assistant focused on helping users with " +
                            "meal planning, nutrition advice, and food-related questions. Be concise, " +
                            "friendly, and maintain context of the conversation. Remember previous " +
                            "suggestions and references to them. When you are prompted for weekly meal planning, " +
                            "suggest user with healthy and balanced diet for seven days including breakfast, lunch and dinner. " +
                            "If you are prompted \"What to eat now\", respond the user with a dish with name, "+
                            "calories, carbs, protein and fats in g, ingredients, allergens and cooking steps." +
                            "Always ensure the formatting of the text so that it is more readable for the user.");
        } catch (JSONException e) {
            Log.e(TAG, "Error creating system message", e);
        }
    }

    private void validateApiKey() {
        if (apiKey == null || apiKey.isEmpty()) {
            throw new IllegalStateException("API key cannot be null or empty");
        }
    }

    public String sendMessage(String message) throws IOException, JSONException {
        JSONObject jsonBody = createRequestBody(message);

        RequestBody body = RequestBody.create(jsonBody.toString(), JSON);
        Request request = new Request.Builder()
                .url(API_URL)
                .addHeader("Authorization", "Bearer " + apiKey)
                .post(body)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                handleErrorResponse(response);
            }

            String responseBody = response.body().string();
            return parseResponse(responseBody);
        }
    }

    private JSONObject createRequestBody(String message) throws JSONException {
        JSONObject jsonBody = new JSONObject();

        jsonBody.put("model", "gpt-4o");
        jsonBody.put("temperature", 0.7);

        JSONArray messages = new JSONArray();

        messages.put(systemMessage);

        // Add message history for context
        for (JSONObject historyMessage : messageHistory) {
            messages.put(historyMessage);
        }

        // Add current message
        JSONObject userMessage = new JSONObject();
        userMessage.put("role", "user");
        userMessage.put("content", message);
        messages.put(userMessage);

        messageHistory.add(userMessage);
        trimHistory();

        jsonBody.put("messages", messages);

        return jsonBody;
    }

    private void trimHistory() {
        // Keep only the most recent messages up to MAX_HISTORY
        if (messageHistory.size() > MAX_HISTORY) {
            messageHistory.subList(0, messageHistory.size() - MAX_HISTORY).clear();
        }
    }
    private void updateMessageHistory(JSONObject newMessage) {
        messageHistory.add(newMessage);
        if (messageHistory.size() > MAX_HISTORY) {
            messageHistory.remove(0);
        }
    }

    private String parseResponse(String responseBody) throws IOException {
        try {
            JSONObject jsonResponse = new JSONObject(responseBody);
            JSONArray choices = jsonResponse.getJSONArray("choices");
            JSONObject choice = choices.getJSONObject(0);
            JSONObject messageObj = choice.getJSONObject("message");
            String content = messageObj.getString("content");

            // Add response to history
            JSONObject assistantMessage = new JSONObject();
            assistantMessage.put("role", "assistant");
            assistantMessage.put("content", content);
            messageHistory.add(assistantMessage);
            trimHistory();

            return content;
        } catch (JSONException e) {
            throw new IOException("Error parsing response", e);
        }
    }

    private void handleErrorResponse(Response response) throws IOException {
        String errorBody = response.body().string();
        String errorMessage;
        try {
            JSONObject error = new JSONObject(errorBody);
            errorMessage = error.getJSONObject("error").getString("message");
        } catch (JSONException e) {
            errorMessage = "Unknown error occurred";
        }
        throw new IOException("API request failed: " + errorMessage);
    }

    static class RetryInterceptor implements Interceptor {
        private final int maxRetries;

        RetryInterceptor(int maxRetries) {
            this.maxRetries = maxRetries;
        }

        @Override
        public Response intercept(Chain chain) throws IOException {
            Request request = chain.request();
            IOException exception = null;

            for (int i = 0; i < maxRetries; i++) {
                try {
                    Response response = chain.proceed(request);
                    if (response.isSuccessful()) {
                        return response;
                    }
                    response.close();
                } catch (IOException e) {
                    exception = e;
                    Log.w(TAG, "Retry attempt " + (i + 1) + " failed", e);
                }
            }

            throw exception != null ? exception :
                    new IOException("Request failed after " + maxRetries + " retries");
        }
    }
}

