package com.capstone.crashsnap.ui.result

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.bumptech.glide.Glide
import com.capstone.crashsnap.R
import com.capstone.crashsnap.data.remote.response.Data
import com.capstone.crashsnap.databinding.ActivityResultBinding
import com.capstone.crashsnap.ui.main.MainActivity
import com.capstone.crashsnap.ui.preview.PreviewActivity.Companion.EXTRA_CLASSIFICATION_RESULT
import com.capstone.crashsnap.ui.profile.ProfileActivity

class ResultActivity : AppCompatActivity() {
    private lateinit var binding: ActivityResultBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityResultBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val imageUri = Uri.parse(intent.getStringExtra(EXTRA_IMAGE_STRING))
        val classificationResult = intent.getSerializableExtra(EXTRA_CLASSIFICATION_RESULT) as? Data

        binding.previewImageView.setImageURI(imageUri)
        classificationResult?.let {
//            Glide.with(this)
//                .load(imagePrediction(it))
//                .into(binding.previewImageView)
            Log.d("Image URL", imagePrediction(it))
            binding.tvCarDamage.text = resultDescription(it)
            binding.tvDescription.text = costPrediction(it)
        }

        binding.backHomeButton.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
        }

        binding.topAppBar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

    }

    private fun resultDescription(data: Data): String {
        val stringBuilder = StringBuilder()

        data.result?.forEach { resultItem ->
            resultItem?.let {
                stringBuilder.append("Damage Detected:\n")
                it.damageDetected?.forEach { damage ->
                    stringBuilder.append("- $damage\n")
                }
            }
        }

        return stringBuilder.toString()
    }

    private fun costPrediction(data: Data): String {
        val stringBuilder = StringBuilder()

        data.result?.forEach { resultItem ->
            resultItem?.let {

                stringBuilder.append("\nCost Prediction:\n")
                it.costPredict?.forEach { cost ->
                    stringBuilder.append("Min Cost: ${cost?.minCost}\n")
                    stringBuilder.append("Max Cost: ${cost?.maxCost}\n\n")
                }
            }
        }

        return stringBuilder.toString()
    }

    private fun imagePrediction(data: Data): String {
        val stringBuilder = StringBuilder()

        data.result?.forEach { resultItem ->
            resultItem?.let {
                stringBuilder.append("Damage Detected:\n")
                it.imageUrl?.forEach { damage ->
                    stringBuilder.append("- $damage\n")
                }
            }
        }

        return stringBuilder.toString()
    }
    companion object {
        const val EXTRA_IMAGE_STRING = "extra_image_string"
    }
}