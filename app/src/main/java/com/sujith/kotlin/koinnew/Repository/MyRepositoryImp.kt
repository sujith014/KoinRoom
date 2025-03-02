package com.sujith.kotlin.koinnew.Repository

import android.content.Context
import com.sujith.kotlin.koinnew.R
import com.sujith.kotlin.koinnew.data.db.TweetsDao
import com.sujith.kotlin.koinnew.model.Tweet
import kotlinx.coroutines.flow.Flow

class MyRepositoryImp(
    private val context: Context,
    private val tweetsDao: TweetsDao,
) :
    MyRepository {
    override fun getText(): String {
        return "This is my repository ${context.getString(R.string.app_name)}"
    }

    override suspend fun insertTweets(tweet: Tweet) = tweetsDao.insert(tweet)

    override suspend fun insertAllTweets(tweets: List<Tweet>) = tweetsDao.insertAll(tweets)

    override fun getAllTweets(): Flow<List<Tweet>> = tweetsDao.getAllTweets()

    override fun getAllCategories(): Flow<List<String>> = tweetsDao.getCategories()
}