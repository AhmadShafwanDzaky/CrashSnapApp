package com.capstone.crashsnap

import android.app.Activity
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.app.ActivityOptionsCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.capstone.crashsnap.databinding.ItemHistoryBinding
import com.capstone.crashsnap.ui.history.HistoryDetailActivity

class SectionsPageAdapter :
    ListAdapter<ListStoryItem, SectionsPageAdapter.MyViewHolder>(DIFF_CALLBACK) {

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
        fun bind(item: ItemHistoryBinding) {
            binding.tvItemTitle.text = item?.name ?: "name"
            binding.tvItemDescription.text = item?.description ?: "desc"
            Glide.with(itemView.context)
                .load(item.photoUrl)
                .into(binding.ivPhoto)

            binding.root.setOnClickListener {
                val context = it.context
                val intent = Intent(context, HistoryDetailActivity::class.java)
                intent.putExtra(HistoryDetailActivity.EXTRA_STORY_ID, item.id)
                context.startActivity(intent)
            }
        }
    }

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<ListStoryItem>() {
            override fun areItemsTheSame(oldItem: ListStoryItem, newItem: ListStoryItem): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(
                oldItem: ListStoryItem,
                newItem: ListStoryItem
            ): Boolean {
                return oldItem == newItem
            }
        }
    }
}