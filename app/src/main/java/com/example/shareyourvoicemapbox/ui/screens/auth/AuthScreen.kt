package com.example.shareyourvoicemapbox.ui.screens.auth

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AlternateEmail
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SecondaryTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.example.shareyourvoicemapbox.R
import com.example.shareyourvoicemapbox.ui.navigation.Route
import com.example.shareyourvoicemapbox.ui.navigation.SecondaryRoute
import com.example.shareyourvoicemapbox.ui.theme.AppTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AuthScreen(
    navHostController: NavHostController,
    viewModel: AuthViewModel = hiltViewModel<AuthViewModel>()
) {
    val state by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.actionFlow.collect { action ->
            when (action) {
                is AuthAction.OpenScreen -> navHostController.navigate(Route.MAP.route) {
                    popUpTo(SecondaryRoute.AUTH.route) {
                        inclusive = true
                    }
                }
            }
        }
    }

    AuthContent(
        state = state,
        onLoginChange = { login ->
            viewModel.onChangeInput(login, state.password)
        },
        onPasswordChange = { password ->
            viewModel.onChangeInput(state.username, password)
        },
        onLoginClick = {
            viewModel.onLoginClick(state.username, state.password)
        },
        onSignInClick = {
            viewModel.onSignInClick()
        },
        onSignUpClick = { viewModel.onSignUpClick() },
        onEmailChange = { email ->
            viewModel.onChangeRegisterInput(email, state.registerPassword)
        },
        onRegisterPasswordChange = { password ->
            viewModel.onChangeRegisterInput(state.email, password)
        },
        onGoBackClick = {
            viewModel.goBack()
        },
        onRegisterNameChange = { name ->
            viewModel.onChangeNameInput(state.registerUsername, name)
        },
        onRegisterUsernameChange = { username ->
            viewModel.onChangeNameInput(username, state.registerName)
        },
        onRegisterClick = {
            viewModel.onRegisterClick()
        },
        onFinishRegisterClick = {
            viewModel.finishRegister()
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AuthContent(
    modifier: Modifier = Modifier,
    state: AuthState.Content,
    onLoginChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onLoginClick: () -> Unit,
    onSignInClick: () -> Unit,
    onSignUpClick: () -> Unit,
    onEmailChange: (String) -> Unit,
    onRegisterPasswordChange: (String) -> Unit,
    onRegisterUsernameChange: (String) -> Unit,
    onRegisterNameChange: (String) -> Unit,
    onGoBackClick: () -> Unit,
    onRegisterClick: () -> Unit,
    onFinishRegisterClick: () -> Unit,
) {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp, 0.dp)
            .background(
                MaterialTheme.colorScheme.background
            ),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Spacer(Modifier.height(240.dp))
        SecondaryTabRow(
            selectedTabIndex = if (state.isSignInSelected) 0 else 1,
            modifier = Modifier.fillMaxWidth(),

        ) {
            Tab(
                selected = state.isSignInSelected,
                onClick = onSignInClick,
                modifier = Modifier.selectable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    selected = state.isSignInSelected,
                    onClick = onSignInClick
                )
            ) {
                Text(
                    stringResource(R.string.sign_in),
                    color = if (state.isSignInSelected) MaterialTheme.colorScheme.primary
                    else MaterialTheme.colorScheme.onBackground,
                )
                Spacer(Modifier.height(8.dp))
            }
            Tab(
                selected = state.isSignUpSelected,
                onClick = onSignUpClick
            ) {
                Text(
                    stringResource(R.string.sign_up),
                    color = if (state.isSignUpSelected) MaterialTheme.colorScheme.primary
                    else MaterialTheme.colorScheme.onBackground,
                )
                Spacer(Modifier.height(8.dp))
            }
        }
        Spacer(Modifier.height(32.dp))

        Box {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                AnimatedVisibility(
                    visible = state.isSignInSelected,
                    enter = fadeIn(
                        animationSpec = tween(500)
                    ),
                    exit = fadeOut(
                        animationSpec = tween(500)
                    )
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        OutlinedTextField(
                            value = state.username,
                            onValueChange = onLoginChange,
                            label = {
                                Text(stringResource(R.string.login))
                            },
                            shape = RoundedCornerShape(16.dp),
                            singleLine = true,
                            isError = state.error.isNotEmpty(),
                        )

                        Spacer(Modifier.height(8.dp))

                        OutlinedTextField(
                            value = state.password,
                            onValueChange = onPasswordChange,
                            label = {
                                Text(stringResource(R.string.password))
                            },
                            shape = RoundedCornerShape(16.dp),
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Password
                            ),
                            visualTransformation = PasswordVisualTransformation(),
                            isError = state.error.isNotEmpty(),
                        )
                        Spacer(Modifier.height(4.dp))
                        Text(
                            text = state.error,
                            color = MaterialTheme.colorScheme.error
                        )
                        Spacer(Modifier.height(8.dp))
                        Button(
                            modifier = Modifier,
                            onClick = onLoginClick,
                            enabled = state.isEnableLogin
                        ) {
                            Text(stringResource(R.string.sign_in))
                        }
                    }
                }
            }
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                AnimatedVisibility(
                    visible = state.isSignUpSelected
                            && state.registerStep == 1,
                    enter = fadeIn(
                        animationSpec = tween(500),
                    ),
                    exit = fadeOut(
                        animationSpec = tween(500),
                    ),
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        OutlinedTextField(
                            value = state.email,
                            onValueChange = onEmailChange,
                            label = {
                                Text(stringResource(R.string.email))
                            },
                            shape = RoundedCornerShape(16.dp),
                            singleLine = true,
                            isError = state.error.isNotEmpty(),
                        )
                        Spacer(Modifier.height(8.dp))

                        OutlinedTextField(
                            value = state.registerPassword,
                            onValueChange = onRegisterPasswordChange,
                            label = {
                                Text(stringResource(R.string.password))
                            },
                            shape = RoundedCornerShape(16.dp),
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Password,
                            ),
                            visualTransformation = PasswordVisualTransformation(),
                            isError = state.error.isNotEmpty(),
                        )
                        Spacer(Modifier.height(4.dp))
                        Text(
                            text = state.error,
                            color = MaterialTheme.colorScheme.error,
                        )
                        Spacer(Modifier.height(8.dp))
                        Button(
                            onClick = onRegisterClick,
                            enabled = state.isEnableRegister,
                        ) {
                            Text(stringResource(R.string.sign_up))
                        }
                    }
                }
            }
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                AnimatedVisibility(
                    visible = state.isSignUpSelected
                            && state.registerStep == 2,
                    enter = fadeIn(
                        animationSpec = tween(500),
                    ),
                    exit = fadeOut(
                        animationSpec = tween(500),
                    ),
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        OutlinedTextField(
                            value = state.registerUsername,
                            onValueChange = onRegisterUsernameChange,
                            label = {
                                Text(stringResource(R.string.username))
                            },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.AlternateEmail,
                                    contentDescription = stringResource(R.string.username)
                                )
                            },
                            shape = RoundedCornerShape(16.dp),
                            singleLine = true,
                            isError = state.error.isNotEmpty(),
                        )
                        Spacer(Modifier.height(8.dp))

                        OutlinedTextField(
                            value = state.registerName,
                            onValueChange = onRegisterNameChange,
                            label = {
                                Text(stringResource(R.string.full_name))
                            },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.Person,
                                    contentDescription = stringResource(R.string.full_name)
                                )
                            },
                            shape = RoundedCornerShape(16.dp),
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Password,
                            ),
                            isError = state.error.isNotEmpty(),
                        )
                        Spacer(Modifier.height(4.dp))
                        Text(
                            text = state.error,
                            color = MaterialTheme.colorScheme.error,
                        )
                        Spacer(Modifier.height(8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                        ) {
                            Button(
                                onClick = onGoBackClick,
                                modifier = Modifier
                                    .width(200.dp)
                                    .weight(1f),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.surfaceContainerHighest,
                                    contentColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                ),
                            ) {
                                Text(stringResource(R.string.go_back))
                            }
                            Spacer(Modifier.width(8.dp))
                            Button(
                                modifier = Modifier
                                    .width(200.dp)
                                    .weight(1f),
                                onClick = onFinishRegisterClick,
                                enabled = state.isEnableRegister,
                            ) {
                                Text(stringResource(R.string.register))
                            }
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
@Preview
fun AuthScreenPreview(modifier: Modifier = Modifier) {

    val state = AuthState.Content()

    AppTheme {
        AuthContent(
            state = state,
            onLoginChange = { login ->
            },
            onPasswordChange = { password ->
            },
            onLoginClick = {},
            onSignInClick = {},
            onSignUpClick = {},
            onEmailChange = {},
            onRegisterPasswordChange = {},
            onRegisterUsernameChange = {},
            onRegisterNameChange = {},
            onGoBackClick = {},
            onRegisterClick = {},
            onFinishRegisterClick = {}
        )
    }
}