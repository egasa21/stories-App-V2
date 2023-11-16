package com.lazzy.stories.data.api

import android.util.Size
import com.lazzy.stories.data.model.LoginRequest
import com.lazzy.stories.data.model.LoginResponse
import com.lazzy.stories.data.model.RegisterRequest
import com.lazzy.stories.data.model.RegisterResponse
import com.lazzy.stories.data.model.StoriesResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Query

interface ApiService {

    @POST("register")
    fun register(@Body request: RegisterRequest) : Call<RegisterResponse>

    @POST("login")
    fun login(@Body request: LoginRequest) : Call<LoginResponse>

    @GET("stories")
    fun getStories(
        @Header("Authorization") token: String,
        @Query("location") location: Int
    ): Call<StoriesResponse>

    @Multipart
    @POST("stories")
    fun addStories(
        @Header("Authorization") token: String,
        @Part file: MultipartBody.Part,
        @Part("description") description: RequestBody,
        @Part("lat") lat: Double,
        @Part("lon") lon: Double
    ) : Call<StoriesResponse>

   @GET("stories")
   suspend fun getStoriesPage(
       @Header("Authorization") token: String,
       @Query("page") page: Int,
       @Query("size") size: Int
   ): StoriesResponse
}