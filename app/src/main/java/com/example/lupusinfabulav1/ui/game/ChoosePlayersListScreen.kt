package com.example.lupusinfabulav1.ui.game

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.tooling.preview.Preview
import com.example.lupusinfabulav1.R
import com.example.lupusinfabulav1.ui.commonui.LupusInFabulaAppBar
import com.example.lupusinfabulav1.ui.playersList.Lists
import com.example.lupusinfabulav1.ui.playersList.PlayersListsUiState
import com.example.lupusinfabulav1.ui.util.InputDialog

@Composable
fun ChoosePlayersListScreen(
    navigateUp: () -> Unit,
    onListLongClick: (Int) -> Unit,
    onListClick: (String) -> Unit,
    onConfirmClick: () -> Unit,
    uiState: ChoosePlayersListUiState,
    modifier: Modifier = Modifier,
) {
    var showSearchField by remember { mutableStateOf(false) }
    var listNameToScroll by remember { mutableStateOf<String?>(null) }

    if (showSearchField) {
        InputDialog(
            title = "Search List",
            onDismiss = { showSearchField = false },
            onConfirm = { listName ->
                listNameToScroll = listName.trim()
                showSearchField = false
            },
            placeholder = "Search Player Name"
        )
    }

    Scaffold(
        topBar = {
            LupusInFabulaAppBar(
                title = "list prova",//LupusInFabulaScreen.VILLAGE.title,
                canNavigateBack = true,
                navigateUp = navigateUp
            )
        },
        floatingActionButton = {
            ChoosePlayersListFloatingButtons(
                onSearchClick = { showSearchField = true },
                onConfirmClick = { onConfirmClick() }
            )
        },
        modifier = modifier
    ) { innerPadding ->

        Lists(
            onListClick = onListClick,
            onListLongClick = onListLongClick,
            playersLists = uiState.playersLists,
            selectedListId = uiState.selectedListId,
            scrollToListName = listNameToScroll,
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        )
    }
}

@Composable
fun ChoosePlayersListFloatingButtons(
    onSearchClick: () -> Unit,
    onConfirmClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.End,
        modifier = modifier.padding(
            end = WindowInsets.safeDrawing.asPaddingValues()
                .calculateEndPadding(LocalLayoutDirection.current)
        )
    ) {
        FloatingActionButton(
            onClick = onSearchClick,
            shape = MaterialTheme.shapes.medium,
        ) {
            Icon(imageVector = Icons.Default.Search, contentDescription = null)
        }
        Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.padding_medium)))
        ExtendedFloatingActionButton(
            containerColor = MaterialTheme.colorScheme.primary,
            onClick = onConfirmClick,
            shape = MaterialTheme.shapes.medium,
        ) {
            Text(text = "Confirm")
            Icon(imageVector = Icons.Default.Check, contentDescription = null)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ChoosePlayersListScreenPreview() {
    ChoosePlayersListScreen(
        navigateUp = {},
        uiState = ChoosePlayersListUiState(
            playersLists = mapOf()
            //playerDetails = FakePlayersRepository.playerDetails
        ),
        onListClick = {},
        modifier = Modifier.fillMaxSize(),
        onListLongClick = {},
        onConfirmClick = {}
    )
}