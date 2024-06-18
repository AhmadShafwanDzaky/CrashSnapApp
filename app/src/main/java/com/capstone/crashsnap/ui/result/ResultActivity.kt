package com.capstone.crashsnap.ui.result

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.bumptech.glide.Glide
import com.capstone.crashsnap.R
import com.capstone.crashsnap.convertIsoDateToFull
import com.capstone.crashsnap.data.remote.response.Data
import com.capstone.crashsnap.data.remote.response.DataDetail
import com.capstone.crashsnap.data.remote.response.ResultItemDetail
import com.capstone.crashsnap.databinding.ActivityResultBinding
import com.capstone.crashsnap.ui.main.MainActivity
import com.capstone.crashsnap.ui.maps.MapsActivity
import com.capstone.crashsnap.ui.preview.PreviewActivity.Companion.EXTRA_CLASSIFICATION_RESULT
import com.capstone.crashsnap.ui.profile.ProfileActivity
import com.capstone.crashsnap.ui.tips.TipsActivity

class ResultActivity : AppCompatActivity() {
    private lateinit var binding: ActivityResultBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityResultBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val imageUri = Uri.parse(intent.getStringExtra(EXTRA_IMAGE_STRING))
        val classificationResult = intent.getSerializableExtra(EXTRA_CLASSIFICATION_RESULT) as? Data

//        binding.previewImageView.setImageURI(imageUri)
        classificationResult?.let {data ->
            Glide.with(this)
                .load(imagePrediction(data))
                .into(binding.ivPhoto)
            Log.d("Image URL", imagePrediction(data))
            Log.d("Image URI", imageUri.toString())
            binding.tvValueMinCost.text = minCost(data)
            binding.tvValueMaxCost.text = maxCost(data)
            binding.tvDamages.text = resultDescription(data)
            binding.tvDate.text = getDate(data)
            binding.tvDamageTotal.text = totalDamage(data)
        }


        binding.backHomeButton.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }

        binding.topAppBar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        binding.repairShopButton.setOnClickListener {
            startActivity(Intent(this, MapsActivity::class.java))
        }

        binding.expertTipsButton.setOnClickListener {
            startActivity(Intent(this, TipsActivity::class.java))
        }

    }

    private fun resultDescription(data: Data): String {
        val stringBuilder = StringBuilder()
        data.result?.forEach { resultItem ->
            resultItem?.let {
                it.damageDetected?.forEachIndexed { index, damage ->
                    stringBuilder.append("• $damage")
                    if (index != it.damageDetected.size - 1) {
                        stringBuilder.append("\n")
                    }
                }
            }
        }
        return stringBuilder.toString()
    }



    private fun imagePrediction(data: Data): String {
        val stringBuilder = StringBuilder()

        data.result?.forEach { resultItem ->
            resultItem?.let {
                stringBuilder.append(it.imageUrl)
            }
        }

        return stringBuilder.toString()
    }

    private fun minCost(data: Data): String {
        val stringBuilder = StringBuilder()
        data.result?.forEach { item ->
            item.let {
                it?.costPredict?.forEach { cost ->
                    stringBuilder.append("Rp. ${cost?.minCost}")
                }
            }
        }

        return stringBuilder.toString()
    }

    private fun maxCost(data: Data): String {
        val stringBuilder = StringBuilder()
        data.result?.forEach { item ->
            item.let {
                it?.costPredict?.forEach { cost ->
                    stringBuilder.append("Rp. ${cost?.maxCost}")
                }
            }
        }
        return stringBuilder.toString()
    }

    private fun totalDamage(data: Data): String {
        val stringBuilder = StringBuilder()
        data.result?.forEach {
            val total = it?.damageDetected?.count()
            stringBuilder.append("$total")
        }
        return stringBuilder.toString()
    }


    private fun getDate(data: Data): String {
        val stringBuilder = StringBuilder()
        val date = data.createdAt?.let { convertIsoDateToFull(it) }
        stringBuilder.append("$date")
        return stringBuilder.toString()
    }
    companion object {
        const val EXTRA_IMAGE_STRING = "extra_image_string"
    }
}