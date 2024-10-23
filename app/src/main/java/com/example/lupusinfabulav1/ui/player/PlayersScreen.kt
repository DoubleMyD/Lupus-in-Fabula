package com.example.lupusinfabulav1.ui.player

//import com.example.lupusinfabulav1.ui.LupusInFabulaScreen
import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.lupusinfabulav1.R
import com.example.lupusinfabulav1.data.fake.FakePlayersRepository
import com.example.lupusinfabulav1.model.PlayerDetails
import com.example.lupusinfabulav1.ui.commonui.LupusInFabulaAppBar
import com.example.lupusinfabulav1.ui.playersList.PlayersListContent
import com.example.lupusinfabulav1.ui.util.InputDialog


@Composable
fun PlayersScreen(
    navigateUp: () -> Unit,
    playersDetails: List<PlayerDetails>,
    onFloatingButtonClick: () -> Unit,
    modifier: Modifier = Modifier,
    cardBackgroundColor: (PlayerDetails) -> Color = { Color.White },
    floatingButtonImage: () -> ImageVector = { Icons.Default.Check },
    playerCardAlpha: (PlayerDetails) -> Float = { 1f },
    onPlayerClick: (PlayerDetails) -> Unit = {},
    onPlayerLongClick: (PlayerDetails) -> Unit = {}
) {
//    val cardBackgroundColor = { playerDetails: PlayerDetails ->
//        if (uiState.selectedPlayers.contains(playerDetails)) Color.LightGray
//        else Color.White
//    }
    var showSearchField by remember { mutableStateOf(false) }
    var playerNameToScroll by remember { mutableStateOf<String?>(null) }
    if(showSearchField){
        InputDialog(
            title = "Search Player",
            onDismiss = { showSearchField = false },
            onConfirm = { playerName ->
                playerNameToScroll = playerName.trim()
                showSearchField = false
                        },
            placeholder = "Search Player Name"
        )
    }

    Scaffold(
        topBar = {
            LupusInFabulaAppBar(
                title = "play screen prova",//LupusInFabulaScreen.VILLAGE.title,
                canNavigateBack = true,
                navigateUp = navigateUp
            )
        },
        floatingActionButton = {
            Column{
                FloatingActionButton(
                    onClick = { showSearchField = true },
                    shape = MaterialTheme.shapes.medium,
                    modifier = Modifier
                        .padding(
                            end = WindowInsets.safeDrawing.asPaddingValues()
                                .calculateEndPadding(LocalLayoutDirection.current)
                        )
                ) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = null
                    )
                }
                Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.padding_medium)))
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
                        imageVector = floatingButtonImage(),
                        contentDescription = null
                    )
                }
            }
        },
        modifier = modifier
    ) { innerPadding ->

        PlayersListContent(
            scrollToPlayerName = playerNameToScroll,
            playersDetails = playersDetails,
            cardBackgroundColor = cardBackgroundColor,
            playerCardAlpha = playerCardAlpha,
            onPlayerClick = onPlayerClick,
            onPlayerLongClick = onPlayerLongClick,
            cardModifier = Modifier
                .height(224.dp)
                .fillMaxWidth(),
            modifier = Modifier
                .padding(innerPadding),
            //.padding(dimensionResource(id = R.dimen.padding_medium))
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PlayersScreenPreview(){
    PlayersScreen(
        navigateUp = {},
        playersDetails = FakePlayersRepository.playerDetails,
        onFloatingButtonClick = {},
        modifier = Modifier.fillMaxWidth(),
        cardBackgroundColor = { Color.White },
        floatingButtonImage = { Icons.Default.Check },
        playerCardAlpha = { 1f },
        onPlayerClick = {},
        onPlayerLongClick = {},
    )
}
