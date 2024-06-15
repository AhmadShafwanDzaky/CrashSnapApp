package com.capstone.crashsnap.ui.history

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.capstone.crashsnap.databinding.ActivityHistoryDetailBinding
import com.capstone.crashsnap.ui.history.historylist.HistoryListActivity

class HistoryDetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityHistoryDetailBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHistoryDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.topAppBar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
        binding.deleteButton.setOnClickListener {
            startActivity(Intent(this, HistoryListActivity::class.java))
        }
    }

    companion object {
        const val EXTRA_HISTORY_ID = "extra_history_id"
    }
}