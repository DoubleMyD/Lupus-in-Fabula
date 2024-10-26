package com.example.lupusinfabulav1.ui.game

import androidx.annotation.DrawableRes
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import com.example.lupusinfabulav1.R
import com.example.lupusinfabulav1.model.MostVotedPlayer
import com.example.lupusinfabulav1.model.PlayerDetails
import com.example.lupusinfabulav1.model.Role
import com.example.lupusinfabulav1.model.RoleVotes
import com.example.lupusinfabulav1.ui.GameState
import com.example.lupusinfabulav1.ui.VillageUiState
import com.example.lupusinfabulav1.ui.player.playerCard.PlayerCard
import kotlinx.coroutines.delay
import kotlin.math.pow

data class VillageLayoutWeights(
    val middleWeights: List<Float>,
    val singleEdgeWeights: List<Float>,
    val totalWeight: Float,
    val singleCardWeight: Float,
    val totalSpacingWeight: Float,
    val maxMiddleWeight: Float = totalSpacingWeight - 2
)

@Composable
fun VillageContent(
    uiState: VillageUiState,
    onPlayerTap: (voter: PlayerDetails, voted: PlayerDetails) -> Unit,
    onCenterIconClick: () -> Unit,
    onCenterIconLongPress: () -> Unit,
    onPlayerLongPress: (PlayerDetails) -> Unit,
    modifier: Modifier = Modifier,
    shouldDelayAnimation: Boolean = true,
) {
    // Memoize the calculations for all required variables
    val layoutWeights =
        remember(uiState.playersState.playersDetails) { calculateWeights(uiState.playersState.playersDetails) }
    val animationDelays =
        remember(uiState.playersState.playersDetails, shouldDelayAnimation) {
            if (shouldDelayAnimation) calculateAnimationDelays(uiState.playersState.playersDetails)
            else List(uiState.playersState.playersDetails.size) { 0L }
        }

    val currentRole = when (uiState.gameState) {
        is GameState.InProgress -> uiState.gameState.currentRole
        else -> Role.CITTADINO
    }
    // Use Box to center an item (icon or composable) in the middle of the screen
    Box(
        propagateMinConstraints = true,
        contentAlignment = Alignment.Center, // Center the content
        modifier = modifier
            .fillMaxSize()
            .background(currentRole.color.copy(alpha = 0.05f)), // Ensure the Box takes up the entire screen
    ) {
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxSize()
        ) {
            PlayerRow(
                animationDelay = animationDelays[0],
                playerDetails = uiState.playersState.playersDetails.first(),
                uiState = uiState,
                currentRole = currentRole,
                leftSpaceWeight = layoutWeights.totalSpacingWeight / 2f,
                rightSpaceWeight = layoutWeights.totalSpacingWeight / 2f,
                cardWeight = layoutWeights.singleCardWeight,
                onPlayerTap = onPlayerTap,
                onPlayerLongPress = onPlayerLongPress,
                modifier = Modifier.weight(1f)
            )

            val numRows = (uiState.playersState.playersDetails.size - 2) / 2
            val isOdd = uiState.playersState.playersDetails.size % 2 == 1
            val rightPlayers = uiState.playersState.playersDetails.subList(1, numRows + 1)
            val leftPlayers = if (isOdd) {
                uiState.playersState.playersDetails.subList(
                    numRows + 3,
                    uiState.playersState.playersDetails.size
                ).reversed()
            } else {
                uiState.playersState.playersDetails.subList(
                    numRows + 2,
                    uiState.playersState.playersDetails.size
                ).reversed()
            }
            // Render middle player rows
            MiddlePlayerRows(
                numRows = numRows,
                rightPlayerDetails = rightPlayers,
                leftPlayerDetails = leftPlayers,
                animationDelays = animationDelays,
                layoutWeights = layoutWeights,
                uiState = uiState,
                currentRole = currentRole,
                onPlayerTap = onPlayerTap,
                onPlayerLongPress = onPlayerLongPress,
                modifier = Modifier.weight(numRows.toFloat())
            )
            LastRow2(
                delays = Pair(
                    animationDelays[numRows + 2],
                    animationDelays[numRows + 1]
                ),//left and right player
                firstRowPlayerDetails = uiState.playersState.playersDetails[numRows + 2],
                secondRowPlayerDetails = uiState.playersState.playersDetails[numRows + 1],
                layoutWeights = layoutWeights,
                uiState = uiState,
                currentRole = currentRole,
                onPlayerTap = onPlayerTap,
                onPlayerLongPress = onPlayerLongPress,
                numRows = numRows,
                modifier = Modifier.weight(1f)
            )
        }
        CenterImage(layoutWeights, currentRole.image, onCenterIconClick, onCenterIconLongPress)
        Text(text = uiState.playersState.playersDetails.size.toString())
    }
}

