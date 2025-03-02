package com.sujith.kotlin.koinnew.Repository

import com.sujith.kotlin.koinnew.model.Tweet
import kotlinx.coroutines.flow.Flow

interface MyRepository {
    fun getText(): String

    suspend fun insertTweets(tweet: Tweet)

    suspend fun insertAllTweets(tweets: List<Tweet>)

    fun getAllTweets(): Flow<List<Tweet>>

    fun getAllCategories(): Flow<List<String>>
}