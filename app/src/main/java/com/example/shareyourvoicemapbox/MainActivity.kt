package com.example.shareyourvoicemapbox

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.shareyourvoicemapbox.ui.navigation.AppNavigationBar
import com.example.shareyourvoicemapbox.ui.theme.AppTheme


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AppTheme {
                AppNavigationBar()
            }
        }
    }
}