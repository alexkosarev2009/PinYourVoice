package com.example.shareyourvoicemapbox.ui.screens.report

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.shareyourvoicemapbox.ui.theme.AppTheme

@Composable
fun ReportScreen(
    modifier: Modifier = Modifier,
    navHostController: NavHostController,
    viewModel: ReportViewModel = viewModel<ReportViewModel>()
) {
    val state by viewModel.state.collectAsState()

    val options = viewModel.options

    ReportContent(
        state,
        options = options,
        onOptionSelect = { text ->
            viewModel.selectOption(text)
        },

    )
}

@Composable
fun ReportContent(
    state: ReportState,
    onOptionSelect: (String) -> Unit,
    options: List<String>
) {
    Column(Modifier.selectableGroup()) {
        options.forEach { text ->
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


@Composable
@Preview
fun ReportScreenPreview(
    modifier: Modifier = Modifier,
) {
    AppTheme() {
        ReportContent(
            onOptionSelect = {},
            state = ReportState(),
            options = listOf(
                "Spam",
                "Frauds and scams",
                "Inappropriate content",
                "Misinformation",
                "Violence or graphic content",
                "Hate and harassment"
            )
        )
    }
}