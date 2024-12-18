package com.example.storyappsubmission.view.detail

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import com.bumptech.glide.Glide
import com.example.storyappsubmission.R
import com.example.storyappsubmission.databinding.ActivityDetailBinding
import com.example.storyappsubmission.view.ViewModelFactory

class DetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailBinding

    private val viewModel by viewModels<DetailViewModel>(){
        ViewModelFactory.getInstance(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val id = intent.getStringExtra(EXTRA_ID)

        showDetail(id.toString())

        viewModel.loading.observe(this) {
            showLoading(it)
        }


        detailStories()
    }


    private fun showDetail(id:String) {
        viewModel.getToken().observe(this@DetailActivity) {
            val token = "Bearer ${it.token}"
            viewModel.detailStory(token,id )
        }
    }
    /*
        function to Show Detail Stories
     */
    private fun detailStories() {
        viewModel.user.observe(this@DetailActivity) { detailStory ->
            if (detailStory != null) {
                binding.titleTextView.text = detailStory.name
                binding.descTextView.text = detailStory.description
                Glide.with(binding.ImageView)
                    .load(detailStory.photoUrl)
                    .into(binding.ImageView)
            }
        }
    }

    /*
        Function to Show Loading Proggres Bar
     */
    private fun showLoading(isLoading: Boolean) {
        if (isLoading) {
            binding.progressBar.visibility = View.VISIBLE
        } else {
            binding.progressBar.visibility = View.GONE
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    companion object {
        const val EXTRA_ID = "id"
    }
}