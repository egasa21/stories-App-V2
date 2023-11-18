package com.lazzy.stories.adapter

import android.app.Activity
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.app.ActivityOptionsCompat
import androidx.core.util.Pair
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.lazzy.stories.data.model.ListStoryItem
import com.lazzy.stories.databinding.ItemListBinding
import com.lazzy.stories.ui.detail.DetailActivity
import com.lazzy.stories.ui.detail.DetailActivity.Companion.EXTRA_DETAIL

class ListStoryAdapter : PagingDataAdapter<ListStoryItem, ListStoryAdapter.ListViewHolder>(DIFF_CALLBACK){
    class ListViewHolder(private val binding: ItemListBinding) : RecyclerView.ViewHolder(binding.root){
        fun bind(listStories: ListStoryItem){
            Glide.with(binding.ivPhoto)
                .load(listStories.photoUrl)
                .fitCenter()
                .into(binding.ivPhoto)
            binding.tvName.text = listStories.name

            itemView.setOnClickListener {
                val intent = Intent(itemView.context, DetailActivity::class.java)
                intent.putExtra(EXTRA_DETAIL, listStories)

                val optionsCompat: ActivityOptionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(
                    itemView.context as Activity,
                    Pair(binding.ivPhoto, "profileDetail"),
                    Pair(binding.tvName, "titleDetail")
                )
                itemView.context.startActivity(intent, optionsCompat.toBundle())
            }
        }

    }

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        val data = getItem(position)
        if (data != null) {
            holder.bind(data)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ListViewHolder (
        ItemListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )

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