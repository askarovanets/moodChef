package com.example.moodchef;

import java.util.HashMap;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface MoodService {
    @Headers("Content-Type: application/json")
    @POST("v3/emotion")  // Update this endpoint based on your API
    Call<MoodResponse> analyzeMood(@Body HashMap<String, String> body);
}
