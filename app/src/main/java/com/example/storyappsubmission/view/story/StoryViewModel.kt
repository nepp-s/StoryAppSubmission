package com.example.storyappsubmission.view.story

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.example.storyappsubmission.data.preferences.TokenModel
import com.example.storyappsubmission.data.repository.Repository
import java.io.File

class StoryViewModel(private val repository: Repository): ViewModel() {

    /*
        Function To Upload Image From Repository
     */
    fun uploadImage(token: String,file:File, description: String) = repository.addStory(token,file,description)

    fun getToken(): LiveData<TokenModel> {
        return repository.getToken().asLiveData()
    }

}