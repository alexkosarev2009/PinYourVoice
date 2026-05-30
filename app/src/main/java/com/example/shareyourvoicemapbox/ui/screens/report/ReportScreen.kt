package com.example.shareyourvoicemapbox.ui.screens.report

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.example.shareyourvoicemapbox.R

@Composable
fun ReportScreen(
    modifier: Modifier = Modifier,
    navHostController: NavHostController,
    viewModel: ReportViewModel = hiltViewModel<ReportViewModel>()
) {
    val state by viewModel.state.collectAsState()

    val options = viewModel.options

    LaunchedEffect(state.isDone) {
        if (state.isDone) {
            navHostController.popBackStack()
        }
    }

    LazyColumn {
        stickyHeader {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .background(MaterialTheme.colorScheme.background)
                    .padding(0.dp, 8.dp, 0.dp, 16.dp),
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically,
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
                Spacer(Modifier.width(8.dp))
                Text(
                    text = stringResource(R.string.report_content),
                    fontWeight = FontWeight.Bold,
                    fontSize = 24.sp,
                )
            }
        }
        item {
            ReportContent(
                state = state,
                onOptionSelect = { text ->
                    viewModel.selectOption(text)
                },
                options = options
            )
        }
    }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .navigationBarsPadding()
            .padding(16.dp),
        contentAlignment = Alignment.BottomCenter,
    ) {
        Button(
            onClick = {
                viewModel.submitReport()
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            enabled = state.selectedOption != "",
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
            ) {
                if (!state.isLoading) {
                    Icon(
                        imageVector = Icons.Default.Flag,
                        contentDescription = stringResource(R.string.report),
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(text = stringResource(R.string.submit), fontSize = 20.sp)
                } else {
                    CircularProgressIndicator(
                        trackColor = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.size(20.dp),
                    )
                }
            }
        }
    }
}

@Composable
fun ReportContent(
    state: ReportState,
    onOptionSelect: (String) -> Unit,
    options: List<Int>
) {
    Column(Modifier.selectableGroup()) {
        options.forEach { resource ->
            val text = stringResource(resource)
            Row(
                Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .selectable(
                        selected = (state.selectedOption == text),
                        onClick = { onOptionSelect(text) },
                        role = Role.RadioButton
                    )
                    .padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                RadioButton(
                    selected = (text == state.selectedOption),
                    onClick = null
                )
                Text(
                    text = text,
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(start = 16.dp)
                )
            }
        }
    }
}