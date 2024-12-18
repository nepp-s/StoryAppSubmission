package com.example.storyappsubmission.data.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import com.example.storyappsubmission.data.ResultState
import com.example.storyappsubmission.data.preferences.TokenModel
import com.example.storyappsubmission.data.preferences.TokenPreferences
import com.example.storyappsubmission.data.remote.response.AddStoryResponse
import com.example.storyappsubmission.data.remote.response.AllStoryResponse
import com.example.storyappsubmission.data.remote.response.DetailStoryResponse
import com.example.storyappsubmission.data.remote.response.ErrorResponse
import com.example.storyappsubmission.data.remote.response.LoginResponse
import com.example.storyappsubmission.data.remote.response.RegisterResponse
import com.example.storyappsubmission.data.remote.retrofit.ApiService
import com.google.gson.Gson
import kotlinx.coroutines.flow.Flow
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Call
import retrofit2.HttpException
import java.io.File


class Repository private constructor(
    private val apiService: ApiService, private val tokenPreferences: TokenPreferences
) {

    /*
        Function To Login User From Fetching Api Service
     */
    fun loginUser(email: String, password: String): LiveData<ResultState<LoginResponse>> =
        liveData {
            emit(ResultState.Loading)
            try {
                val users = apiService.login(email, password)
                tokenPreferences.saveToken(TokenModel(users.loginResult?.token.toString()))
                emit(ResultState.Sucsess(users))
                Log.e("Succes", "Succes")
            } catch (e: HttpException) {
                val errorBody = e.response()?.errorBody()?.string()
                val errorResponse = Gson().fromJson(errorBody, ErrorResponse::class.java)
                emit(ResultState.Error(errorResponse.message!!))
            }
        }

    /*
        Function To Register User From Fetching Api Service
     */
    fun register(
        name: String, email: String, password: String
    ): LiveData<ResultState<RegisterResponse>> = liveData {
        emit(ResultState.Loading)
        try {
            val users = apiService.register(name, email, password)
            emit(ResultState.Sucsess(users))
            Log.d("Succes", "Succes")
        } catch (e: HttpException) {
            val errorBody = e.response()?.errorBody()?.string()
            val errorResponse = Gson().fromJson(errorBody, ErrorResponse::class.java)
            emit(ResultState.Error(errorResponse?.message!!))
        }
    }


    fun getStory(token: String): Call<AllStoryResponse> {
        return apiService.getStories(token)
    }

    fun detailStory(token: String,id: String): Call<DetailStoryResponse> {
        return apiService.detailStories(token,id)
    }

    fun addStory(token: String,imageFile: File, description: String) = liveData {
        emit(ResultState.Loading)
        val requestBody = description.toRequestBody("text/plain".toMediaType())
        val requestImageFile = imageFile.asRequestBody("image/jpeg".toMediaType())
        val multipartBody = MultipartBody.Part.createFormData(
            "photo", imageFile.name, requestImageFile
        )
        try {
            val successResponse = apiService.uploadImage(token,multipartBody, requestBody)
            emit(ResultState.Sucsess(successResponse))
        } catch (e: HttpException) {
            val errorBody = e.response()?.errorBody()?.string()
            val errorResponse = Gson().fromJson(errorBody, AddStoryResponse::class.java)
            emit(ResultState.Error(errorResponse.message!!))
        }
    }

    /*
        Function To Get Token
     */

    fun getToken(): Flow<TokenModel> {
        return tokenPreferences.getToken()
    }

    /*
        Function To Save Token From Data Store
     */
    suspend fun saveToken(token: TokenModel) {
        tokenPreferences.saveToken(token)
    }

    /*
        Function To Delete Or Logout Token From Data Store
     */
    suspend fun logout() {
        tokenPreferences.logout()
    }




    companion object {
        @Volatile
        private var instance: Repository? = null
        fun getInstance(apiService: ApiService, tokenPreferences: TokenPreferences): Repository =
            instance ?: synchronized(this) {
                instance ?: Repository(apiService,tokenPreferences)
            }.also { instance = it }

    }

}