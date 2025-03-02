package com.sujith.kotlin.koinnew.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sujith.kotlin.koinnew.Repository.MyRepository
import com.sujith.kotlin.koinnew.model.Tweet
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch


open class MainViewModel(private val repository: MyRepository? = null) : ViewModel() {
    private val _tweets = MutableStateFlow<List<Tweet>>(emptyList())
    val tweetsFlow = _tweets.asStateFlow()

    private val _category = MutableStateFlow("")
    val category = _category.asStateFlow()

    private val _tweetText = MutableStateFlow("")
    val tweetText = _tweetText.asStateFlow()

    init {
        getAllTweets()
    }

    fun updateCategory(value: String) {
        _category.value = value
    }

    fun updateTweetText(value: String) {
        _tweetText.value = value
    }

    fun resetFields() {
        _category.value = ""
        _tweetText.value = ""
    }

    fun addTweet(tweet: Tweet) {
        viewModelScope.launch(Dispatchers.IO) {
            repository?.insertTweets(tweet)
            getAllTweets()
        }
    }

    private fun getAllTweets() {
        viewModelScope.launch(Dispatchers.IO) {
            repository?.getAllTweets()?.collect {
                _tweets.value = it
            }
        }
    }
}