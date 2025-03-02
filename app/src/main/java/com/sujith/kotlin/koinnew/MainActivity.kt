package com.sujith.kotlin.koinnew

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.sujith.kotlin.koinnew.model.Tweet
import com.sujith.kotlin.koinnew.ui.theme.KoinNewTheme
import com.sujith.kotlin.koinnew.viewmodel.MainViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : ComponentActivity() {
    val viewModel: MainViewModel by viewModel<MainViewModel>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        /*lifecycleScope.launch {
            val tweet = Tweet("hello", "hello")
            viewModel.addTweet(tweet) // Assuming it's a suspend function

            viewModel.getAllTweets() // Fetching tweets

            // Collecting tweets properly
            viewModel.tweetsFlow.collect { tweets ->
                println("All categories: $tweets")
            }
        }*/
        setContent {
            KoinNewTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    val tweets by viewModel.tweetsFlow.collectAsState()/*collectAsStateWithLifecycle()*/
                    AddTweet(
                        innerPadding,
                        viewModel::addTweet,
                        tweets,
                        viewModel.tweetText,
                        viewModel.category,
                        viewModel
                    )
                }
            }
        }
    }
}

@Composable
fun AddTweet(
    paddingValues: PaddingValues,
    addOnclick: (Tweet) -> Unit,
    tweets: List<Tweet>,
    tweet: StateFlow<String>,
    category: StateFlow<String>,
    viewModel: MainViewModel,
) {

//    val category by category.collectAsStateWithLifecycle()
//    val tweetText by tweet.collectAsStateWithLifecycle()
    val category by remember { viewModel.category }.collectAsStateWithLifecycle()
    val tweetText by remember { viewModel.tweetText }.collectAsStateWithLifecycle()
    Column(
        modifier = Modifier
            .padding(paddingValues)
            .fillMaxSize(0.95F),
        horizontalAlignment = Alignment.CenterHorizontally
    )
    {
        CustomOutlinedTextField(category, " category ") {
            viewModel.updateCategory(it)
        }
        CustomOutlinedTextField(tweetText, " Tweet ") {
            viewModel.updateTweetText(it)
        }

        Spacer(modifier = Modifier.padding(horizontal = 10.dp))
        Button(
            onClick = {
                if (category.isNotEmpty() && tweetText.isNotEmpty()) {
                    addOnclick(Tweet(category, tweetText))
                    viewModel.resetFields()
                }
            }
        ) {
            Text(text = "Add")
        }
        Spacer(
            modifier = Modifier
                .padding(horizontal = 10.dp)
                .background(color = Color.Yellow)
        )
        TweetsList(tweets)
    }
}

@Composable
fun CustomOutlinedTextField(value: String, labelText: String, onValueChange: (String) -> Unit) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange, // Proper state lifting
        maxLines = 1,
        label = { Text(text = labelText) },
        modifier = Modifier.fillMaxWidth(0.9f)
    )
}


@Composable
fun TweetsList(tweets: List<Tweet>) {
    LazyColumn(
        modifier = Modifier
            .padding(9.dp)
            .border(4.dp, color = Color.Blue)
            .fillMaxSize(),
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.Top,
        contentPadding = PaddingValues(16.dp)
    ) {
        items(tweets) {
            TweetItemList(it)
        }
    }
}

@Composable
fun TweetItemList(tweet: Tweet) {
    Card(
        shape = RectangleShape,
        elevation = CardDefaults.elevatedCardElevation(6.dp),
        colors = CardDefaults.cardColors(Color.LightGray),
        modifier = Modifier
            .padding(9.dp)
            .fillMaxWidth()
    ) {
        Text(text = "${tweet.category}")
        Text(text = "${tweet.tweet_text}")
    }
}

@Preview(showBackground = true)
@Composable
fun AddTweetPreview() {
    val dummyCategory = MutableStateFlow("Sample Category")
    val dummyTweetText = MutableStateFlow("Sample Tweet")
    val fakeViewModel = MainViewModel() // Temporary ViewModel for Preview

    AddTweet(
        paddingValues = PaddingValues(10.dp),
        addOnclick = {},
        tweets = listOf(Tweet("test", "tweet")),
        tweet = dummyTweetText,
        category = dummyCategory,
        viewModel = fakeViewModel
    )
}

