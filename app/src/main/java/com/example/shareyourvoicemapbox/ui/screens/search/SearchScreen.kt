package com.example.shareyourvoicemapbox.ui.screens.search

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.example.shareyourvoicemapbox.R
import com.example.shareyourvoicemapbox.domain.amplituda.ParseAmplitudesUseCase
import com.example.shareyourvoicemapbox.ui.components.MarkerCard
import com.example.shareyourvoicemapbox.ui.navigation.SecondaryRoute

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    modifier: Modifier = Modifier,
    navHostController: NavHostController,
    viewModel: SearchViewModel = hiltViewModel<SearchViewModel>()
) {
    val state by viewModel.state.collectAsState()




    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .statusBarsPadding()
            .systemBarsPadding()
    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            IconButton(
                onClick = {
                    navHostController.popBackStack()
                }
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBackIosNew,
                    contentDescription = stringResource(R.string.go_back)
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            SearchBar(
                modifier = Modifier.weight(1f),
                expanded = false,
                onExpandedChange = {},
                inputField = {

                    SearchBarDefaults.InputField(
                        query = state.query,
                        onQueryChange = { query ->
                            viewModel.changeQuery(query)
                        },
                        onSearch = {
                            viewModel.search()
                        },
                        expanded = false,
                        onExpandedChange = {},
                        placeholder = {
                            Text(stringResource(R.string.search_markers))
                        },
                        trailingIcon = {
                            IconButton(
                                onClick = {
                                    viewModel.search()
                                }
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Search,
                                    contentDescription = null
                                )
                            }
                        },
                    )
                },
            ) {}
        }

        if (state.query.isEmpty()) {

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Text(
                    text = stringResource(R.string.find_anything),
                    style = MaterialTheme.typography.titleMedium,
                )
            }

        } else {

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(
                    horizontal = 24.dp,
                    vertical = 8.dp
                ),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {

                items(state.markers) { marker ->

                    MarkerCard(
                        modifier = Modifier
                            .padding(
                                0.dp,
                            ),
                        id = marker.id,
                        title = marker.title,
                        location = marker.location,
                        username = marker.authorUsername,
                        avatarUrl = marker.authorAvatarUrl,
                        imageUrl = marker.imageUrl ?: "",
                        onPlayClick = {},
                        onOpenMap = {},
                        amplitudes = ParseAmplitudesUseCase().invoke(marker.amplitudes),
                        waveformProgress = 0f,
                        onWaveformProgressChange = { },
                        name = marker.authorName,
                        createdAt = marker.createdAt,
                        isPLaying = false,
                        onNameClick = {
                            navHostController.navigate("${SecondaryRoute.PERSON.route}?username=${marker.authorUsername}")
                        },
                        onReportClick = {

                        },
                    )
                }
            }
        }
    }
}