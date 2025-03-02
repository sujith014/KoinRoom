package com.sujith.kotlin.koinnew.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.sujith.kotlin.koinnew.model.Tweet

@Database(entities = [Tweet::class], version = 1, exportSchema = false)
abstract class MyDatabase : RoomDatabase() {
    abstract fun getTweetsDao(): TweetsDao
}