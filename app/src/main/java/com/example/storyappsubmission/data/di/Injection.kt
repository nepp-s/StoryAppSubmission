package com.example.storyappsubmission.data.di


import android.content.Context
import com.example.storyappsubmission.data.preferences.TokenPreferences
import com.example.storyappsubmission.data.preferences.dataStore
import com.example.storyappsubmission.data.remote.retrofit.ApiConfig
import com.example.storyappsubmission.data.repository.Repository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

object Injection {
    fun provideRepository(context: Context): Repository {
        val pref = TokenPreferences.getInstance(context.dataStore)
        val apiService = ApiConfig.getApiService()
        return Repository.getInstance(apiService,pref)
    }
}