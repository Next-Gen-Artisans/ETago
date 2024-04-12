package com.nextgenartisans.etago.api;

import okhttp3.MultipartBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface ETagoAPI {

    String BASE_URL = "https://magical-together-orca.ngrok-free.app/";
    //Change this to your server's IP address https://magical-together-orca.ngrok-free.app/, https://weevil-content-locust.ngrok-free.app/, http://192.168.1.22:8080/,

    @Multipart
    @POST("/detection/img_object_detection_to_json")
    Call<ResponseBody> uploadImageForJson(@Part MultipartBody.Part file);

    // Endpoint for uploading an image and getting back an image with object detection annotations
    @Multipart
    @POST("/detection/img_object_detection_to_img")
    Call<ResponseBody> uploadImageForAnnotation(@Part MultipartBody.Part file);

    // Endpoint for uploading an annotated image and getting back a censored image
    @Multipart
    @POST("/detection/img_object_detection_to_censored_img")
    Call<ResponseBody> uploadImageForCensoring(@Part MultipartBody.Part file);


}
