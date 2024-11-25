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
    private static final String API_KEY = BuildConfig.TWINWORD_API_KEY; // New API Key

    private OkHttpClient client;

    public TwinwordEmotionApiClient() {
        client = new OkHttpClient();
    }

    public void analyzeEmotion(String text, final ApiCallback callback) {
        MediaType mediaType = MediaType.parse("multipart/form-data; boundary=---011000010111000001101001");
        String requestBodyContent = "-----011000010111000001101001\r\n" +
                "Content-Disposition: form-data; name=\"text\"\r\n\r\n" +
                text + "\r\n" +
                "-----011000010111000001101001--";

        RequestBody body = RequestBody.create(mediaType, requestBodyContent);

        Request request = new Request.Builder()
                .url(API_URL)
                .post(body)
                .addHeader("x-rapidapi-key", API_KEY)
                .addHeader("x-rapidapi-host", "twinword-emotion-analysis-v1.p.rapidapi.com")
                .addHeader("Content-Type", "multipart/form-data; boundary=---011000010111000001101001")
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
