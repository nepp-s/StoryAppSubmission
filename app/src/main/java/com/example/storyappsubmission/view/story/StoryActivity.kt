package com.example.storyappsubmission.view.story

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.storyappsubmission.R
import com.example.storyappsubmission.data.ResultState
import com.example.storyappsubmission.databinding.ActivityStoryBinding
import com.example.storyappsubmission.view.ViewModelFactory
import com.example.storyappsubmission.view.main.MainActivity

class StoryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityStoryBinding

    private var currentImageUri: Uri? = null

    private val viewModel by viewModels<StoryViewModel> {
        ViewModelFactory.getInstance(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()

        binding.galleryButton.setOnClickListener { startGallery() }
        binding.cameraButton.setOnClickListener { startCamera() }
        binding.uploadButton.setOnClickListener { uploadImage() }
    }

    /*
        Function To Open Gallery
     */
    private fun startGallery() {
        launchGallery.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }

    private val launchGallery = registerForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            currentImageUri = uri
            showImage()
        } else {
            Log.d("Photo Picker", "No Media Selected")
        }
    }

    /*
        Function To Open Camera
     */
    private fun startCamera() {
        currentImageUri = getImageUri(this)
        launchIntentCamera.launch(currentImageUri!!)
    }

    private val launchIntentCamera = registerForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { isSuccess ->
        if (isSuccess) {
            showImage()
        }
    }

    /*
        Function to ShowImage To Place Holder
     */
    private fun showImage() {
        currentImageUri?.let {
            Log.d("Image URI", "showImage: $it")
            binding.previewImageView.setImageURI(it)
        }
    }

    /*
        Function To Upload Image From API
     */
    private fun uploadImage() {
        viewModel.getToken().observe(this@StoryActivity) {
            currentImageUri?.let { uri ->
                val token = "Bearer ${it.token}"
                val imageFile = uriToFile(uri, this).reduceFileImage()
                Log.d("Image File", "ShowImage: ${imageFile.path}")
                val description = binding.edtDesc.text.toString()

                viewModel.uploadImage(token,imageFile, description).observe(this) { result ->
                    if (result != null) {
                        when (result) {
                            is ResultState.Loading -> {
                                showLoading(true)
                            }

                            is ResultState.Sucsess -> {
                                showToast(result.data.message.toString())
                                showLoading(false)
                                AlertDialog.Builder(this@StoryActivity).apply {
                                    setTitle(getString(R.string.yeah))
                                    setMessage(getString(R.string.succes_storry))
                                    setPositiveButton(getString(R.string.lanjut)) {_,_ ->
                                        val intent = Intent(this@StoryActivity, MainActivity::class.java)
                                        startActivity(intent)
                                    }
                                }
                                    .create()
                                    .show()
                            }

                            is ResultState.Error -> {
                                showToast(result.error)
                                showLoading(false)
                            }
                        }
                    }
                }

            }
        }

    }

    /*
        Function To Show Loading Bar
     */
    private fun showLoading(isLoading: Boolean) {
       binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    /*
        Function to Show Toast Text
     */
    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }


}