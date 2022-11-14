package com.dicoding.intermediate.storyapp.ui.main

import android.app.Activity
import android.content.Intent
import android.location.Geocoder
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityOptionsCompat
import androidx.core.util.Pair
import androidx.core.view.marginTop
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.dicoding.intermediate.storyapp.R
import com.dicoding.intermediate.storyapp.data.remote.response.ListStoryItem
import com.dicoding.intermediate.storyapp.databinding.ItemRowStoryBinding
import com.dicoding.intermediate.storyapp.ui.storydetail.DetailActivity
import com.dicoding.intermediate.storyapp.ui.storydetail.DetailActivity.Companion.EXTRA_DETAIL
import com.dicoding.intermediate.storyapp.utils.Helper
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import okio.IOException
import java.util.*
import java.util.logging.Handler
import kotlin.math.pow
import kotlin.math.round
import kotlin.math.roundToLong

class ListStoryAdapter: PagingDataAdapter<ListStoryItem, ListStoryAdapter.ListStoryVH>(DIFF_CALLBACK) {

    private var helper = Helper()

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ListStoryVH {
        val binding = ItemRowStoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ListStoryVH(binding)
    }

    override fun onBindViewHolder(holder: ListStoryVH, position: Int) {
        val item = getItem(position)
        if (item != null) {
            holder.bind(item)
        }
    }

    inner class ListStoryVH(private val binding: ItemRowStoryBinding): RecyclerView.ViewHolder(binding.root) {
        @OptIn(DelicateCoroutinesApi::class)
        fun bind(listStory: ListStoryItem) {
            with(binding) {
                helper.apply {
                    tvItemUsername.text = listStory.name
                    tvItemDescription.text = listStory.description.trim()
                    tvItemDateCreated.text = listStory.createdAt.withDateFormat()

//                    val geoCoder = Geocoder(itemView.context, Locale.getDefault())
//                    val latitude = String.format("%.6f", listStory.lat).toDouble()
//                    val longitude = String.format("%.6f", listStory.lon).toDouble()
//
//                    GlobalScope.launch(Dispatchers.Main) {
//                        try {
//                            val list = geoCoder.getFromLocation(latitude, longitude, 1)
//                            if (list != null && list.size != 0) {
//                               launch(Dispatchers.Main) {
//                                   binding.tvItemLocation.text = list.first().adminArea
//                               }
//                            }
//                        } catch (e: Exception) {
//                            e.printStackTrace()
//                        }
//                    }


                    Glide.with(itemView.context)
                        .load(listStory.photoUrl)
                        .placeholder(R.drawable.image_loading_placeholder)
                        .error(R.drawable.image_load_error)
                        .into(tvItemStoryImage)

                    itemView.setOnClickListener {
                        val optionsCompat: ActivityOptionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(
                            itemView.context as Activity,
                            Pair(tvItemUsername, "username"),
                            Pair(tvItemStoryImage, "story_image"),
                            Pair(tvItemDescription, "description"),
                            Pair(tvItemDateCreated, "date")
                        )
                        Intent(itemView.context, DetailActivity::class.java).also { intent ->
                            intent.putExtra(EXTRA_DETAIL, listStory)
                            itemView.context.startActivity(intent, optionsCompat.toBundle())
                        }
                    }
                }
            }
        }
    }

    companion object {
        val DIFF_CALLBACK: DiffUtil.ItemCallback<ListStoryItem> =
            object : DiffUtil.ItemCallback<ListStoryItem>() {
                override fun areItemsTheSame(oldItem: ListStoryItem, newItem: ListStoryItem): Boolean {
                    return oldItem.id == newItem.id
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