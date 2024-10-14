package com.example.lupusinfabulav1.ui.player

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.lupusinfabulav1.R
import com.example.lupusinfabulav1.data.fake.FakePlayersRepository
import com.example.lupusinfabulav1.model.PlayerDetails
import com.example.lupusinfabulav1.ui.LupusInFabulaScreen
import com.example.lupusinfabulav1.ui.commonui.LupusInFabulaAppBar
import com.example.lupusinfabulav1.ui.navigation.NavigationDestination
import com.example.lupusinfabulav1.ui.player.playerCard.PlayerCardInfo

object PlayersListDestination : NavigationDestination {
    override val route = "Players List"
    override val titleRes = R.string.app_name
    const val playerIdArg = "playerId"
    val routeWithArgs = "$route/{$playerIdArg}"
}

@Composable
fun PlayersListScreen(
    navigateUp: () -> Unit,
    playersDetails: List<PlayerDetails>,
    showRoleIcon: Boolean = false,
    modifier: Modifier = Modifier,
    onPlayerClick: (PlayerDetails) -> Unit = {},
    onPlayerLongClick: (PlayerDetails) -> Unit = {}
) {
    Scaffold(
        topBar = {
            LupusInFabulaAppBar(
                title = LupusInFabulaScreen.VILLAGE.title,
                canNavigateBack = true,
                navigateUp = navigateUp
            )
        },
        modifier = modifier
    ) { innerPadding ->
        PlayersListContent(
            playersDetails = playersDetails,
            showRoleIcon = showRoleIcon,
            onPlayerClick = onPlayerClick,
            onPlayerLongClick = onPlayerLongClick,
            modifier = Modifier
                .padding(innerPadding)
                //.padding(dimensionResource(id = R.dimen.padding_medium))
        )
    }
}

@Composable
fun PlayersListContent(
    playersDetails: List<PlayerDetails>,
    showRoleIcon: Boolean = false,
    modifier: Modifier = Modifier,
    onPlayerClick: (PlayerDetails) -> Unit = {},
    onPlayerLongClick: (PlayerDetails) -> Unit = {}
) {
    LazyColumn(
        modifier = modifier
            .background(Color.Cyan)
    ) {
        items(playersDetails) { playerDetails ->
            PlayerCardInfo(
                playerDetails = playerDetails,
                onCardClick = { onPlayerClick(playerDetails) },
                onCardLongClick = { onPlayerLongClick(playerDetails) },
                showRoleIcon = showRoleIcon,
                modifier = Modifier
                    .height(224.dp)
                    .fillMaxWidth()
                //.padding(dimensionResource(id = R.dimen.padding_very_small))
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PlayersListScreenPreview() {
    PlayersListScreen(
        navigateUp = {},
        playersDetails = FakePlayersRepository.playerDetails
    )
}