package com.example.shareyourvoicemapbox.ui.screens.editProfile

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import coil3.compose.AsyncImage
import com.example.shareyourvoicemapbox.R

const val MAX_NAME_LEN = 30
const val MAX_BIO_LEN = 50

@Composable
fun EditProfileScreen(
    modifier: Modifier = Modifier,
    navHostController: NavHostController,
    viewModel: EditProfileViewModel = hiltViewModel<EditProfileViewModel>(),
) {
    val state by viewModel.state.collectAsState()

    val pickMedia =
        rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
            // Callback is invoked after the user selects a media item or closes the
            // photo picker.
            if (uri != null) {
                viewModel.pickNewAvatar(uri)
            } else {

            }
        }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp, 0.dp),
    ) {
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
                },
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBackIosNew,
                    contentDescription = stringResource(R.string.go_back),
                )
            }
            Spacer(Modifier.width(8.dp))
            Text(
                text = stringResource(R.string.edit_profile),
                fontWeight = FontWeight.Bold,
                fontSize = 24.sp,
            )
        }
        EditProfileContent(
            state = state,
            onChangeAvatarClick = {

            },
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,

            ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "Name",
                    fontWeight = FontWeight.Bold,
                )
                Spacer(Modifier.width(8.dp))
                TextField(
                    modifier = Modifier
                        .heightIn(min = 52.dp)
                        .widthIn(max = 250.dp),
                    singleLine = false,
                    value = state.nameInput,
                    onValueChange = {
                        viewModel.onNameChange(it)
                    },
                    colors = TextFieldDefaults.colors(
                        unfocusedContainerColor = MaterialTheme.colorScheme.background,
                        focusedContainerColor = MaterialTheme.colorScheme.background,
                        unfocusedIndicatorColor = MaterialTheme.colorScheme.background,
                        focusedIndicatorColor = MaterialTheme.colorScheme.background,
                    ),
                    textStyle = TextStyle(
                        fontSize = 16.sp,
                    ),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Text,
                        imeAction = ImeAction.Done,
                    ),
                )
            }
            Text(
                text = "${state.nameInput.length}/${MAX_NAME_LEN}",
                fontWeight = FontWeight.Light,
            )
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,

                ) {
                Text("Bio",
                    fontWeight = FontWeight.Bold)
                Spacer(Modifier.width(8.dp))
                TextField(
                    modifier = Modifier
                        .heightIn(min = 52.dp)
                        .widthIn(max = 250.dp),
                    singleLine = false,
                    value = state.bioInput,
                    onValueChange = {
                        viewModel.changeBio(it)
                    },
                    colors = TextFieldDefaults.colors(
                        unfocusedContainerColor = MaterialTheme.colorScheme.background,
                        focusedContainerColor = MaterialTheme.colorScheme.background,
                        unfocusedIndicatorColor = MaterialTheme.colorScheme.background,
                        focusedIndicatorColor = MaterialTheme.colorScheme.background,
                    ),
                    textStyle = TextStyle(
                        fontSize = 16.sp,
                    ),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Text,
                        imeAction = ImeAction.Done,
                    ),
                )
            }
            Text(
                text = "${state.bioInput.length}/${MAX_BIO_LEN}",
                fontWeight = FontWeight.Light,
            )
        }
    }
}


@Composable
fun EditProfileContent(
    state: EditProfileState,
    onChangeAvatarClick: () -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box(
            contentAlignment = Alignment.BottomEnd,
        ) {
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surfaceContainerHighest),
                contentAlignment = Alignment.Center,
            ) {

                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = stringResource(R.string.default_avatar),
                    modifier = Modifier.size(68.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                if (state.avatarUrl != "") {
                    AsyncImage(
                        modifier = Modifier
                            .size(100.dp)
                            .clip(CircleShape),
                        model = state.avatarUrl,
                        contentDescription = stringResource(R.string.user_avatar),
                        contentScale = ContentScale.Crop,
                    )
                }
            }
        }
        Spacer(Modifier.height(4.dp))
        Text(
            stringResource(R.string.change_avatar),
            color = MaterialTheme.colorScheme.primary,
        )
    }
}