package com.example.storyappsubmission.view.main

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.storyappsubmission.data.ResultState
import com.example.storyappsubmission.data.preferences.TokenModel
import com.example.storyappsubmission.data.remote.response.AllStoryResponse
import com.example.storyappsubmission.data.remote.response.ListStoryItem
import com.example.storyappsubmission.data.remote.retrofit.ApiConfig
import com.example.storyappsubmission.data.remote.retrofit.ApiService
import com.example.storyappsubmission.data.repository.Repository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainViewModel(private val repository: Repository) : ViewModel() {

    private val _users = MutableLiveData<List<ListStoryItem>>()
    val users : LiveData<List<ListStoryItem>> = _users

    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean> = _loading





    /*
        Function To Get Token From Repository
     */
    fun getToken(): LiveData<TokenModel> {
        return repository.getToken().asLiveData()
    }


    /*
        Function To Delete Session From DataStore
     */
    fun logout() {
        viewModelScope.launch {
            repository.logout()
        }
    }

    fun saveToken(token:TokenModel) {
        viewModelScope.launch {
            repository.saveToken(token)
        }
    }

    /*
        Function To getStories from fetching Api Response
     */
    fun getAllStories(token: String) {
        _loading.value = true
        val client = repository.getStory(token)
        client.enqueue(object : Callback<AllStoryResponse> {
            override fun onResponse(
                call: Call<AllStoryResponse>,
                response: Response<AllStoryResponse>
            ) {
                if (response.isSuccessful && response.body() != null) {
                    _loading.value = false
                    _users.value = response.body()?.listStory
                } else {
                    Log.d(TAG,"onFailure ${response.message()}")
                }
            }

            override fun onFailure(call: Call<AllStoryResponse>, t: Throwable) {
                _loading.value = false
                Log.d(TAG,"onFailuere ${t.message.toString()}")
            }

        })

    }

    companion object {
        const val TAG = "MainViewModel"

    }

}