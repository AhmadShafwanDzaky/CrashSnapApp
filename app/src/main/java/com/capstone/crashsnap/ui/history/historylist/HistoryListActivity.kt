package com.capstone.crashsnap.ui.history.historylist

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.capstone.crashsnap.R
import com.capstone.crashsnap.SectionsPageAdapter
import com.capstone.crashsnap.ViewModelFactory
import com.capstone.crashsnap.data.NetResult
import com.capstone.crashsnap.data.remote.response.DataItem
import com.capstone.crashsnap.data.remote.response.HistoryResponse
import com.capstone.crashsnap.databinding.ActivityHistoryListBinding

class HistoryListActivity : AppCompatActivity() {
    private val viewModel by viewModels<HistoryListViewModel> {
        ViewModelFactory.getInstance(this)
    }
    private lateinit var binding: ActivityHistoryListBinding
    private var oldResult: NetResult<HistoryResponse>? = null
    private var oldData: List<DataItem>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHistoryListBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val layoutManager = LinearLayoutManager(this)
        binding.rvHistory.layoutManager = layoutManager

        binding.topAppBar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        getAllHistory()

        binding.layoutRefresh.setColorSchemeResources(R.color.redmaroon_normal)

        binding.layoutRefresh.setOnRefreshListener {
            getAllHistory()
        }

    }

    private fun getAllHistory() {
        val extraToken = intent.getStringExtra(EXTRA_TOKEN_HISTORYLIST).toString()
        viewModel.getSession().observe(this) { user ->
            var token = user.token
            if (token == null) {
                token = extraToken
            }
            viewModel.getAllHistory(token).observe(this) { result ->
                if (result == oldResult) {
                    return@observe
                }
                oldResult = result
                when (result) {
                    is NetResult.Success -> {
                        binding.layoutRefresh.isRefreshing = false
                        if (result.data.data == oldData) {
                            return@observe
                        }
                        if (result.data.data.isEmpty()) {
                            binding.tvEmpty.visibility = View.VISIBLE
                        } else {
                            binding.tvEmpty.visibility = View.GONE
                        }
                        val adapter = SectionsPageAdapter()
                        adapter.submitList(result.data.data)
                        binding.rvHistory.adapter = adapter

                    }

                    is NetResult.Error -> {
                        binding.layoutRefresh.isRefreshing = false
                        if (result.error.isNotEmpty()){
                            binding.tvEmpty.visibility = View.VISIBLE
                        }else {
                            binding.tvEmpty.visibility = View.GONE
                        }
                        val adapter = SectionsPageAdapter()
                        binding.rvHistory.adapter = adapter
                        val errorMessage: String = result.error
                        showToast(errorMessage)
                    }

                    is NetResult.Loading -> {
                        binding.layoutRefresh.isRefreshing = true
                    }

                    else -> {}
                }
            }
        }
    }


    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun showLoading(isLoading: Boolean) {
        if (isLoading) {
            binding.progressBar.visibility = View.VISIBLE

        } else {
            binding.progressBar.visibility = View.GONE
        }
    }

    companion object {
        const val EXTRA_TOKEN_HISTORYLIST = "extra_token_historylist"
    }
}