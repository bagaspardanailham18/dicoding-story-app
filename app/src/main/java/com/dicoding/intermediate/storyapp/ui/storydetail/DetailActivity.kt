package com.dicoding.intermediate.storyapp.ui.storydetail

import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.Window
import android.view.WindowInsetsController
import android.view.WindowManager
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.dicoding.intermediate.storyapp.R
import com.dicoding.intermediate.storyapp.data.remote.response.ListStoryItem
import com.dicoding.intermediate.storyapp.databinding.ActivityDetailBinding
import com.dicoding.intermediate.storyapp.utils.Helper

class DetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailBinding

    private val helper = Helper()

    companion object {
        const val EXTRA_DETAIL = "extra_detail"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupWindow()

        setSupportActionBar(binding.materialToolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        val storyData = intent.getParcelableExtra<ListStoryItem>(EXTRA_DETAIL)
        setStoryData(storyData)

    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    private fun setStoryData(story: ListStoryItem?) {
        binding.apply {
            helper.apply {
                tvItemUsername.text = story?.name
                tvItemLocation.text = "location"
                tvItemDescription.text = story?.description
                tvItemDateCreated.text = story?.createdAt?.withDateFormat()

                Glide.with(this@DetailActivity)
                    .load(story?.photoUrl)
                    .placeholder(R.drawable.image_loading_placeholder)
                    .error(R.drawable.image_load_error)
                    .into(tvItemStoryImage)
            }
        }
    }

    private fun setupWindow() {
        val window: Window = this.window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.statusBarColor = ContextCompat.getColor(this, R.color.white)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            val wic = window.decorView.windowInsetsController
            wic?.setSystemBarsAppearance(0, WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS)
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                window.setDecorFitsSystemWindows(false)
            } else {
                window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            }
        }

        setSupportActionBar(binding.materialToolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
    }
}