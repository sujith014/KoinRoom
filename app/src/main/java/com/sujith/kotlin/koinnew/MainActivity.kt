package com.sujith.kotlin.koinnew

import android.app.Application
import android.content.Context
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.sujith.kotlin.koinnew.model.Tweet
import com.sujith.kotlin.koinnew.ui.theme.KoinNewTheme
import com.sujith.kotlin.koinnew.viewmodel.MainViewModel
import com.sujith.kotlin.koinnew.viewmodel.ObserveAsEvents
import com.sujith.kotlin.koinnew.viewmodel.SnackBarAction
import com.sujith.kotlin.koinnew.viewmodel.SnackBarController
import com.sujith.kotlin.koinnew.viewmodel.SnackBarEvent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
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
                val scope = rememberCoroutineScope()
                val snackbarHostState = remember { SnackbarHostState() }
                val currentEvent = remember { mutableStateOf<SnackBarEvent?>(null) }

                ObserveAsEvents(flow = SnackBarController.events) { events ->
                    currentEvent.value = events  // Store the latest event

                    scope.launch {
                        snackbarHostState.currentSnackbarData?.dismiss()
                        val result = snackbarHostState.showSnackbar(
                            message = events.message,
                            actionLabel = events.action?.label,
                            duration = events.duration,
                            withDismissAction = events.withDismissAction
                        )
                        when (result) {
                            SnackbarResult.ActionPerformed -> events.action?.action?.invoke()
                            SnackbarResult.Dismissed -> events.action?.dismissAction?.invoke()
                        }
                    }
                }
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    snackbarHost = {
                        SnackbarHost(hostState = snackbarHostState) { data ->
                            /*AnimatedVisibility(  // Animation for Snackbar entry
                                visible = snackbarHostState.currentSnackbarData != null,
                                enter = fadeIn(animationSpec = spring(stiffness = Spring.DampingRatioHighBouncy)),  // Slide up animation
                                exit = slideOutVertically(targetOffsetY = { it })    // Slide down when dismissed
                            ) {

                            }*/
                            Snackbar(
                                modifier = Modifier
                                    .padding(16.dp)
                                    .background(
                                        currentEvent.value?.backgroundColor ?: MaterialTheme.colorScheme.primary,
                                        shape = RoundedCornerShape(16.dp)
                                    ),
                                action = {
                                    data.visuals.actionLabel?.let { actionLabel ->
                                        TextButton(
                                            onClick = { data.performAction() },
                                            colors = ButtonDefaults.textButtonColors( // Custom background color
                                                containerColor = Color.Yellow, // Change action button bg color
                                                contentColor = Color.Black      // Change text color
                                            )
                                        ) {
                                            Text(
                                                text = actionLabel,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                    }
                                },

                                dismissAction = if (data.visuals.withDismissAction) {
                                    {
                                        TextButton(
                                            onClick = { data.dismiss() },
                                            colors = ButtonDefaults.textButtonColors( // Custom dismiss bg color
                                                containerColor = Color.Red,  // Change dismiss button bg color
                                                contentColor = Color.White  // Change text color
                                            )
                                        ) {
                                            Text(text = "Dismiss", fontWeight = FontWeight.Bold)
                                        }
                                    }
                                } else null,
                                shape = RoundedCornerShape(16.dp),
                                containerColor = MaterialTheme.colorScheme.secondary,
                                contentColor = Color.White,
                                actionContentColor = Color.Red,
                                dismissActionContentColor = Color.Green,
                            ) {
                                Column {
                                    Text(
                                        text = data.visuals.message,
                                        style = MaterialTheme.typography.bodyLarge
                                    )
                                    Spacer(modifier = Modifier.height(4.dp)) // Add spacing

                                    // Custom Dismiss Button (Optional)
                                    /* TextButton(
                                     onClick = { data.dismiss() } // Explicitly dismiss snackbar
                                 ) {
                                     Text(
                                         text = "Dismiss",
                                         color = Color.Green,
                                         fontWeight = FontWeight.Bold
                                     )
                                 }*/
                                }
                            }
                        }
                    },
                ) { innerPadding ->
                    val navController = rememberNavController()
                    NavHost(navController = navController, startDestination = Screens.AddTweet) {
                        composable<Screens.AddTweet>() {
                            val tweets by viewModel.tweetsFlow./*collectAsState()*/collectAsStateWithLifecycle()
                            AddTweet(
                                this@MainActivity,
                                innerPadding,
                                viewModel::addTweet,
                                tweets,
                                viewModel.tweetText,
                                viewModel.category,
                                viewModel,
                                snackbarHostState,
                                scope,
                                navController = navController
                            )
                        }
                        composable<Screens.ScreenB>() {
                            ScreenB()
                        }
                    }
                }
            }
        }
    }
}


sealed class Screens {
    @Serializable
    data object AddTweet : Screens()

    @Serializable
    data object ScreenB : Screens()
}


@Composable
fun AddTweet(
    context: Context,
    paddingValues: PaddingValues,
    addOnclick: (Tweet) -> Unit,
    tweets: List<Tweet>,
    tweet: StateFlow<String>,
    category: StateFlow<String>,
    viewModel: MainViewModel,
    snackbarHostState: SnackbarHostState,
    scope: CoroutineScope = rememberCoroutineScope(),
    navController: NavHostController,
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
                else {
                    scope.launch {
                        SnackBarController.sendEvent(
                            event = SnackBarEvent(
                                message = "Deleted the data",
                                withDismissAction = false,
                                backgroundColor = Color.White,
                                action = SnackBarAction(
                                    label = "Undo",
                                    action = {
                                        println("Action performed")
                                        SnackBarController.sendEvent(
                                            event = SnackBarEvent(
                                                message = "Undo deleted data",
                                                backgroundColor = Color.Red
                                            )
                                        )
                                    },
                                    dismissAction = {
                                        println("Dismissed")
//                                        viewModel.resetFields()
                                        SnackBarController.sendEvent(
                                            event = SnackBarEvent(
                                                message = "Successfully deleted",
                                                backgroundColor = Color.Green,
                                            )
                                        )
                                    }
                                )
                            )
                        )
                    }
                }
            }
        ) {
            Text(text = "Add")
        }
        Button(onClick = {
//            navController.navigate(Screens.ScreenB)
//            Toast.makeText(context, "nav b", Toast.LENGTH_SHORT).show()
            scope.launch {
                SnackBarController.sendEvent(
                    event = SnackBarEvent(
                        message = "Custom Background Color!",
                        backgroundColor = Color(0xFF00FF00) // Explicit green color
                    )
                )
            }


        }) {
            Text("nav b")
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
fun ScreenB() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(text = "Screen B")
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
    val context = Application()
    AddTweet(
        context = context,
        paddingValues = PaddingValues(10.dp),
        addOnclick = {},
        tweets = listOf(Tweet("test", "tweet")),
        tweet = dummyTweetText,
        category = dummyCategory,
        viewModel = fakeViewModel,
        snackbarHostState = SnackbarHostState(),
        navController = rememberNavController()

    )
}

