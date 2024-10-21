package com.example.lupusinfabulav1.ui.playersList

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.example.lupusinfabulav1.R
import com.example.lupusinfabulav1.data.database.entity.PlayersList
import com.example.lupusinfabulav1.data.fake.FakePlayersRepository
import com.example.lupusinfabulav1.ui.commonui.LupusInFabulaAppBar


@Composable
fun PlayersListInfoScreen(
    //navigateBack: () -> Unit,
    navigateUp: () -> Unit,
    uiState: InfoPlayersListUiState,
    modifier: Modifier = Modifier,
    onFloatingButtonClick: () -> Unit = {},
) {

    Scaffold(
        topBar = {
            LupusInFabulaAppBar(
                title = "Edit PlayerS List",
                canNavigateBack = true,
                navigateUp = navigateUp
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { onFloatingButtonClick() },
                shape = MaterialTheme.shapes.medium,
                modifier = Modifier
                    .padding(
                        end = WindowInsets.safeDrawing.asPaddingValues()
                            .calculateEndPadding(LocalLayoutDirection.current)
                    )
            ) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = null
                )
            }
        },
        modifier = modifier
    ) { innerPadding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            if (uiState.playersDetails.isNotEmpty()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(dimensionResource(id = R.dimen.padding_medium))
                ) {
                    if (uiState.playersList != null) {
                        Text(text = uiState.playersList.name)
                        Spacer(modifier = Modifier.weight(1f))
                        Text(
                            text = stringResource(
                                id = R.string.player_list_size,
                                uiState.playersDetails.size
                            )
                        )
                    } else {
                        Text(text = "Errore")
                    }
                }
                PlayersListContent(
                    playersDetails = uiState.playersDetails,
                    modifier = Modifier
                        .fillMaxSize()
                        //.padding(innerPadding)
                )
            } else {
                Text(
                    text = stringResource(id = R.string.list_have_no_players),
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(dimensionResource(id = R.dimen.padding_medium))
                )
            }
        }
    }
}

@Preview
@Composable
fun EditPlayersListScreenPreview() {
    PlayersListInfoScreen(
        //navigateBack = { },
        navigateUp = { },
        uiState = InfoPlayersListUiState(
            listId = 1,
            playersList = PlayersList(
                id = 1,
                name = "Lista giocatori",
                playersId = listOf(1, 2, 3)
            ),
            playersDetails = FakePlayersRepository.playerDetails
        )
    )
}