package com.example.shareyourvoicemapbox.ui.screens.profile

import android.content.Context
import android.location.Geocoder
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.PeopleAlt
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Place
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import coil3.compose.AsyncImage
import com.example.shareyourvoicemapbox.ui.components.MarkerCard
import com.example.shareyourvoicemapbox.ui.navigation.SecondaryRoute
import com.example.shareyourvoicemapbox.ui.screens.edit.PlayerState
import com.example.shareyourvoicemapbox.ui.theme.AppTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.Locale

@Composable
fun ProfileScreen(
    modifier: Modifier,
    navHostController: NavHostController,
    viewModel: ProfileViewModel = hiltViewModel<ProfileViewModel>()
    ) {

    val state by viewModel.uiState.collectAsState()

    val context = LocalContext.current

    Scaffold(
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.background)
                    .statusBarsPadding()
                    .padding(24.dp, 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "Profile",
                    fontWeight = FontWeight.Bold,
                    fontSize = 24.sp,
                )
                Icon(
                    imageVector = Icons.Default.Menu,
                    contentDescription = "Menu",
                    modifier = Modifier.clickable(
                        onClick = {
                            viewModel.onMenuClick()
                            navHostController.navigate(SecondaryRoute.AUTH.route) {
                                popUpTo(0)
                            }
                        }
                    )
                )
            }
        },
    ) { innerPadding ->
        LazyColumn {
            item {
                ProfileContent(
                    state = state,
                    modifier = Modifier.padding(innerPadding)
                )
            }
            items(state.markers) { marker ->
                MarkerCard(
                    modifier = Modifier.padding(20.dp, 0.dp),
                    title = marker.title,
                    location = marker.location,
                    username = marker.authorUsername,
                    avatarUrl = marker.authorAvatarUrl,
                    imageUrl = marker.imageUrl ?: "",
                    onPlayClick = {  },
                    onOpenMap = {  },
                    amplitudes = emptyList(),
                    waveformProgress = 0f,
                    onWaveformProgressChange = {},
                    playerState = PlayerState(),
                    name = marker.authorName
                )
                Spacer(Modifier.height(20.dp))
            }
        }
    }
}

@Composable
fun ProfileContent(
    modifier: Modifier = Modifier,
    state: ProfileState
    ) {
    Column(
        modifier = modifier
            .fillMaxSize()
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surfaceContainerHighest),
                contentAlignment = Alignment.Center
            ) {

                Icon(imageVector = Icons.Default.Person,
                    contentDescription = "default avatar",
                    modifier = Modifier.size(68.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant)
                if (state.avatarUrl != "") {
                    AsyncImage(
                        modifier = Modifier
                            .size(100.dp)
                            .clip(CircleShape),
                        model = state.avatarUrl,
                        contentDescription = "user avatar",
                        contentScale = ContentScale.Crop
                    )
                }
            }
            Spacer(Modifier.height(8.dp))
            Text(state.fullName, fontSize = 20.sp, fontWeight = FontWeight.Bold)
            Text("@${state.userName}", fontSize = 16.sp)
        }
        Spacer(Modifier.height(16.dp))
        Row(
            modifier = Modifier
                .padding(24.dp, 0.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            ElevatedCard(
                elevation = CardDefaults.elevatedCardElevation(8.dp),
                modifier = Modifier.height(80.dp).weight(1f),
                colors = CardDefaults.elevatedCardColors(
                    contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(4.dp)
                ) {
                    Icon(
                        modifier = Modifier.align(Alignment.TopEnd),
                        imageVector = Icons.Default.Place,
                        contentDescription = "marker",
                        tint = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Column(
                        modifier = Modifier
                            .align(Alignment.CenterStart)
                            .padding(8.dp)
                    ) {
                        Text(state.markers.size.toString(), fontSize = 24.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onPrimaryContainer)
                        Text("Markers", fontSize = 12.sp, color = MaterialTheme.colorScheme.onBackground)
                    }
                }
            }
            Spacer(Modifier.width(24.dp))
            ElevatedCard(
                elevation = CardDefaults.elevatedCardElevation(8.dp),
                modifier = Modifier.height(80.dp).weight(1f),
                colors = CardDefaults.elevatedCardColors(
                    contentColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    containerColor = MaterialTheme.colorScheme.surfaceContainerHigh
                )
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(4.dp)
                ) {
                    Icon(
                        modifier = Modifier.align(Alignment.TopEnd),
                        imageVector = Icons.Default.PeopleAlt,
                        contentDescription = "marker",
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                    Column(
                        modifier = Modifier
                            .align(Alignment.CenterStart)
                            .padding(8.dp)
                    ) {
                        Text("8", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                        Text("Friends", fontSize = 12.sp, color = MaterialTheme.colorScheme.onBackground)
                    }
                }
            }
        }
        Spacer(Modifier.height(16.dp))
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp, 0.dp)
        ) {
            Text("Bio", fontWeight = FontWeight.Bold)
            Text(state.bio, fontStyle = FontStyle.Italic,
                fontSize = 16.sp)
            Spacer(Modifier.height(16.dp))
            Text(
                "Markers",
                fontWeight = FontWeight.Bold,
                fontSize = 24.sp,
            )
        }
    }
}


@Composable
@Preview(showSystemUi = true)
fun ProfileScreenPreview(modifier: Modifier = Modifier) {
    AppTheme(
        darkTheme = false
    ) {
        ProfileContent(
            state = ProfileState()
        )
    }
}