@Composable
fun MiddlePlayerRows(
    numRows: Int,
    rightPlayerDetails: List<PlayerDetails>,
    leftPlayerDetails: List<PlayerDetails>,
    animationDelays: List<Long>,
    layoutWeights: VillageLayoutWeights,
    uiState: VillageUiState,
    currentRole: Role,
    onPlayerTap: (PlayerDetails, PlayerDetails) -> Unit,
    onPlayerLongPress: (PlayerDetails) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(modifier = modifier) {
        Column(modifier = Modifier.weight(1f)) {
            for (i in 0 until numRows) {
                PlayerRow(
                    animationDelay = animationDelays[uiState.playersState.playersDetails.size - i - 1],
                    playerDetails = leftPlayerDetails[i],
                    uiState = uiState,
                    currentRole = currentRole,
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
                    playerDetails = rightPlayerDetails[i],
                    uiState = uiState,
                    currentRole = currentRole,
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

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun CenterImage(
    layoutWeights: VillageLayoutWeights,
    @DrawableRes imageResourceId: Int,
    onClick: () -> Unit,
    onCenterIconLongPress: () -> Unit
) {
    Row(
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Spacer(modifier = Modifier.weight(layoutWeights.singleCardWeight + layoutWeights.maxMiddleWeight / 2))
        Image(
            painter = painterResource(id = imageResourceId),
            contentDescription = null,
            modifier = Modifier
                .weight(layoutWeights.totalSpacingWeight - layoutWeights.maxMiddleWeight * 0.4f)
                .combinedClickable(
                    onClick = { onClick() },
                    onLongClick = { onCenterIconLongPress() }
                )
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
    firstRowPlayerDetails: PlayerDetails,
    secondRowPlayerDetails: PlayerDetails,
    layoutWeights: VillageLayoutWeights,
    uiState: VillageUiState,
    currentRole: Role,
    onPlayerTap: (PlayerDetails, PlayerDetails) -> Unit,
    onPlayerLongPress: (PlayerDetails) -> Unit,
    numRows: Int,
    modifier: Modifier = Modifier
) {
    Row(modifier = modifier) {
        Spacer(modifier = Modifier.weight(layoutWeights.totalSpacingWeight / 2f))
        val isOdd = uiState.playersState.playersDetails.size % 2 == 1
        if (isOdd) {
            Box(modifier = Modifier.weight(layoutWeights.singleCardWeight)) {
                AnimatePlayerRow(
                    isVisible = true,
                    delayMillis = delays.first,
                ) {
                    PlayerCard(
                        alphaColor = getBackgroundAlphaColor(
                            playerDetails = firstRowPlayerDetails,
                            currentRole = currentRole,
                            currentVoting = uiState.vote.currentVoting
                        ), //uiState),
                        votedCount = getPlayerVotedCount(
                            playerDetails = firstRowPlayerDetails,
                            uiState.vote.currentVoting
                        ),
                        rolesVotedBy = getVotedByRole(
                            playerDetails = firstRowPlayerDetails,
                            uiState.vote.votedPlayerByRole
                        ),
                        border = getBorder(
                            playerDetails = firstRowPlayerDetails,
                            gameStarted = uiState.gameState is GameState.InProgress,
                            selectedPlayerDetails = uiState.playersState.selectedPlayer,
                            currentVoting = uiState.vote.currentVoting,
                            currentRole = currentRole
                        ),
                        playerDetails = firstRowPlayerDetails,
                        onPlayerTap = {
                            uiState.playersState.selectedPlayer?.let {
                                onPlayerTap(
                                    it,
                                    firstRowPlayerDetails
                                )
                            }
                        },
                        onPlayerLongPress = { onPlayerLongPress(firstRowPlayerDetails) },
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
                    alphaColor = getBackgroundAlphaColor(
                        playerDetails = secondRowPlayerDetails,
                        currentRole = currentRole,
                        currentVoting = uiState.vote.currentVoting
                    ), //uiState),
                    votedCount = getPlayerVotedCount(
                        playerDetails = secondRowPlayerDetails,
                        uiState.vote.currentVoting
                    ),
                    rolesVotedBy = getVotedByRole(
                        playerDetails = secondRowPlayerDetails,
                        uiState.vote.votedPlayerByRole
                    ),
                    border = getBorder(
                        playerDetails = secondRowPlayerDetails,
                        gameStarted = uiState.gameState is GameState.InProgress,
                        selectedPlayerDetails = uiState.playersState.selectedPlayer,
                        currentVoting = uiState.vote.currentVoting,
                        currentRole = currentRole
                    ),
                    playerDetails = secondRowPlayerDetails,
                    onPlayerTap = {
                        uiState.playersState.selectedPlayer?.let {
                            onPlayerTap(
                                it,
                                secondRowPlayerDetails
                            )
                        }
                    },
                    onPlayerLongPress = { onPlayerLongPress(secondRowPlayerDetails) },
                )
            }
        }
        Spacer(modifier = Modifier.weight(layoutWeights.totalSpacingWeight / 2f))
    }
}

@Composable
private fun PlayerRow(
    animationDelay: Long,
    playerDetails: PlayerDetails,
    uiState: VillageUiState,
    currentRole: Role,
    leftSpaceWeight: Float,
    rightSpaceWeight: Float,
    cardWeight: Float,
    onPlayerTap: (voter: PlayerDetails, voted: PlayerDetails) -> Unit,
    onPlayerLongPress: (PlayerDetails) -> Unit,
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
                alphaColor = getBackgroundAlphaColor(
                    playerDetails = playerDetails,
                    currentRole = currentRole,
                    currentVoting = uiState.vote.currentVoting
                ), //uiState),
                votedCount = getPlayerVotedCount(
                    playerDetails = playerDetails,
                    uiState.vote.currentVoting
                ),
                rolesVotedBy = getVotedByRole(
                    playerDetails = playerDetails,
                    votedPlayerByRole = uiState.vote.votedPlayerByRole
                ),
                border = getBorder(
                    playerDetails = playerDetails,
                    gameStarted = uiState.gameState is GameState.InProgress,
                    selectedPlayerDetails = uiState.playersState.selectedPlayer,
                    currentVoting = uiState.vote.currentVoting,
                    currentRole = currentRole
                ),
                playerDetails = playerDetails,
                onPlayerTap = {
                    uiState.playersState.selectedPlayer?.let {
                        onPlayerTap(
                            it,
                            playerDetails
                        )
                    }
                },
                onPlayerLongPress = { onPlayerLongPress(playerDetails) },
            )
        }
        Spacer(modifier = Modifier.weight(rightSpaceWeight))
    }
}

private fun calculateAnimationDelays(playerDetails: List<PlayerDetails>): List<Long> {
    val totalDelay = 3500L
    val singleDelay = totalDelay / playerDetails.size
    val delays = playerDetails.indices.map { it * singleDelay + 500L }

    return delays
}

private fun calculateWeights(playerDetails: List<PlayerDetails>): VillageLayoutWeights {
    val rowPlayers = playerDetails.subList(1, playerDetails.size - 1)
    val numRows = (rowPlayers.size) / 2

    val totalWeight = 200f
    val singleCardWeight = 70f
    val totalSpacingWeight = totalWeight - (singleCardWeight * 2f)

    val middleWeights = mutableListOf<Float>()
    val edgeWeights = mutableListOf<Float>()

    val maxMiddleWeight = totalSpacingWeight - 2  // 58
    val minMiddleWeight = when (playerDetails.size) {
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

private fun getBackgroundAlphaColor(
    playerDetails: PlayerDetails,
    currentRole: Role,
    currentVoting: RoleVotes
): Float {
    val hasVoted = when (currentRole) {
        Role.CUPIDO -> currentVoting.votesPairPlayers.count { it.voter == playerDetails } == 2
        else -> currentVoting.votesPairPlayers.any { it.voter == playerDetails }
    }
    return if (hasVoted) 0.4f else 0.1f
}

private fun getVotedByRole(
    playerDetails: PlayerDetails,
    votedPlayerByRole: Map<Role, MostVotedPlayer>
): List<Role> {
    return if (playerDetails.alive.not()) emptyList()
    else {
        Role.entries.filter { role ->
            when (val votedPlayer = votedPlayerByRole[role]) {
                is MostVotedPlayer.SinglePlayer -> votedPlayer.playerDetails.id == playerDetails.id && votedPlayer.playerDetails.alive
                is MostVotedPlayer.PairPlayers -> votedPlayer.playerDetails1.id == playerDetails.id || votedPlayer.playerDetails2.id == playerDetails.id
                null -> false
            }
        }
    }
}

private fun getPlayerVotedCount(playerDetails: PlayerDetails, currentVoting: RoleVotes): Int {
    return currentVoting.votesPairPlayers.count { it.votedPlayerDetails == playerDetails }
}

@Composable
private fun getBorder(
    playerDetails: PlayerDetails,
    gameStarted: Boolean,
    selectedPlayerDetails: PlayerDetails?,
    currentVoting: RoleVotes,
    currentRole: Role
): BorderStroke {
    return when {
        gameStarted.not() -> CardDefaults.outlinedCardBorder()
        playerDetails.alive && playerDetails == selectedPlayerDetails -> BorderStroke(
            dimensionResource(id = R.dimen.border_width_large),
            Color.Black
        )

        currentVoting.voters.contains(playerDetails) -> BorderStroke(
            dimensionResource(id = R.dimen.border_width_medium),
            currentRole.color
        )

        playerDetails.alive -> CardDefaults.outlinedCardBorder()
        else -> CardDefaults.outlinedCardBorder(false)
    }
}


//private fun getBackgroundAlphaColor(playerDetails: PlayerDetails, currentRole: Role, /*uiState: VillageUiState*/): Float {
//    val hasVoted = when (uiState.currentRole) {
//        Role.CUPIDO -> uiState.currentVoting.votesPairPlayers.count { it.voter == playerDetails } == 2
//        else -> uiState.currentVoting.votesPairPlayers.any { it.voter == playerDetails }
//    }
//    return if (hasVoted) 0.4f else 0.1f
//}
//
//private fun getVotedByRole(playerDetails: PlayerDetails, uiState: VillageUiState): List<Role> {
//    return Role.entries.filter { role ->
//        when (val votedPlayer = uiState.votedPlayerByRole[role]) {
//            is MostVotedPlayer.SinglePlayer -> votedPlayer.playerDetails == playerDetails
//            is MostVotedPlayer.PairPlayers -> votedPlayer.playerDetails1 == playerDetails || votedPlayer.playerDetails2 == playerDetails
//            null -> false
//        }
//    }
//}
//
//private fun getPlayerVotedCount(playerDetails: PlayerDetails, uiState: VillageUiState): Int {
//    return uiState.currentVoting.votesPairPlayers.count { it.votedPlayerDetails == playerDetails }
//}
//
//@Composable
//private fun getBorder(playerDetails: PlayerDetails, uiState: VillageUiState): BorderStroke {
//    return when {
//        uiState.gameStarted.not() -> CardDefaults.outlinedCardBorder()
//        playerDetails.alive && playerDetails == uiState.selectedPlayerDetails -> BorderStroke(
//            dimensionResource(id = R.dimen.border_width_large),
//            Color.Black
//        )
//
//        uiState.currentVoting.voters.contains(playerDetails) -> BorderStroke(
//            dimensionResource(id = R.dimen.border_width_medium),
//            uiState.currentRole.color
//        )
//
//        playerDetails.alive -> CardDefaults.outlinedCardBorder()
//        else -> CardDefaults.outlinedCardBorder(false)
//    }
//}