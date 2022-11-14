package com.dicoding.intermediate.storyapp

import com.dicoding.intermediate.storyapp.data.remote.response.*
import kotlinx.coroutines.flow.Flow

object DataDummy {
    fun generateDummyStoryResponse(): List<ListStoryItem> {
        val items: MutableList<ListStoryItem> = arrayListOf()
        for (i in 0..100) {
            val story = ListStoryItem(
                i.toString(),
                "created $i",
                "name $i",
                "description $i",
                i.toDouble(),
                "id $i",
                i.toDouble()
            )
            items.add(story)
        }
        return items
    }

    fun generateDummyLoginResponse(): LoginResponse {
        return LoginResponse(
            LoginResult(
                "name",
            "id",
            "token"
            ),
            false,
            "token"
        )
    }

    fun generateDummyRegisterResponse(): RegisterResponse {
        return RegisterResponse(
            false,
            "success"
        )
    }

    fun generateDummyStoryWithLocationResponse(): GetAllStoriesResponse {
        val items: MutableList<ListStoryItem> = mutableListOf()
        for (i in 0..10) {
            val story = ListStoryItem(
                i.toString(),
                "created $i",
                "name $i",
                "description $i",
                i.toDouble(),
                "id $i",
                i.toDouble()
            )
            items.add(story)
        }
        return GetAllStoriesResponse(items, false, "success")
    }

    fun generateDummyAddNewStoryResponse(): FileUploadResponse {
        return FileUploadResponse(
            false,
            "success"
        )
    }
}