package com.example.moodchef;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import java.io.IOException;

public class TwinwordEmotionApiClient {

    private static final String API_URL = "https://twinword-emotion-analysis-v1.p.rapidapi.com/analyze/";
    private final String apiKey = BuildConfig.API_KEY;

    private OkHttpClient client;

    public TwinwordEmotionApiClient() {
        client = new OkHttpClient();
    }

    public void analyzeEmotion(String text, final ApiCallback callback) {
        MediaType mediaType = MediaType.parse("application/x-www-form-urlencoded");
        String requestBodyContent = "text=" + text;

        RequestBody body = RequestBody.create(mediaType, requestBodyContent);

        Request request = new Request.Builder()
                .url(API_URL)
                .post(body)
                .addHeader("Content-Type", "application/x-www-form-urlencoded")
                .addHeader("X-RapidAPI-Key", apiKey)
                .addHeader("X-RapidAPI-Host", "twinword-emotion-analysis-v1.p.rapidapi.com")
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                callback.onFailure(e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    callback.onSuccess(response.body().string());
                } else {
                    callback.onFailure(new IOException("Unexpected code " + response));
                }
            }
        });
    }

    public interface ApiCallback {
        void onSuccess(String jsonResponse);
        void onFailure(Exception e);
    }
}
