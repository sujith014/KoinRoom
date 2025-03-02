package com.sujith.kotlin.koinnew.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.sujith.kotlin.koinnew.model.Tweet
import kotlinx.coroutines.flow.Flow


@Dao
interface TweetsDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(tweet: Tweet)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(tweets: List<Tweet>)

    @Query("SELECT * FROM tweets_table")
    fun getAllTweets(): Flow<List<Tweet>>

    @Query("SELECT DISTINCT category FROM tweets_table")
    fun getCategories(): Flow<List<String>>

}