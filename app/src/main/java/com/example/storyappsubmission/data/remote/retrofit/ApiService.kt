package com.example.storyappsubmission.data.remote.retrofit

import com.example.storyappsubmission.data.remote.response.AddStoryResponse
import com.example.storyappsubmission.data.remote.response.AllStoryResponse
import com.example.storyappsubmission.data.remote.response.DetailStoryResponse
import com.example.storyappsubmission.data.remote.response.ListStoryItem
import com.example.storyappsubmission.data.remote.response.LoginResponse
import com.example.storyappsubmission.data.remote.response.RegisterResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path

interface ApiService {
    @FormUrlEncoded
    @POST("register")
    suspend fun register(
        @Field("name") name: String,
        @Field("email") email: String,
        @Field("password") password: String
    ): RegisterResponse

    @FormUrlEncoded
    @POST("login")
    suspend fun login(
        @Field("email") email: String,
        @Field("password") password: String
    ): LoginResponse

    @GET("stories")
    fun getStories(
        @Header("Authorization") token: String
    ): Call<AllStoryResponse>

    @GET("stories/{id}")
    fun detailStories(
        @Header("Authorization") token: String,
       @Path("id") id: String,
    ): Call<DetailStoryResponse>

    @Multipart
    @POST("stories")
    suspend fun uploadImage(
        @Header("Authorization") token: String,
        @Part file: MultipartBody.Part,
        @Part("description") description: RequestBody,
    ): AddStoryResponse



}