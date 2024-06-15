package com.capstone.crashsnap

import android.app.Activity
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.app.ActivityOptionsCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.capstone.crashsnap.data.remote.response.DataItem
import com.capstone.crashsnap.data.remote.response.HistoryResponse
import com.capstone.crashsnap.databinding.ItemHistoryBinding
import com.capstone.crashsnap.ui.history.HistoryDetailActivity

class SectionsPageAdapter :
    ListAdapter<DataItem, SectionsPageAdapter.MyViewHolder>(DIFF_CALLBACK) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = ItemHistoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val review = getItem(position)
        holder.bind(review)
    }

    class MyViewHolder(private val binding: ItemHistoryBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: DataItem) {
            item.result.forEach { res ->
                val damageString = res.damageDetected.joinToString("\n")
                binding.tvItemDescription.text = damageString
                val costRangeList = res.costPredict.map { cost ->
                    "Rp ${cost.minCost} - Rp ${cost.maxCost}"
                }
                val costRangeString = costRangeList.joinToString(", ")
                binding.tvItemTitle.text = costRangeString
                Glide.with(itemView.context)
                    .load(res.imageUrl)
                    .into(binding.ivPhoto)
            }

            val date = convertIsoDate(item.createdAt)

            binding.tvItemDate.text = date

            binding.root.setOnClickListener {
                val context = it.context
                val intent = Intent(context, HistoryDetailActivity::class.java)
                intent.putExtra(HistoryDetailActivity.EXTRA_HISTORY_ID, item.id)
                context.startActivity(intent)
            }

        }
    }

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<DataItem>() {
            override fun areItemsTheSame(oldItem: DataItem, newItem: DataItem): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(
                oldItem: DataItem,
                newItem: DataItem
            ): Boolean {
                return oldItem == newItem
            }
        }
    }
}