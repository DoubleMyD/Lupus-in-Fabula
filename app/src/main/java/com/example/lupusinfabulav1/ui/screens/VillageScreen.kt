package com.example.lupusinfabulav1.ui.screens


import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
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
import com.example.lupusinfabulav1.R
import com.example.lupusinfabulav1.model.MostVotedPlayer
import com.example.lupusinfabulav1.model.Player
import com.example.lupusinfabulav1.model.Role
import com.example.lupusinfabulav1.ui.VillageUiState
import com.example.lupusinfabulav1.ui.playerCard.PlayerCard
import com.example.lupusinfabulav1.ui.playerCard.PlayerCardInfo
import kotlinx.coroutines.delay
import kotlin.math.pow


//private const val TAG = "VillageScreen"
data class VillageLayoutWeights(
    val middleWeights: List<Float>,
    val singleEdgeWeights: List<Float>,
    val totalWeight: Float,
    val singleCardWeight: Float,
    val totalSpacingWeight: Float,
    val maxMiddleWeight: Float = totalSpacingWeight - 2
)

@Composable
fun VillageScreen7(
    uiState: VillageUiState,
    onPlayerTap: (voter: Player, voted: Player) -> Unit,
    onCenterIconClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    //PAGER  component from youtube, useful for scrolling from different pages

    // Memoize the calculations for all required variables
    val layoutWeights = remember(uiState.players) { calculateWeights(uiState.players) }
    val animationDelays = remember(uiState.players) { calculateAnimationDelays(uiState.players) }

    // Remember the state of the dialog
    var playerToShowInfo by remember { mutableStateOf<Player?>(null) }
    val onPlayerLongPress = { player: Player -> playerToShowInfo = player }

    // Show player info dialog
    playerToShowInfo?.let { player ->
        Dialog(
            onDismissRequest = { playerToShowInfo = null },
            properties = DialogProperties(dismissOnBackPress = true, dismissOnClickOutside = true)
        ) {
            PlayerCardInfo(
                player = player,
                backgroundColor = player.role.color.copy(alpha = 0.1f),
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
            PlayerRow(
                animationDelay = animationDelays[0],
                player = uiState.players.first(),
                uiState = uiState,
                leftSpaceWeight = layoutWeights.totalSpacingWeight / 2f,
                rightSpaceWeight = layoutWeights.totalSpacingWeight / 2f,
                cardWeight = layoutWeights.singleCardWeight,
                onPlayerTap = onPlayerTap,
                onPlayerLongPress = onPlayerLongPress,
                modifier = Modifier.weight(1f)
            )

            val numRows = (uiState.players.size - 2) / 2
            val isOdd = uiState.players.size % 2 == 1
            val rightPlayers = uiState.players.subList(1, numRows + 1)
            val leftPlayers = if (isOdd) {
                uiState.players.subList(numRows + 3, uiState.players.size).reversed()
            } else {
                uiState.players.subList(numRows + 2, uiState.players.size).reversed()
            }

            // Render middle player rows
            MiddlePlayerRows(
                numRows = numRows,
                rightPlayers = rightPlayers,
                leftPlayers = leftPlayers,
                animationDelays = animationDelays,
                layoutWeights = layoutWeights,
                uiState = uiState,
                onPlayerTap = onPlayerTap,
                onPlayerLongPress = onPlayerLongPress,
                modifier = Modifier.weight(numRows.toFloat())
            )
//            Row(
//                modifier = Modifier.weight(numRows.toFloat())
//            ) {
//                Column( //
//                    modifier = Modifier.weight(1f)
//                ) {
//                    for (i in 0 until numRows) {
//                        PlayerRow(
//                            animationDelay = animationDelays[uiState.players.size - i - 1],
//                            player = leftPlayers[i],
//                            uiState = uiState,
//                            leftSpaceWeight = layoutWeights.singleEdgeWeights[i],
//                            rightSpaceWeight = layoutWeights.middleWeights[i] / 2f,
//                            cardWeight = layoutWeights.singleCardWeight,
//                            onPlayerTap = onPlayerTap,
//                            onPlayerLongPress = onPlayerLongPress,
//                            modifier = Modifier.weight(1f)
//                        )
//                    }
//                }
//                Column(
//                    modifier = Modifier.weight(1f)
//                ) {
//                    for (i in 0 until numRows) {
//                        PlayerRow(
//                            animationDelay = animationDelays[i + 1],
//                            player = rightPlayers[i],
//                            uiState = uiState,
//                            leftSpaceWeight = layoutWeights.middleWeights[i] / 2f,
//                            rightSpaceWeight = layoutWeights.singleEdgeWeights[i],
//                            cardWeight = layoutWeights.singleCardWeight,
//                            onPlayerTap = onPlayerTap,
//                            onPlayerLongPress = onPlayerLongPress,
//                            modifier = Modifier.weight(1f)
//                        )
//                    }
//                }
//            }
            LastRow2(
                delays = Pair(
                    animationDelays[numRows + 2],
                    animationDelays[numRows + 1]
                ),//left and right player
                firstRowPlayer = uiState.players[numRows + 2],
                secondRowPlayer = uiState.players[numRows + 1],
                layoutWeights = layoutWeights,
                uiState = uiState,
                onPlayerTap = onPlayerTap,
                onPlayerLongPress = onPlayerLongPress,
                numRows = numRows,
                modifier = Modifier.weight(1f)
            )
        }
        CenterImage(layoutWeights, uiState, onCenterIconClick)
    }
}

@Preview(showBackground = true)
@Composable
fun VillageScreen7Preview() {
    VillageScreen7(
        onPlayerTap = { _, _ -> },
        onCenterIconClick = {},
        uiState = VillageUiState(),
        modifier = Modifier
            .fillMaxSize()
            .padding(dimensionResource(id = R.dimen.padding_small))
    )
}

@Composable
fun MiddlePlayerRows(
    numRows: Int,
    rightPlayers: List<Player>,
    leftPlayers: List<Player>,
    animationDelays: List<Long>,
    layoutWeights: VillageLayoutWeights,
    uiState: VillageUiState,
    onPlayerTap: (Player, Player) -> Unit,
    onPlayerLongPress: (Player) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(modifier = modifier) {
        Column(modifier = Modifier.weight(1f)) {
            for (i in 0 until numRows) {
                PlayerRow(
                    animationDelay = animationDelays[uiState.players.size - i - 1],
                    player = leftPlayers[i],
                    uiState = uiState,
                    leftSpaceWeight = layoutWeights.singleEdgeWeights[i],
                    rightSpaceWeight = layoutWeights.middleWeights[i] / 2f,
                    cardWeight = layoutWeights.singleCardWeight,
                    onPlayerTap = onPlayerTap,
                    onPlayerLongPress = onPlayerLongPress,
                    modifier = Modifier.weight(1f)
                )
            }
        }
        Column(modifier = Modifier.weight(1f)) {
            for (i in 0 until numRows) {
                PlayerRow(
                    animationDelay = animationDelays[i + 1],
                    player = rightPlayers[i],
                    uiState = uiState,
                    leftSpaceWeight = layoutWeights.middleWeights[i] / 2f,
                    rightSpaceWeight = layoutWeights.singleEdgeWeights[i],
                    cardWeight = layoutWeights.singleCardWeight,
                    onPlayerTap = onPlayerTap,
                    onPlayerLongPress = onPlayerLongPress,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
fun AnimatePlayerRow(
    isVisible: Boolean,
    delayMillis: Long,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    // Use internal state to handle entry visibility with delay
    val internalVisibleState = remember { mutableStateOf(false) }

    // Trigger the entry animation with a delay
    LaunchedEffect(Unit) {
        delay(delayMillis)
        internalVisibleState.value = true
    }

    AnimatedVisibility(
        visible = internalVisibleState.value && isVisible,
        enter = scaleIn(animationSpec = tween(durationMillis = 1000)) + fadeIn(
            animationSpec = tween(durationMillis = 1000)
        ),
        exit = scaleOut() + fadeOut(),
        modifier = modifier
    ) {
        content()
    }
}

@Composable
fun CenterImage(layoutWeights: VillageLayoutWeights, uiState: VillageUiState, onClick: () -> Unit) {
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
                .clickable { onClick() }
                .padding(
                    dimensionResource(id = R.dimen.padding_small)
                )
        )
        Spacer(modifier = Modifier.weight(layoutWeights.singleCardWeight + layoutWeights.maxMiddleWeight / 2))
    }
}


@Composable
fun LastRow2(
    delays: Pair<Long, Long>,
    firstRowPlayer: Player,
    secondRowPlayer: Player,
    layoutWeights: VillageLayoutWeights,
    uiState: VillageUiState,
    onPlayerTap: (Player, Player) -> Unit,
    onPlayerLongPress: (Player) -> Unit,
    numRows: Int,
    modifier: Modifier = Modifier
) {
    Row(modifier = modifier) {
        Spacer(modifier = Modifier.weight(layoutWeights.totalSpacingWeight / 2f))
        val isOdd = uiState.players.size % 2 == 1
        if (isOdd) {
            Box(modifier = Modifier.weight(layoutWeights.singleCardWeight)) {
                AnimatePlayerRow(
                    isVisible = true,
                    delayMillis = delays.first,
                ) {
                    PlayerCard(
                        alphaColor = getBackgroundAlphaColor(firstRowPlayer, uiState),
                        votedCount = getPlayerVotedCount(firstRowPlayer, uiState),
                        rolesVotedBy = getVotedByRole(firstRowPlayer, uiState),
                        border = getBorder(firstRowPlayer, uiState),
                        player = firstRowPlayer,
                        onPlayerTap = {
                            uiState.selectedPlayer?.let {
                                onPlayerTap(
                                    it,
                                    firstRowPlayer
                                )
                            }
                        },
                        onPlayerLongPress = { onPlayerLongPress(firstRowPlayer) },
                    )
                }
            }
            Spacer(modifier = Modifier.weight(layoutWeights.singleEdgeWeights[numRows - 1] * 0.3f))
        }
        Box(modifier = Modifier.weight(layoutWeights.singleCardWeight)) {//The box is used to separate the layout from the content during the animation
            AnimatePlayerRow(
                isVisible = true,
                delayMillis = delays.second,
            ) {
                PlayerCard(
                    alphaColor = getBackgroundAlphaColor(secondRowPlayer, uiState),
                    votedCount = getPlayerVotedCount(secondRowPlayer, uiState),
                    rolesVotedBy = getVotedByRole(secondRowPlayer, uiState),
                    border = getBorder(secondRowPlayer, uiState),
                    player = secondRowPlayer,
                    onPlayerTap = {
                        uiState.selectedPlayer?.let {
                            onPlayerTap(
                                it,
                                secondRowPlayer
                            )
                        }
                    },
                    onPlayerLongPress = { onPlayerLongPress(secondRowPlayer) },
                )
            }
        }
        Spacer(modifier = Modifier.weight(layoutWeights.totalSpacingWeight / 2f))
    }
}

@Composable
private fun PlayerRow(
    animationDelay: Long,
    player: Player,
    uiState: VillageUiState,
    leftSpaceWeight: Float,
    rightSpaceWeight: Float,
    cardWeight: Float,
    onPlayerTap: (voter: Player, voted: Player) -> Unit,
    onPlayerLongPress: (Player) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
    ) {
        Spacer(modifier = Modifier.weight(leftSpaceWeight))
        AnimatePlayerRow(
            isVisible = true,
            delayMillis = animationDelay, // Top to bottom delay
            modifier = Modifier.weight(cardWeight)
        ) {
            PlayerCard(
                alphaColor = getBackgroundAlphaColor(player, uiState),
                votedCount = getPlayerVotedCount(player, uiState),
                rolesVotedBy = getVotedByRole(player, uiState),
                border = getBorder(player, uiState),
                player = player,
                onPlayerTap = { uiState.selectedPlayer?.let { onPlayerTap(it, player) } },
                onPlayerLongPress = { onPlayerLongPress(player) },
            )
        }
        Spacer(modifier = Modifier.weight(rightSpaceWeight))
    }
}

private fun calculateAnimationDelays(players: List<Player>): List<Long> {
    val totalDelay = 3500L
    val singleDelay = totalDelay / players.size
    val delays = players.indices.map { it * singleDelay + 500L }

    return delays
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

    // Return all the calculated values as a VillageLayoutWeights object
    return VillageLayoutWeights(
        middleWeights = middleWeights,
        singleEdgeWeights = edgeWeights,
        totalWeight = totalWeight,
        singleCardWeight = singleCardWeight,
        totalSpacingWeight = totalSpacingWeight,
        maxMiddleWeight = maxMiddleWeight
    )
}

// Fast start using square root of t (t^0.5)
private fun fastStartCurve(
    t: Float,
    minValue: Float,
    @Suppress("SameParameterValue") maxValue: Float
): Float {
    // Fast start curve using square root: ranges from min_value to max_value over t in [0, 1]
    return minValue + (maxValue - minValue) * (t.pow(0.2f))
}

private fun getBackgroundAlphaColor(player: Player, uiState: VillageUiState): Float {
    val hasVoted = when (uiState.currentRole) {
        Role.CUPIDO -> uiState.currentVoting.votesPairPlayers.count { it.voter == player } == 2
        else -> uiState.currentVoting.votesPairPlayers.any { it.voter == player }
    }
    return if (hasVoted) 0.4f else 0.1f
}

private fun getVotedByRole(player: Player, uiState: VillageUiState): List<Role> {
    return Role.entries.filter { role ->
        when (val votedPlayer = uiState.votedPlayerByRole[role]) {
            is MostVotedPlayer.SinglePlayer -> votedPlayer.player == player
            is MostVotedPlayer.PairPlayers -> votedPlayer.player1 == player || votedPlayer.player2 == player
            null -> false
        }
    }
}

private fun getPlayerVotedCount(player: Player, uiState: VillageUiState): Int {
    return uiState.currentVoting.votesPairPlayers.count { it.votedPlayer == player }
}

@Composable
private fun getBorder(player: Player, uiState: VillageUiState): BorderStroke {
    return when {
        uiState.gameStarted.not() -> CardDefaults.outlinedCardBorder()
        player.alive && player == uiState.selectedPlayer -> BorderStroke(
            dimensionResource(id = R.dimen.border_width_large),
            Color.Black
        )

        uiState.currentVoting.voters.contains(player) -> BorderStroke(
            dimensionResource(id = R.dimen.border_width_medium),
            uiState.currentRole.color
        )

        player.alive -> CardDefaults.outlinedCardBorder()
        else -> CardDefaults.outlinedCardBorder(false)
    }
}

