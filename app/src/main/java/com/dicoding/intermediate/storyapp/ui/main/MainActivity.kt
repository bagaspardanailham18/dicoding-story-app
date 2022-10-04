package com.dicoding.intermediate.storyapp.ui.main

import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.view.*
import android.view.WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS
import android.widget.Toast
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.dicoding.intermediate.storyapp.R
import com.dicoding.intermediate.storyapp.data.remote.response.ListStoryItem
import com.dicoding.intermediate.storyapp.databinding.ActivityMainBinding
import com.dicoding.intermediate.storyapp.ui.addstory.AddStoryActivity
import com.dicoding.intermediate.storyapp.ui.auth.AuthActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private val mainViewModel: MainViewModel by viewModels()

    private val adapter = ListStoryAdapter()

    private var token: String = ""

    companion object {
        const val EXTRA_TOKEN = "extra_token"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupWindow()

        token = intent.getStringExtra(EXTRA_TOKEN)!!
        setupRvStoriesData()

        setupStoriesRv()
        setupRvWhenRefresh()
    }

    private fun setupRvStoriesData() {
        setLoadingState(true)
        binding.swipeToRefresh.isRefreshing = true
        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.RESUMED) {
                mainViewModel.getAllStories(token).collect { result ->
                    result.onSuccess { response ->
                        setLoadingState(false)
                        updateRecyclerViewData(response.listStory)
                        binding.apply {

                            swipeToRefresh.isRefreshing = false
                        }
                    }
                    result.onFailure {
                        Toast.makeText(
                            this@MainActivity,
                            "${it.message}",
                            Toast.LENGTH_SHORT
                        ).show()
                        setLoadingState(false)
                        binding.swipeToRefresh.isRefreshing = false
                    }
                }
            }
        }
    }

    private fun setupStoriesRv() {
        binding.apply {
            rvStories.layoutManager = LinearLayoutManager(this@MainActivity)
            rvStories.adapter = adapter
            rvStories.setHasFixedSize(true)
        }
    }

    private fun setupRvWhenRefresh() {
        binding.swipeToRefresh.setOnRefreshListener {
            setupRvStoriesData()
        }
    }

    private fun updateRecyclerViewData(stories: List<ListStoryItem>) {
        val recyclerViewState = binding.rvStories.layoutManager?.onSaveInstanceState()
        adapter.submitList(stories)
        binding.rvStories.layoutManager?.onRestoreInstanceState(recyclerViewState)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            R.id.menu_post -> {
                startActivity(Intent(this, AddStoryActivity::class.java))
            }
            R.id.menu_language -> {
                startActivity(Intent(Settings.ACTION_LOCALE_SETTINGS))
                return true
            }
            R.id.menu_logout -> {
                mainViewModel.clearAuthToken()
                startActivity(Intent(this@MainActivity, AuthActivity::class.java))
                finish()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun setLoadingState(state: Boolean) {
        binding.apply {
            if (state) {
                ObjectAnimator.ofFloat(binding.progressBar, View.ALPHA, 1f).start()
            } else {
                ObjectAnimator.ofFloat(binding.progressBar, View.ALPHA, 0f).start()
            }
        }
    }

    private fun setupWindow() {
        val window: Window = this.window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.statusBarColor = ContextCompat.getColor(this, R.color.white)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            val wic = window.decorView.windowInsetsController
            wic?.setSystemBarsAppearance(0, APPEARANCE_LIGHT_STATUS_BARS)
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                window.setDecorFitsSystemWindows(false)
            } else {
                window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            }
        }

        setSupportActionBar(binding.customToolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
    }
}