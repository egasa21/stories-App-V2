package com.lazzy.stories.ui.detail

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import com.bumptech.glide.Glide
import com.lazzy.stories.R
import com.lazzy.stories.data.model.ListStoryItem
import com.lazzy.stories.databinding.ActivityDetailBinding

class DetailActivity : AppCompatActivity() {

    private var _binding: ActivityDetailBinding? = null
    private val binding get() = _binding!!
    companion object {
        const val EXTRA_DETAIL = "extra_detail"
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.elevation = 0f
        supportActionBar?.title = "Detail User"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val detail = intent.getParcelableExtra<ListStoryItem>(EXTRA_DETAIL)
        Glide.with(this)
            .load(detail?.photoUrl)
            .into(binding.ivDetailStory)
        binding.tvDetailTitle.text = detail?.name
        binding.tvDetailDesc.text = detail?.description

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home){
            onBackPressedDispatcher.onBackPressed()
        }
        return super.onOptionsItemSelected(item)
    }

}