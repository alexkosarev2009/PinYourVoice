package com.example.shareyourvoicemapbox.ui.screens.profile

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.example.shareyourvoicemapbox.ui.navigation.SecondaryRoute

@Composable
fun ProfileScreen(
    modifier: Modifier = Modifier,
    navHostController: NavHostController,
    viewModel: ProfileViewModel = hiltViewModel<ProfileViewModel>()
    ) {
    Scaffold(
        modifier = modifier
    ) { innerPadding ->
        Column(
            modifier = modifier.padding(innerPadding)
        ) {
            Text("PROFILE")
            IconButton(
                onClick = {
                    viewModel.onLogOutClick()
                    navHostController.navigate(SecondaryRoute.AUTH.route) {
                        popUpTo(0)
                    }
                }
            ) {
                Icon(imageVector = Icons.AutoMirrored.Filled.Logout,
                    contentDescription = "Log out")
            }
        }
    }
}