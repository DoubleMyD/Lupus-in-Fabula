package com.example.lupusinfabulav1.ui.playersList

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import com.example.lupusinfabulav1.R
import com.example.lupusinfabulav1.data.database.entity.PlayersList
import com.example.lupusinfabulav1.data.fake.FakePlayersRepository
import com.example.lupusinfabulav1.model.PlayerDetails
import com.example.lupusinfabulav1.model.PlayerImageSource
import com.example.lupusinfabulav1.model.getPainter
import com.example.lupusinfabulav1.ui.commonui.LupusInFabulaAppBar
import com.example.lupusinfabulav1.ui.util.InputDialog
import com.google.android.material.floatingactionbutton.FloatingActionButton

@Composable
fun PlayersListsScreen(
    navigateUp: () -> Unit,
    onListClick: (String) -> Unit,
    onListLongClick: (Int) -> Unit,
    uiState: PlayersListsUiState,
    onCreateNewList: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    var isDialogOpen by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            LupusInFabulaAppBar(
                title = "list prova",//LupusInFabulaScreen.VILLAGE.title,
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
            onListLongClick = onListLongClick,
            playersLists = uiState.playersLists,
            selectedListId = uiState.selectedListId,
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
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
    selectedListId: Int?,
    modifier: Modifier = Modifier,
    onListClick: (String) -> Unit = {},
    onListLongClick: (Int) -> Unit = {},
    scrollToListName: String? = null,
    ) {
    val listCardBorder = @Composable { listId: Int ->
        when (listId) {
            selectedListId -> BorderStroke(
                width = dimensionResource(id = R.dimen.border_width_medium),
                color = Color.Cyan
            )
            else -> null
        }
    }
    val listCardBackgroundColor = @Composable { listId: Int ->
        when (listId) {
            selectedListId -> Color.LightGray
            else -> Color.White
        }
    }

    // State to control LazyColumn's scroll position
    val listState = rememberLazyListState()

    // Find the index of the player we want to scroll to
    val targetIndex = playersLists.keys.map { it.name }.indexOf(scrollToListName)

    // Scroll to the target player when the scrollToPlayer changes
    LaunchedEffect(scrollToListName) {
        if (targetIndex != -1) {
            // Center the item on the screen
            listState.animateScrollToItem(targetIndex)
        }
    }

    if (playersLists.isEmpty()) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = modifier
        ) {
            Text(
                text = stringResource(id = R.string.no_lists),
                modifier = Modifier.padding(dimensionResource(id = R.dimen.padding_medium))
            )
        }
    } else {
        LazyColumn(
            state = listState,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = modifier
        ) {
            items(playersLists.entries.toList()) { (list, playersDetails) ->
                ListPosterCard(
                    onListClick = { onListClick(list.id.toString()) },
                    onListLongClick = { onListLongClick(list.id) },
                    imageSources = playersDetails.map { it.imageSource },
                    listName = list.name,
                    posterBorder = listCardBorder(list.id),
                    posterBackgroundColor = listCardBackgroundColor(list.id),
                )
//                HorizontalDivider(
//                    thickness = dimensionResource(id = R.dimen.border_width_small)
//                )
            }
        }
    }
}


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ListPosterCard(
    onListClick: () -> Unit,
    imageSources: List<PlayerImageSource>,
    listName: String,
    modifier: Modifier = Modifier,
    onListLongClick: () -> Unit = {},
    posterHeight: Dp? = null,
    posterBorder: BorderStroke? = null,
    posterBackgroundColor: Color = Color.White,
) {
    val scrollState = rememberScrollState()

    // Use the resource only here, inside the function
    val height = posterHeight ?: dimensionResource(id = R.dimen.posterHeight)
    val border = posterBorder ?: BorderStroke(
        width = dimensionResource(id = R.dimen.border_width_small),
        color = MaterialTheme.colorScheme.outline
    )

    OutlinedCard(
        colors = CardDefaults.cardColors(containerColor = posterBackgroundColor),
        border = border,
        modifier = modifier
            .height(height)
            .fillMaxWidth()
            .background(color = posterBackgroundColor)
            .combinedClickable(
                onClick = { onListClick() },
                onLongClick = { onListLongClick() }
            )
    ) {
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(dimensionResource(id = R.dimen.padding_small))
            ) {
                Text(
                    text = listName,
                )
                Spacer(modifier = Modifier.weight(1f))
                Text(
                    text = stringResource(id = R.string.player_list_size, imageSources.size),
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
}


@Preview(showBackground = true)
@Composable
fun PlayersListScreenPreview() {
    PlayersListsScreen(
        navigateUp = {},
        uiState = PlayersListsUiState(
            playersLists = mapOf(
                PlayersList(1, "test") to FakePlayersRepository.playerDetails
            )
        ),
        onListClick = {},
        modifier = Modifier.fillMaxWidth(),
        onCreateNewList = {},
        onListLongClick = {},
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