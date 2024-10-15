package com.example.lupusinfabulav1.ui.playersList

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.lupusinfabulav1.R
import com.example.lupusinfabulav1.data.database.entity.PlayersList
import com.example.lupusinfabulav1.model.PlayerDetails
import com.example.lupusinfabulav1.model.PlayerImageSource
import com.example.lupusinfabulav1.model.getPainter
import com.example.lupusinfabulav1.ui.LupusInFabulaScreen
import com.example.lupusinfabulav1.ui.commonui.LupusInFabulaAppBar
import com.example.lupusinfabulav1.ui.util.InputDialog

@Composable
fun PlayersListsScreen(
    navigateUp: () -> Unit,
    onListClick: (String) -> Unit,
    uiState: PlayersListsUiState,
    onCreateNewList: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    var isDialogOpen by remember { mutableStateOf(false) }

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
                onClick = { isDialogOpen = true },
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

        Lists(
            onListClick = onListClick,
            playersLists = uiState.playersLists,
            modifier = Modifier.padding(innerPadding)
        )

        if (isDialogOpen) {
            InputDialog(
                title = "Create New List",
                placeholder = "Enter list name",
                onDismiss = { isDialogOpen = false },
                onConfirm = { listName ->
                    onCreateNewList(listName)
                    isDialogOpen = false
                }
            )
        }
    }
}

@Composable
fun Lists(
    playersLists: Map<PlayersList, List<PlayerDetails>>,
    modifier: Modifier = Modifier,
    onListClick: (String) -> Unit = {},
) {
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
    ) {
        if (playersLists.isEmpty()) {
            Text(
                text = stringResource(id = R.string.no_lists),
                modifier = Modifier.padding(dimensionResource(id = R.dimen.padding_medium))
            )
        } else {
            for ((list, playersDetails) in playersLists) {
                ListPoster(
                    onListClick = { onListClick(list.id.toString()) },
                    imageSources = playersDetails.map { it.imageSource },
                    listName = list.name
                )
                HorizontalDivider(
                    thickness = dimensionResource(id = R.dimen.border_width_small)
                )
            }
        }
    }
}


@Composable
fun ListPoster(
    onListClick: () -> Unit,
    imageSources: List<PlayerImageSource>,
    listName: String,
    modifier: Modifier = Modifier,
    posterHeight: Int = 160 + 32,
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .height(posterHeight.dp)
            .fillMaxWidth()
            .clickable { onListClick() }
    ) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(dimensionResource(id = R.dimen.padding_small))
        ) {
            Text(
                text = listName,
                //modifier = Modifier.align(Alignment.Start)
            )
            Text(
                text = stringResource(id = R.string.player_list_size, imageSources.size),
                //modifier = Modifier.align(Alignment.End)
            )
        }
        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .padding(dimensionResource(id = R.dimen.padding_small))
                .horizontalScroll(state = scrollState)
        ) {
            for (imageSource in imageSources) {
                Image(
                    painter = imageSource.getPainter(),
                    contentDescription = null,
                    modifier = Modifier
                        .size(dimensionResource(id = R.dimen.image_big))
                )
            }
        }
    }
}



@Preview(showBackground = true)
@Composable
fun PlayersListScreenPreview() {
    PlayersListsScreen(
        navigateUp = {},
        uiState = PlayersListsUiState(),
        onListClick = {},
        modifier = Modifier.fillMaxWidth(),
        onCreateNewList = {}
    )
}
//
//@Preview(showBackground = true)
//@Composable
//fun ListsPreview() {
//    Lists(
//        playersLists = FakePlayersRepository.playersLists,
//        modifier = Modifier.fillMaxWidth()
//    )
//}