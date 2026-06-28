package com.example.shareyourvoicemapbox.ui.screens.editProfile

import android.Manifest
import android.content.ContentValues
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.provider.MediaStore
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Photo
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import coil3.compose.AsyncImage
import com.example.shareyourvoicemapbox.R
import com.tanishranjan.cropkit.CropDefaults
import com.tanishranjan.cropkit.CropShape
import com.tanishranjan.cropkit.ImageCropper
import com.tanishranjan.cropkit.rememberCropController

const val MAX_NAME_LEN = 30
const val MAX_BIO_LEN = 50


@Composable
fun ColumnScope.CropComposable(
    state: EditProfileState,
    onCrop: (Bitmap) -> Unit,
    onBack: () -> Unit,
) {
    val bitmap = remember(state.imagePath) {
        BitmapFactory.decodeFile(state.imagePath)
    }
    val cropController = rememberCropController(bitmap = bitmap,
        cropOptions = CropDefaults.cropOptions(
            cropShape = CropShape.CIRCLE
        ))
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.BottomCenter
    ){
        ImageCropper(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp, 24.dp, 24.dp, 0.dp),
            cropController = cropController,
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp, 0.dp, 24.dp, 120.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            FloatingActionButton(
                onClick = onBack,
                shape = CircleShape
            ) {
                Icon(imageVector = Icons.Default.ArrowBackIosNew,
                    contentDescription = "Back",
                    modifier = Modifier.size(28.dp))
            }
            FloatingActionButton(
                onClick = {
                    val croppedBitmap = cropController.crop()
                    onCrop(croppedBitmap)
                },
                contentColor = MaterialTheme.colorScheme.onPrimary,
                containerColor = MaterialTheme.colorScheme.primary,
                shape = CircleShape
            ) {
                Icon(imageVector = Icons.Default.Check,
                    contentDescription = "Crop",
                    modifier = Modifier.size(30.dp))
            }
        }

    }
}

@Composable
fun EditProfileScreen(
    modifier: Modifier = Modifier,
    navHostController: NavHostController,
    viewModel: EditProfileViewModel = hiltViewModel<EditProfileViewModel>(),
) {
    val state by viewModel.state.collectAsState()

    val context = LocalContext.current
    var expanded by remember { mutableStateOf(false) }

    var imageUri by remember { mutableStateOf<Uri?>(null) }

    val takePictureLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.TakePicture(),
    ) { success ->
        if (success) {
            imageUri?.let { nonNullUri ->
                viewModel.pickNewAvatar(nonNullUri)
                viewModel.getFileFromUri(context, nonNullUri)
                viewModel.showCropScreen()
            }
        }
        else {

        }
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission(),
    ) {

    }

    val imagePicker =
        rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
            if (uri != null) {
                viewModel.pickNewAvatar(uri)
                viewModel.getFileFromUri(context, uri)
                viewModel.showCropScreen()
            }
        }

    Box {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(0.dp, 0.dp)
                .navigationBarsPadding(),
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
                expanded = expanded,
                onChangeAvatarClick = {
                    expanded = true
                },
                onDismissMenu = {
                    expanded = false
                },
                onSelectFromGalleryClick = {
                    imagePicker.launch(
                        PickVisualMediaRequest(
                            ActivityResultContracts.PickVisualMedia.ImageOnly,
                        ),
                    )
                    expanded = false
                },
                onTakePictureClick = {
                    val hasGranted = ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA)
                    if (hasGranted == PackageManager.PERMISSION_GRANTED) {
                        val uri = createImageUri(context)
                        imageUri = uri
                        takePictureLauncher.launch(uri)
                    }
                    else {
                        permissionLauncher.launch(Manifest.permission.CAMERA)
                    }
                    expanded = false
                }
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp, 0.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,

                ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        stringResource(R.string.name),
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
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp, 0.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,

                    ) {
                    Text(
                        stringResource(R.string.bio),
                        fontWeight = FontWeight.Bold,
                    )
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
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp, 0.dp),
                contentAlignment = Alignment.BottomCenter,
            ) {
                Button(
                    onClick = { viewModel.updateMe() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    enabled = true,
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                    ) {
                        if (!state.isLoading) {
                            Text(stringResource(R.string.save_changes), fontSize = 20.sp)
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
        AnimatedVisibility(
            visible = state.imagePath != "" && state.showCropScreen
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                CropComposable(
                    state = state,
                    onCrop = { bitmap ->
                        viewModel.getFileFromBitmap(bitmap, context)
                        viewModel.hideCropScreen()
                    },
                    onBack = {
                        viewModel.hideCropScreen()
                    }
                )
            }
        }
    }
}


@Composable
fun EditProfileContent(
    state: EditProfileState,
    onChangeAvatarClick: () -> Unit,
    expanded: Boolean,
    onDismissMenu: () -> Unit,
    onSelectFromGalleryClick: () -> Unit,
    onTakePictureClick: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp, 0.dp),
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
            modifier = Modifier.clickable(
                onClick = onChangeAvatarClick
            ),
            text = stringResource(R.string.change_avatar),
            color = MaterialTheme.colorScheme.primary,
        )
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = onDismissMenu
        ) {
            DropdownMenuItem(
                text = { Text(stringResource(R.string.take_picture)) },
                leadingIcon = { Icon(Icons.Default.CameraAlt, null) },
                onClick = onTakePictureClick
            )
            DropdownMenuItem(
                text = { Text(stringResource(R.string.select_from_gallery)) },
                leadingIcon = { Icon(Icons.Default.Photo, null) },
                onClick = onSelectFromGalleryClick,
            )
        }
    }
}

fun createImageUri(context: Context): Uri {
    val values = ContentValues().apply {
        put(
            MediaStore.Images.Media.DISPLAY_NAME,
            "IMG_${System.currentTimeMillis()}.jpg"
        )
        put(
            MediaStore.Images.Media.MIME_TYPE,
            "image/jpeg"
        )
    }

    return context.contentResolver.insert(
        MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
        values
    )!!
}