package com.capstone.crashsnap.ui.preview

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import com.capstone.crashsnap.R
import com.capstone.crashsnap.ViewModelFactory
import com.capstone.crashsnap.data.NetResult
import com.capstone.crashsnap.databinding.ActivityPreviewBinding
import com.capstone.crashsnap.data.remote.response.Data
import com.capstone.crashsnap.data.remote.response.FileUploadResponse
import com.capstone.crashsnap.data.remote.retrofit.ApiConfig
import com.capstone.crashsnap.ui.result.ResultActivity
import com.capstone.crashsnap.uriToFile
import com.google.gson.Gson
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import retrofit2.HttpException

class PreviewActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPreviewBinding
    private lateinit var imageUri: Uri

    private val viewModel by viewModels<PreviewViewModel> {
        ViewModelFactory.getInstance(this)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityPreviewBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        imageUri = Uri.parse(intent.getStringExtra(EXTRA_IMAGE_STRING))
        binding.previewImageView.setImageURI(imageUri)

        binding.analyzeButton.setOnClickListener {
            uploadImage()
        }
    }


    private fun startResultActivity(classificationResult: Data?) {
        val intent = Intent(this, ResultActivity::class.java).apply {
            putExtra(ResultActivity.EXTRA_IMAGE_STRING, imageUri.toString())
            putExtra(EXTRA_CLASSIFICATION_RESULT, classificationResult)
        }
        startActivity(intent)
    }

    private fun uploadImage() {
        imageUri?.let { uri ->
            val imageFile = uriToFile(uri, this)
            Log.d("Classification File", "showImage: ${imageFile.path}")
            showLoading(true)

            viewModel.uploadImage(imageFile,).observe(this) { result ->
                if (result != null) {
                    when (result) {
                        is NetResult.Loading -> {
                            showLoading(true)
                        }

                        is NetResult.Success -> {
                            result.data.message?.let { showToast(it) }
                            showLoading(false)
                            startResultActivity(result.data.data)
                        }

                        is NetResult.Error -> {
                            showToast(result.error)
                            showLoading(false)
                        }
                    }
                }
            }
        } ?: showToast(getString(R.string.empty_history))
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
    companion object {
        const val EXTRA_IMAGE_STRING = "extra_image_string"
        const val EXTRA_CLASSIFICATION_RESULT = "extra_classification_result"
    }
}