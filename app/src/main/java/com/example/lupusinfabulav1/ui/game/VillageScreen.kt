package com.example.lupusinfabulav1.ui.game

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.lupusinfabulav1.R
import com.example.lupusinfabulav1.data.fake.FakePlayersRepository
import com.example.lupusinfabulav1.model.PlayerDetails
import com.example.lupusinfabulav1.model.PlayerManager
import com.example.lupusinfabulav1.model.RoundResultManager
import com.example.lupusinfabulav1.model.VoteManager
import com.example.lupusinfabulav1.ui.PlayersState
//import com.example.lupusinfabulav1.ui.LupusInFabulaScreen
import com.example.lupusinfabulav1.ui.VillageUiState
import com.example.lupusinfabulav1.ui.commonui.LupusInFabulaAppBar
import com.example.lupusinfabulav1.ui.playersList.PlayersListContent
import com.example.lupusinfabulav1.ui.player.playerCard.PlayerCardInfo

//private const val TAG = "VillageScreen"

@Composable
fun VillageScreen7(
    navigateUp: () -> Unit,
    viewModel: VillageViewModel,
    uiState: VillageUiState,
    onPlayerTap: (voter: PlayerDetails, voted: PlayerDetails) -> Unit,
    onCenterIconTap: () -> Unit,
    onCenterIconLongPress: () -> Unit,
    modifier: Modifier = Modifier
) {
    //Log.d("VillageScreen", "playerstate: ${uiState.playersState.playersDetails.map {"\n$it"}} ")
    HandleVillageEvents(viewModel = viewModel, context = LocalContext.current)

    var shouldDelayAnimation by remember { mutableStateOf(true) }

    // Remember the state of the dialog
    var playerDetailsToShowInfo by remember { mutableStateOf<PlayerDetails?>(null) }
    val onPlayerLongPress =
        { playerDetails: PlayerDetails -> playerDetailsToShowInfo = playerDetails }

    Scaffold(
        topBar = {
            LupusInFabulaAppBar(
                title = "prova",//LupusInFabulaScreen.VILLAGE.title,
                canNavigateBack = true,
                navigateUp = navigateUp
            )
        },
        modifier = modifier
    ) { innerPadding ->
        // Show player info dialog
        playerDetailsToShowInfo?.let { player ->
            PlayerInfoDialog(
                { playerDetailsToShowInfo = null },
                playerDetails = player
            )
        }

        val pageState = rememberPagerState { 2 }
        HorizontalPager(
            state = pageState,
            modifier = Modifier.padding(innerPadding)
        ) { page ->
            when (page) {
                0 -> VillageContent(
                    shouldDelayAnimation = shouldDelayAnimation,
                    uiState = uiState,
                    onPlayerTap = onPlayerTap,
                    onCenterIconClick = onCenterIconTap,
                    onCenterIconLongPress = onCenterIconLongPress,
                    onPlayerLongPress = onPlayerLongPress,
                    modifier = Modifier.fillMaxSize()
                )

                1 -> {
                    shouldDelayAnimation = false
                    PlayersListContent(
                        playersDetails = uiState.playersState.playersDetails,
                        showRoleIcon = true,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
        }
    }
}

@Composable
private fun PlayerInfoDialog(onDismiss: () -> Unit, playerDetails: PlayerDetails) {
    Dialog(
        onDismissRequest = { onDismiss() },
        properties = DialogProperties(dismissOnBackPress = true, dismissOnClickOutside = true)
    ) {
        PlayerCardInfo(
            playerDetails = playerDetails,
            backgroundColor = playerDetails.role.color.copy(alpha = 0.1f),
            modifier = Modifier
                .size(400.dp)
                .padding(dimensionResource(id = R.dimen.padding_medium))
        )
    }
}

@Composable
fun HandleVillageEvents(viewModel: VillageViewModel, context: Context) {
    //val villageUiEvent = viewModel.uiEvent.collectAsState(initial = null)
    val villageUiEvent by viewModel.uiEvent.collectAsState()

    LaunchedEffect(villageUiEvent, ) {
        when (villageUiEvent) {
            is VillageEvent.ErrorNotAllPlayersHaveVoted -> {
                Toast.makeText(context, R.string.error_not_all_players_have_voted, Toast.LENGTH_SHORT).show()
            }
            is VillageEvent.AllPlayersHaveVoted -> {
                Toast.makeText(context, R.string.all_players_have_voted, Toast.LENGTH_SHORT).show()
            }
            is VillageEvent.Tie -> {
                Toast.makeText(context, R.string.tie, Toast.LENGTH_SHORT).show()
            }
            is VillageEvent.TieRestartVoting -> {
                Toast.makeText(context, R.string.tie_restart_voting, Toast.LENGTH_SHORT).show()
            }
            is VillageEvent.GameNotStarted -> {
                Toast.makeText(context, R.string.game_not_started, Toast.LENGTH_SHORT).show()
            }

            is VillageEvent.CupidoAlreadyVoted -> {
                Toast.makeText(context, R.string.cupido_already_voted, Toast.LENGTH_SHORT).show()
            }
            is VillageEvent.RoleEvent -> handleRoleEvent(context, (villageUiEvent as VillageEvent.RoleEvent).roleEvent)

            else -> Unit
            //Toast.makeText(context, "An unexpected error occurred", Toast.LENGTH_SHORT).show()
        }
        Log.d("Village event", "Event triggered: $villageUiEvent")
    }
}

private fun handleRoleEvent(context: Context, roleTypeEvent: RoleTypeEvent) {
    when(roleTypeEvent){
        is RoleTypeEvent.AssassinKilledPlayers -> Toast.makeText(context, context.getString(R.string.role_event_assassin_killed_players, roleTypeEvent.playerDetailsKilled.name), Toast.LENGTH_SHORT).show()
        is RoleTypeEvent.CupidoKilledPlayers -> Toast.makeText(context, context.getString(R.string.role_event_cupido_killed_players, roleTypeEvent.playersKilled.first.name, roleTypeEvent.playersKilled.second.name), Toast.LENGTH_SHORT).show()
        is RoleTypeEvent.FaciliCostumiSavedPlayer -> Toast.makeText(context, R.string.role_event_facili_costumi_saved_player, Toast.LENGTH_SHORT).show()
        is RoleTypeEvent.VeggenteDiscoverKiller -> Toast.makeText(context, context.getString(R.string.role_event_veggente_discover_killer, roleTypeEvent.killer.name), Toast.LENGTH_SHORT).show()
    }
}


@Preview(showBackground = true)
@Composable
fun VillageScreen7Preview() {
    VillageScreen7(
        viewModel = VillageViewModel(playerManager = PlayerManager(), voteManager = VoteManager(), roundResultManager = RoundResultManager()),
        navigateUp = {},
        onPlayerTap = { _, _ -> },
        onCenterIconTap = {},
        onCenterIconLongPress = {},
        uiState = VillageUiState().copy(playersState = PlayersState().copy(playersDetails = FakePlayersRepository.playerDetails) ),
        modifier = Modifier
            .fillMaxSize()
            .padding(dimensionResource(id = R.dimen.padding_small))
    )
}

@Preview(showBackground = true)
@Composable
fun VillageScreenContentPreview() {
    VillageContent(
        onPlayerTap = { _, _ -> },
        onCenterIconClick = {},
        onPlayerLongPress = {},
        onCenterIconLongPress = {},
        uiState = VillageUiState().copy(playersState = PlayersState().copy(playersDetails = FakePlayersRepository.playerDetails) ),
        modifier = Modifier
            .fillMaxSize()
            .padding(dimensionResource(id = R.dimen.padding_small))
    )
}



