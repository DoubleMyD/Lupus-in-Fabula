package com.example.lupusinfabulav1.ui.player

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.dp
import com.example.lupusinfabulav1.model.PlayerDetails
import com.example.lupusinfabulav1.ui.LupusInFabulaScreen
import com.example.lupusinfabulav1.ui.commonui.LupusInFabulaAppBar
import com.example.lupusinfabulav1.ui.playersList.PlayersListContent


@Composable
fun PlayersScreen(
    navigateUp: () -> Unit,
    playersDetails: List<PlayerDetails>,
    onFloatingButtonClick: () -> Unit,
    modifier: Modifier = Modifier,
    onPlayerClick: (PlayerDetails) -> Unit = {},
    onPlayerLongClick: (PlayerDetails) -> Unit = {}
) {
//    val cardBackgroundColor = { playerDetails: PlayerDetails ->
//        if (uiState.selectedPlayers.contains(playerDetails)) Color.LightGray
//        else Color.White
//    }

    Scaffold(
        topBar = {
            LupusInFabulaAppBar(
                title = LupusInFabulaScreen.VILLAGE.title,
                canNavigateBack = true,
                navigateUp = navigateUp
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    onFloatingButtonClick()
                    navigateUp()
                },
                shape = MaterialTheme.shapes.medium,
                modifier = Modifier
                    .padding(
                        end = WindowInsets.safeDrawing.asPaddingValues()
                            .calculateEndPadding(LocalLayoutDirection.current)
                    )
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = null
                )
            }
        },
        modifier = modifier
    ) { innerPadding ->

        PlayersListContent(
            playersDetails = playersDetails,
            //cardBackgroundColor = cardBackgroundColor,
            onPlayerClick = onPlayerClick,
            onPlayerLongClick = onPlayerLongClick,
            modifier = Modifier,
            cardModifier = Modifier
                .padding(innerPadding)
                .height(224.dp)
                .fillMaxWidth()
            //.padding(dimensionResource(id = R.dimen.padding_medium))
        )
    }
}