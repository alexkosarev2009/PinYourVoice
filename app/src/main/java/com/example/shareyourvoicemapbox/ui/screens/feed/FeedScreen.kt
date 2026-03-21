package com.example.shareyourvoicemapbox.ui.screens.feed

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun FeedScreen(modifier: Modifier = Modifier) {
    Column(modifier.statusBarsPadding()) {
        Text("Feed")
    }
}