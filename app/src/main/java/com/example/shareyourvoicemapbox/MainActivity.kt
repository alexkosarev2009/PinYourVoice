package com.example.shareyourvoicemapbox

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import com.example.shareyourvoicemapbox.ui.navigation.NavigationBarExample
import com.mapbox.geojson.Point
import com.mapbox.maps.extension.compose.animation.viewport.rememberMapViewportState


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            NavigationBarExample(Modifier)
        }
    }


}