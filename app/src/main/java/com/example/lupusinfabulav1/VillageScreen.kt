package com.example.lupusinfabulav1

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.lupusinfabulav1.data.VillageUiState
import com.example.lupusinfabulav1.model.Player
import com.example.lupusinfabulav1.model.Role
import com.example.lupusinfabulav1.model.VillageEvent
import com.example.lupusinfabulav1.ui.playerCard.PlayerCard
import com.example.lupusinfabulav1.ui.playerCard.PlayerCardInfo
import kotlin.math.pow

private const val TAG = "VillageScreen"

data class VillageLayoutWeights(
    val middleWeights: List<Float>,
    val edgeWeights: List<Float>,
    val totalWeight: Float,
    val singleCardWeight: Float,
    val totalSpacingWeight: Float,
    val maxMiddleWeight: Float = totalSpacingWeight - 2
)

@Composable
fun VillageScreen6(
    uiState: VillageUiState,
    onPlayerTap: (voter: Player, voted: Player) -> Unit,
    onCenterIconClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val getBorder: @Composable (Player) -> BorderStroke = { player ->
        when {
            uiState.isGameStarted.not() -> CardDefaults.outlinedCardBorder()
            player.alive && player == uiState.selectedPlayer -> BorderStroke(dimensionResource(id = R.dimen.border_width_large), Color.Black)
            player.alive && player.role ==uiState.currentRole -> BorderStroke(dimensionResource(id = R.dimen.border_width_medium), uiState.currentRole.color)
            player.alive -> CardDefaults.outlinedCardBorder()
            else -> CardDefaults.outlinedCardBorder(false)
        }
    }
    val getVotedByRole: (Player) -> List<Role> = { player ->
        Role.entries.filter { uiState.votedPlayerByRole[it] == player }
    }
    val getPlayerVotedCount: (Player) -> Int = { player ->
        uiState.currentVoting.votesPairPlayers.count { it.votedPlayer == player }
    }
    val getBackgroundAlphaColor: (Player) -> Float = { player ->
        if (uiState.currentVoting.votesPairPlayers.any { it.voter == player }) 0.4f else 0.1f
    }
    // Memoize the calculations for all required variables
    val layoutWeights = remember(uiState.players) { calculateWeights(uiState.players) }

    // Remember the state of the dialog
    var playerToShowInfo by remember { mutableStateOf<Player?>(null) }
    val onPlayerLongPress = { player: Player -> playerToShowInfo = player }

    // Show the dialog if a player is long pressed
    if (playerToShowInfo != null) {
        Dialog(
            onDismissRequest = { playerToShowInfo = null },
            properties = DialogProperties(dismissOnBackPress = true, dismissOnClickOutside = true)
        ) {
            PlayerCardInfo(
                player = playerToShowInfo!!,
                backgroundColor = playerToShowInfo!!.role.color.copy(alpha = 0.1f),
                modifier = Modifier
                    .size(400.dp)
                    .padding(dimensionResource(id = R.dimen.padding_medium))
            )
        }
    }

    // Use Box to center an item (icon or composable) in the middle of the screen
    Box(
        propagateMinConstraints = true,
        contentAlignment = Alignment.Center, // Center the content
        modifier = modifier
            .fillMaxSize()
            .background(uiState.currentRole.color.copy(alpha = 0.05f)), // Ensure the Box takes up the entire screen
    ) {
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxSize()
        ) {
            Row(
                modifier = Modifier.weight(1f)
            ) {
                val firstPlayer = uiState.players.first()
                Spacer(modifier = Modifier.weight(layoutWeights.totalSpacingWeight / 2f))
                PlayerCard(
                    alphaColor = getBackgroundAlphaColor(firstPlayer),
                    votedCount = getPlayerVotedCount(firstPlayer),
                    rolesVotedBy = getVotedByRole(firstPlayer),
                    border = getBorder(firstPlayer),
                    player = firstPlayer,
                    onPlayerTap = { uiState.selectedPlayer?.let { onPlayerTap(it, firstPlayer) } },
                    onPlayerLongPress = { onPlayerLongPress(firstPlayer) },
                    modifier = Modifier.weight(layoutWeights.singleCardWeight),
                )
                Spacer(modifier = Modifier.weight(layoutWeights.totalSpacingWeight / 2f))
            }

            val rowPlayers = uiState.players.subList(1, uiState.players.size - 1)
            val numRows = (rowPlayers.size) / 2
            for (i in 0 until numRows) {
                val edgeWeight = layoutWeights.edgeWeights[i]
                val middleWeight = layoutWeights.middleWeights[i]
                val singleCardWeight = layoutWeights.singleCardWeight
                val firstRowPlayer = rowPlayers[i * 2]
                val secondRowPlayer = rowPlayers[i * 2 + 1]

                Row(modifier = Modifier.weight(1f)) {
                    Spacer(modifier = Modifier.weight(edgeWeight))
                    PlayerCard(
                        alphaColor = getBackgroundAlphaColor(firstRowPlayer),
                        votedCount = getPlayerVotedCount(firstRowPlayer),
                        rolesVotedBy = getVotedByRole(firstRowPlayer),
                        border = getBorder(firstRowPlayer),
                        player = firstRowPlayer,
                        onPlayerTap = { uiState.selectedPlayer?.let { onPlayerTap(it, firstRowPlayer) } },
                        onPlayerLongPress = { onPlayerLongPress(firstRowPlayer) },
                        modifier = Modifier.weight(singleCardWeight)
                    )
                    Spacer(modifier = Modifier.weight(middleWeight))
                    PlayerCard(
                        alphaColor = getBackgroundAlphaColor(secondRowPlayer),
                        votedCount = getPlayerVotedCount(secondRowPlayer),
                        rolesVotedBy = getVotedByRole(secondRowPlayer),
                        border = getBorder(secondRowPlayer),
                        player = secondRowPlayer,
                        onPlayerTap = { uiState.selectedPlayer?.let { onPlayerTap(it, secondRowPlayer) } },
                        onPlayerLongPress = { onPlayerLongPress(secondRowPlayer) },
                        modifier = Modifier.weight(singleCardWeight)
                    )
                    Spacer(modifier = Modifier.weight(edgeWeight))
                }
            }

            Row(modifier = Modifier.weight(1f)) {
                Spacer(modifier = Modifier.weight(layoutWeights.totalSpacingWeight / 2f))
                val isOdd = uiState.players.size % 2 == 1
                if (isOdd) {
                    val penultimatePlayer = uiState.players[uiState.players.size - 2]
                    PlayerCard(
                        alphaColor = getBackgroundAlphaColor(penultimatePlayer),
                        votedCount = getPlayerVotedCount(penultimatePlayer),
                        rolesVotedBy = getVotedByRole(penultimatePlayer),
                        border = getBorder(penultimatePlayer),
                        player = penultimatePlayer,
                        onPlayerTap = { uiState.selectedPlayer?.let { onPlayerTap(it, penultimatePlayer) } },
                        onPlayerLongPress = { onPlayerLongPress(penultimatePlayer) },
                        modifier = Modifier.weight(layoutWeights.singleCardWeight)
                    )
                    Spacer(modifier = Modifier.weight(layoutWeights.edgeWeights[numRows - 1] * 0.3f))
                }
                val lastPlayer = uiState.players.last()
                PlayerCard(
                    alphaColor = getBackgroundAlphaColor(lastPlayer),
                    votedCount = getPlayerVotedCount(lastPlayer),
                    rolesVotedBy = getVotedByRole(lastPlayer),
                    border = getBorder(lastPlayer),
                    player = lastPlayer,
                    onPlayerTap = { uiState.selectedPlayer?.let { onPlayerTap(it, lastPlayer) } },
                    onPlayerLongPress = { onPlayerLongPress(lastPlayer) },
                    modifier = Modifier.weight(layoutWeights.singleCardWeight)
                )
                Spacer(modifier = Modifier.weight(layoutWeights.totalSpacingWeight / 2f))
            }
        }

        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Spacer(modifier = Modifier.weight(layoutWeights.singleCardWeight + layoutWeights.maxMiddleWeight / 2))
            Image(
                painter = painterResource(id = uiState.currentRole.image),
                contentDescription = null,
                modifier = Modifier
                    .weight(layoutWeights.totalSpacingWeight - layoutWeights.maxMiddleWeight * 0.4f)
                    .clickable { onCenterIconClick() }
                    .padding(
                        dimensionResource(id = R.dimen.padding_small)
                    )
            )
            Spacer(modifier = Modifier.weight(layoutWeights.singleCardWeight + layoutWeights.maxMiddleWeight / 2))
        }
    }
}

