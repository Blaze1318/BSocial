package com.example.firebaseauthenticationandstoragetest.Notifications;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface APIService {

    @Headers({
            "Content-Type:application/json",
            "Authorization:key=AAAAFTnu8aI:APA91bG3qY7mVPDh4vS8Xo4jB_NPd1a6DxvNaYLiC2Jjl6abRjDUAhF9PRwy3CGx1chzeH6MyrPnC8ULtH9GG-Ws2gI4nQRdTTJIAMWy9fMNVAU9Zx-7yLUA8qaTj6k-3E4f8d79q9Tf"
    })

    @POST("fcm/send")
    Call<Response> sendNotification(@Body Sender body);
}
