package com.sujith.kotlin.koinnew.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tweets_table")
data class Tweet(
    val category: String,
    @PrimaryKey
    val tweet_text: String,
)
