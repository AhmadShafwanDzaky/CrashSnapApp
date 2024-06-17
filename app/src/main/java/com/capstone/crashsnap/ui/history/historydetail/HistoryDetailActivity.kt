package com.capstone.crashsnap.ui.history.historydetail

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import com.bumptech.glide.Glide
import com.capstone.crashsnap.ViewModelFactory
import com.capstone.crashsnap.data.NetResult
import com.capstone.crashsnap.data.remote.response.ResultItemDetail
import com.capstone.crashsnap.databinding.ActivityHistoryDetailBinding
import com.capstone.crashsnap.ui.preview.PreviewActivity

class HistoryDetailActivity : AppCompatActivity() {
    private val viewModel by viewModels<HistoryDetailViewModel> {
        ViewModelFactory.getInstance(this)
    }
    private lateinit var binding: ActivityHistoryDetailBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHistoryDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)


        setupViews()
        getHistoryDetail()

    }

    private fun setupViews(){
        binding.topAppBar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
        binding.deleteButton.setOnClickListener {
            deleteHistoryId()
            finish()
        }
    }

    private fun deleteHistoryId() {
        val id = intent.getStringExtra(EXTRA_HISTORY_ID).toString()
        val extraToken = intent.getStringExtra(PreviewActivity.EXTRA_TOKEN_PREVIEW).toString()
        viewModel.getSession().observe(this) { user ->
            var token = user.token
            if (token == null) {
                token = extraToken
            }
            viewModel.deleteHistoryId(token, id).observe(this){ result ->
                when (result) {
                    is NetResult.Success -> {
                        showLoading(false)
                        showToast(result.data.message)
                    }

                    is NetResult.Error -> {
                        showLoading(false)
                        showToast(result.error)
                    }

                    is NetResult.Loading -> {
                        showLoading(true)
                    }

                    else -> {}

                }

            }
        }
    }

    private fun getHistoryDetail() {
        val id = intent.getStringExtra(EXTRA_HISTORY_ID).toString()
        val extraToken = intent.getStringExtra(PreviewActivity.EXTRA_TOKEN_PREVIEW).toString()
        viewModel.getSession().observe(this) { user ->
            var token = user.token
            if (token == null) {
                token = extraToken
            }
            viewModel.getHistoryDetail(token, id).observe(this) { result ->
                when (result) {
                    is NetResult.Success -> {
                        showLoading(false)
                        val data = result.data.data
                        data.result.forEach { detail ->
                            Log.d("History Detail", "Detail: $detail")
                            Glide.with(this)
                                .load(detail.imageUrl)
                                .into(binding.ivPhoto)
                            binding.tvCarDamage.text = costPrediction(detail)
                            binding.tvDescription.text = resultDescription(detail)
                        }

                    }

                    is NetResult.Error -> {
                        showLoading(false)
                        showToast(result.error)

                    }

                    is NetResult.Loading -> {
                        showLoading(true)
                    }

                    else -> {}

                }
            }
        }

    }

    private fun resultDescription(data: ResultItemDetail): String {
        val stringBuilder = StringBuilder()
        data.let {
            stringBuilder.append("Damage Detected:\n")
            it.damageDetected.forEach { damage ->
                stringBuilder.append("- $damage\n")
            }
        }
        return stringBuilder.toString()
    }

    private fun costPrediction(data: ResultItemDetail): String {
        val stringBuilder = StringBuilder()
        data.let {
            stringBuilder.append("\nCost Prediction:\n")
            it.costPredict.forEach { cost ->
                stringBuilder.append("Min Cost: ${cost.minCost}\n")
                stringBuilder.append("Max Cost: ${cost.maxCost}\n\n")
            }
        }

        return stringBuilder.toString()
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    companion object {
        const val EXTRA_HISTORY_ID = "extra_history_id"
        const val EXTRA_TOKEN_DETAIL = "extra_token_detail"
    }
}