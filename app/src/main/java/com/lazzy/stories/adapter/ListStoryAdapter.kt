package com.lazzy.stories.adapter

import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.lazzy.stories.data.model.ListStoryItem
import com.lazzy.stories.databinding.ItemListBinding

class ListStoryAdapter : PagingDataAdapter<ListStoryItem, ListStoryAdapter.ListViewHolder>(DIFF_CALLBACK){
    class ListViewHolder(private val binding: ItemListBinding) : RecyclerView.ViewHolder(binding.root){
        fun bind(listStories: ListStoryItem){
            Glide.with(binding.ivPhoto)
                .load(listStories.photoUrl)
                .fitCenter()
                .into(binding.ivPhoto)
            binding.tvName.text = listStories.name

            itemView.setOnClickListener {

            }
        }

    }

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        TODO("Not yet implemented")
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
        TODO("Not yet implemented")
    }

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<ListStoryItem>() {
            override fun areItemsTheSame(
                oldItem: ListStoryItem,
                newItem: ListStoryItem,
            ): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(
                oldItem: ListStoryItem,
                newItem: ListStoryItem,
            ): Boolean {
                return oldItem == newItem
            }

        }
    }

}