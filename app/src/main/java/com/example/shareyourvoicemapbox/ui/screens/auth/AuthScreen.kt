package com.example.shareyourvoicemapbox.ui.screens.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.shareyourvoicemapbox.ui.navigation.AuthRoute
import com.example.shareyourvoicemapbox.ui.navigation.Route

@Composable
fun AuthScreen(
    navHostController: NavHostController,
    viewModel: AuthViewModel = viewModel<AuthViewModel>()
) {
    val state by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.actionFlow.collect { action ->
            when (action) {
                is AuthAction.OpenScreen -> navHostController.navigate(Route.MAP.route) {
                    popUpTo(AuthRoute.AUTH.route) {
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
            viewModel.onChangeInput(state.login, password)
        },
        onLoginClick = {
            viewModel.onLoginClick(state.login, state.password)
        }
    )
}

@Composable
fun AuthContent(
    modifier: Modifier = Modifier,
    state: AuthState.Content,
    onLoginChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onLoginClick: () -> Unit,
) {

    Column(
        modifier = Modifier.fillMaxSize().background(
            MaterialTheme.colorScheme.background
        ),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        OutlinedTextField(
            value = state.login,
            onValueChange = onLoginChange,
            label = {
                Text("Login")
            },
            shape = RoundedCornerShape(16.dp),
            singleLine = true,
            isError = state.error.isNotEmpty()
        )

        Spacer(Modifier.height(8.dp))

        OutlinedTextField(
            value = state.password,
            onValueChange = onPasswordChange,
            label = {
                Text("Password")
            },
            shape = RoundedCornerShape(16.dp),
            singleLine = true,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Password
            ),
            visualTransformation = PasswordVisualTransformation(),
            isError = state.error.isNotEmpty()
        )
        Spacer(Modifier.height(4.dp))
        Text(
            text = state.error,
            color = MaterialTheme.colorScheme.error
        )
        Spacer(Modifier.height(8.dp))
        Button(
            onClick = onLoginClick,
            enabled = state.isEnableLogin
        ) {
            Text("Login")
        }
    }
}