@Preview(showBackground = true)
@Composable
fun VillageScreen6Preview() {
    VillageScreen6(
        onPlayerTap = { _, _ -> },
        onCenterIconClick = {},
        uiState = VillageUiState(),
        modifier = Modifier
            .fillMaxSize()
            .padding(dimensionResource(id = R.dimen.padding_small))
    )
}



private fun calculateWeights(players: List<Player>): VillageLayoutWeights {
    val rowPlayers = players.subList(1, players.size - 1)
    val numRows = (rowPlayers.size) / 2

    val totalWeight = 200f
    val singleCardWeight = 70f
    val totalSpacingWeight = totalWeight - (singleCardWeight * 2f)

    val middleWeights = mutableListOf<Float>()
    val edgeWeights = mutableListOf<Float>()

    val maxMiddleWeight = totalSpacingWeight - 2  // 58
    val minMiddleWeight = when (players.size) {
        6 -> 58f
        7 -> 58f
        8 -> 36f
        9 -> 36f
        else -> 16f
    }
    val minSingleEdgeWeight = 1f  // Minimum weight per edge in the final row
    val halfRowSize = numRows / 2

    val middleIndex = if (numRows % 2 == 1) halfRowSize else -1

    // First half: Increase the middle weight
    for (i in 0 until halfRowSize) {
        val t = i / (halfRowSize.toFloat())  // Normalize index to [0, 1] for the first half
        val middleWeight = fastStartCurve(t, minMiddleWeight, maxMiddleWeight)
        val singleEdgeWeight = (totalSpacingWeight - middleWeight) / 2

        middleWeights.add(middleWeight)
        edgeWeights.add(singleEdgeWeight)
    }

    // If the rowSize is odd, keep the middle row at max middleWeight
    if (middleIndex != -1) {
        middleWeights.add(maxMiddleWeight)
        edgeWeights.add(minSingleEdgeWeight)
    }

    // Second half: Decrease the middle weight in reverse
    for (i in halfRowSize - 1 downTo 0) {
        middleWeights.add(middleWeights[i])
        edgeWeights.add(edgeWeights[i])
    }

    // Log the results
    Log.d(TAG, "middleWeights: $middleWeights")
    Log.d(TAG, "edgeWeights: $edgeWeights")

    // Return all the calculated values as a VillageLayoutWeights object
    return VillageLayoutWeights(
        middleWeights = middleWeights,
        edgeWeights = edgeWeights,
        totalWeight = totalWeight,
        singleCardWeight = singleCardWeight,
        totalSpacingWeight = totalSpacingWeight,
        maxMiddleWeight = maxMiddleWeight
    )
}

