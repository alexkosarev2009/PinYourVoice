package com.example.shareyourvoicemapbox.ui.screens.invitation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.example.shareyourvoicemapbox.R
import com.example.shareyourvoicemapbox.ui.components.InvitationCard
import com.example.shareyourvoicemapbox.ui.navigation.SecondaryRoute

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InvitationScreen(
    modifier: Modifier = Modifier,
    navHostController: NavHostController,
    viewModel: InvitationViewModel = hiltViewModel<InvitationViewModel>()
) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.getInvitations()
    }

    LazyColumn(
        modifier = Modifier
            .statusBarsPadding()
            .padding(24.dp, 0.dp)
    ) {
        stickyHeader {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
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
                    text = stringResource(R.string.friend_invitations),
                    fontWeight = FontWeight.Bold,
                    fontSize = 24.sp,
                )
            }
        }
        items(state.invitations,
            key = { it.id }
        ) { invitation ->
            InvitationCard(
                invitation = invitation,
                modifier = Modifier.animateItem(),
                onNameClick = {
                    navHostController.navigate("${SecondaryRoute.PERSON.route}?username=${invitation.senderUsername}")
                },
                onDeclineClick = {
                    viewModel.declineInvitation(invitation.id)
                },
                onAcceptClick = {
                    viewModel.acceptInvitation(invitation.id)
                }
            )
            Spacer(Modifier.height(20.dp))
        }
    }
}