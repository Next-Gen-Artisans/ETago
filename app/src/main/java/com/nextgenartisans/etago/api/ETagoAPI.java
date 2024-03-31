package com.nextgenartisans.etago.api;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface ETagoAPI {

    @Multipart
    @POST("/detection/img_object_detection_to_img")
    Call<ResponseBody> uploadImage(@Part("file\"; filename=\"image.jpg\" ") RequestBody file);

}