// Fast start using square root of t (t^0.5)
private fun fastStartCurve(t: Float, minValue: Float, maxValue: Float): Float {
    // Fast start curve using square root: ranges from min_value to max_value over t in [0, 1]
    return minValue + (maxValue - minValue) * (t.pow(0.2f))
}

//private fun getBackgroundAlphaColor(player: Player, uiState: VillageUiState): Float {
//    return if (uiState.currentVoting.votesPairPlayers.any { it.voter == player }) 0.4f else 0.1f
//}
//
//private fun getVotedByRole(player: Player, uiState: VillageUiState): List<Role> {
//    return Role.entries.filter { uiState.votedPlayerByRole[it] == player }
//}
//
//private fun getPlayerVotedCount(player: Player, uiState: VillageUiState): Int {
//    return uiState.currentVoting.votesPairPlayers.count { it.votedPlayer == player }
//}
//
//@Composable
//private fun getBorder(player: Player, uiState: VillageUiState): BorderStroke {
//    return when {
//        uiState.isGameStarted.not() -> CardDefaults.outlinedCardBorder()
//        player.alive && player == uiState.selectedPlayer -> BorderStroke(dimensionResource(id = R.dimen.border_width_large), Color.Black)
//        player.alive && player.role == uiState.currentRole -> BorderStroke(dimensionResource(id = R.dimen.border_width_medium), uiState.currentRole.color)
//        player.alive -> CardDefaults.outlinedCardBorder()
//        else -> CardDefaults.outlinedCardBorder(false)
//    }
//}


