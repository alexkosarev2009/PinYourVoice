package com.example.shareyourvoicemapbox.ui.screens.edit

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController

@Composable
fun EditScreen(
    modifier: Modifier = Modifier,
    navHostController: NavHostController,
    viewModel: EditViewModel = hiltViewModel<EditViewModel>()
) {
    val audioPath = navHostController.previousBackStackEntry?.savedStateHandle?.get<String>("audioPath")

    Column(
        modifier.fillMaxSize().systemBarsPadding(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Button(
            onClick = {
                navHostController.popBackStack()
            }
        ) {
            Text("Back")
        }
        Button(
            onClick = {

            }
        ) {
            Text("PLAY")
        }
        Text(audioPath ?: "No")
    }
}