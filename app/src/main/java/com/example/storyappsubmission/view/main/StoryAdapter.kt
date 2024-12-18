package com.example.storyappsubmission.view.main

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.app.ActivityOptionsCompat
import androidx.core.util.Pair
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.storyappsubmission.data.remote.response.ListStoryItem
import com.example.storyappsubmission.databinding.StoryRowBinding
import com.example.storyappsubmission.view.detail.DetailActivity

class StoryAdapter(private val listStory: List<ListStoryItem>) :
    ListAdapter<ListStoryItem, StoryAdapter.MyViewHolder>(DIFF_CALLBACK) {

    private lateinit var onItemClickCallback: OnItemClickCallback

    fun setOnItemClickCallback(onItemClickCallback: OnItemClickCallback) {
        this.onItemClickCallback = onItemClickCallback
    }



    class MyViewHolder(private val binding: StoryRowBinding) :
        RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("checkResult")
        fun bind(user: ListStoryItem) {
            binding.titleTextView.text = user.name
            binding.descTextView.text = user.description
            Glide.with(binding.ImageView)
                .load(user.photoUrl)
                .into(binding.ImageView)

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = StoryRowBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val list = getItem(position)
        holder.bind(list)
        holder.itemView.setOnClickListener {
            onItemClickCallback.onItemClicked(listStory[holder.adapterPosition])
        }
    }

    interface OnItemClickCallback {
        fun onItemClicked(user: ListStoryItem)
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