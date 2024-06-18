package com.capstone.crashsnap.ui.history.historydetail

import android.app.Dialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.Window
import android.widget.Toast
import androidx.activity.viewModels
import com.bumptech.glide.Glide
import com.capstone.crashsnap.R
import com.capstone.crashsnap.ViewModelFactory
import com.capstone.crashsnap.convertIsoDate
import com.capstone.crashsnap.convertIsoDateToFull
import com.capstone.crashsnap.data.NetResult
import com.capstone.crashsnap.data.remote.response.DataDetail
import com.capstone.crashsnap.data.remote.response.ResultItemDetail
import com.capstone.crashsnap.databinding.ActivityHistoryDetailBinding
import com.capstone.crashsnap.showAlertDialog
import com.capstone.crashsnap.ui.auth.LoginActivity
import com.capstone.crashsnap.ui.maps.MapsActivity
import com.capstone.crashsnap.ui.preview.PreviewActivity
import com.capstone.crashsnap.ui.tips.TipsActivity

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

        binding.repairShopButton.setOnClickListener {
            startActivity(Intent(this, MapsActivity::class.java))
        }

        binding.expertTipsButton.setOnClickListener {
            startActivity(Intent(this, TipsActivity::class.java))
        }

        viewModel.getSession().observe(this) { user ->
            if (!user.isLogin) {
                startActivity(Intent(this, LoginActivity::class.java))
                finish()
            }
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
                        showToast(getString(R.string.success_deleted))
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
                            Glide.with(this)
                                .load(detail.imageUrl)
                                .into(binding.ivPhoto)
                            binding.tvValueMinCost.text = minCost(detail)
                            binding.tvValueMaxCost.text = maxCost(detail)
                            binding.tvDamages.text = resultDescription(detail)
                        }
                        binding.tvDate.text = getDate(data)
                        binding.tvDamageTotal.text = totalDamage(data)

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
            it.damageDetected.forEachIndexed { index, damage ->
                stringBuilder.append("• $damage")
                if (index != it.damageDetected.size - 1) {
                    stringBuilder.append("\n")
                }
            }
        }
        return stringBuilder.toString()
    }



    private fun minCost(data: ResultItemDetail): String {
        val stringBuilder = StringBuilder()
        data.let {
            it.costPredict.forEach { cost ->
                stringBuilder.append("Rp. ${cost.minCost}")
            }
        }
        return stringBuilder.toString()
    }

    private fun maxCost(data: ResultItemDetail): String {
        val stringBuilder = StringBuilder()
        data.let {
            it.costPredict.forEach { cost ->
                stringBuilder.append("Rp. ${cost.maxCost}")
            }
        }
        return stringBuilder.toString()
    }

    private fun totalDamage(data: DataDetail): String {
        val stringBuilder = StringBuilder()
        data.result.forEach {
            val total = it.damageDetected.count()
            stringBuilder.append("$total")
        }
        return stringBuilder.toString()
    }

    private fun getDate(data: DataDetail): String {
        val stringBuilder = StringBuilder()
        data.let {
            val date = convertIsoDateToFull(it.createdAt)
            stringBuilder.append("$date")
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