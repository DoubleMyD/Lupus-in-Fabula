package com.example.lupusinfabulav1.ui

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.example.lupusinfabulav1.R
import com.example.lupusinfabulav1.ui.viewModels.NewPlayerEvent
import com.example.lupusinfabulav1.ui.viewModels.NewPlayerViewModel
import com.example.lupusinfabulav1.ui.viewModels.PlayersForRoleEvent
import com.example.lupusinfabulav1.ui.viewModels.PlayersForRoleViewModel
import com.example.lupusinfabulav1.ui.viewModels.RoleTypeEvent
import com.example.lupusinfabulav1.ui.viewModels.VillageEvent
import com.example.lupusinfabulav1.ui.viewModels.VillageViewModel

private const val TAG = "App Events"

@Composable
fun HandleNewPlayerEvents(viewModel: NewPlayerViewModel, context: Context) {
    val newPlayerUiEvent = viewModel.uiEvent.collectAsState(initial = null)

    LaunchedEffect(newPlayerUiEvent.value) {
        when (newPlayerUiEvent.value) {
            NewPlayerEvent.ErrorNameNotAvailable -> {
                Toast.makeText(context, R.string.error_name_not_available, Toast.LENGTH_SHORT).show()
            }
            else -> Unit // Handle other events if necessary
        }
    }
}

@Composable
fun HandlePlayersForRoleEvents(viewModel: PlayersForRoleViewModel, context: Context) {
    val playersForRoleUiEvent = viewModel.uiEvent.collectAsState(initial = null)

    LaunchedEffect(playersForRoleUiEvent.value) {
        when (playersForRoleUiEvent.value) {
            PlayersForRoleEvent.ErrorNotAllPlayersSelected -> {
                Toast.makeText(context, R.string.error_not_all_players_selected, Toast.LENGTH_SHORT).show()
            }
            PlayersForRoleEvent.ErrorTooManyPlayersSelected -> {
                Toast.makeText(context, R.string.error_too_many_players_selected, Toast.LENGTH_SHORT).show()
            }
            else -> Unit // Handle other potential events
        }
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
        Log.d(TAG, "Event triggered: $villageUiEvent")
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