package com.example.ecrop;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface ApiService {
    @POST("recommend")
    Call<CropRecommendationResponse> getRecommendations(@Body CropInput input);
}
