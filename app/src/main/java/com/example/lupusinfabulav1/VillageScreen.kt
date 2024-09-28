package com.example.lupusinfabulav1

import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.lupusinfabulav1.data.VillageUiState
import com.example.lupusinfabulav1.model.Player
import com.example.lupusinfabulav1.model.getPainter
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
            player == uiState.selectedPlayer -> BorderStroke(2.dp, Color.Black)
            player.role ==uiState.currentRole -> BorderStroke(4.dp, uiState.currentRole.color)
            else -> CardDefaults.outlinedCardBorder()
        }
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
            PlayerCard(
                border = getBorder(playerToShowInfo!!),
                orientation = Orientation.Vertical,
                player = playerToShowInfo!!,
                onPlayerTap = { },
                onPlayerLongPress = { },
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
            .background(uiState.currentRole.color.copy(alpha = 0.1f)), // Ensure the Box takes up the entire screen
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
                    border = getBorder(firstPlayer),
                    player = firstPlayer,
                    onPlayerTap = { onPlayerTap(uiState.selectedPlayer, firstPlayer) },
                    onPlayerLongPress = { onPlayerLongPress(firstPlayer) },
                    modifier = Modifier.weight(layoutWeights.singleCardWeight)
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
                        border = getBorder(firstRowPlayer),
                        player = firstRowPlayer,
                        onPlayerTap = { onPlayerTap(uiState.selectedPlayer, firstRowPlayer) },
                        onPlayerLongPress = { onPlayerLongPress(firstRowPlayer) },
                        modifier = Modifier.weight(singleCardWeight)
                    )
                    Spacer(modifier = Modifier.weight(middleWeight))
                    PlayerCard(
                        border = getBorder(secondRowPlayer),
                        player = secondRowPlayer,
                        onPlayerTap = { onPlayerTap(uiState.selectedPlayer, secondRowPlayer) },
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
                        border = getBorder(penultimatePlayer),
                        player = penultimatePlayer,
                        onPlayerTap = { onPlayerTap(uiState.selectedPlayer, penultimatePlayer) },
                        onPlayerLongPress = { onPlayerLongPress(penultimatePlayer) },
                        modifier = Modifier.weight(layoutWeights.singleCardWeight)
                    )
                    Spacer(modifier = Modifier.weight(layoutWeights.edgeWeights[numRows - 1] * 0.3f))
                }
                val lastPlayer = uiState.players.last()
                PlayerCard(
                    border = getBorder(lastPlayer),
                    player = lastPlayer,
                    onPlayerTap = { onPlayerTap(uiState.selectedPlayer, lastPlayer) },
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
                painter = painterResource(id = R.drawable.android_superhero1),
                contentDescription = null,
                modifier = Modifier
                    .weight(layoutWeights.totalSpacingWeight - layoutWeights.maxMiddleWeight * 0.4f)
                    .padding(
                        dimensionResource(id = R.dimen.padding_small)
                    )
                    .clickable { onCenterIconClick() }
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

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun PlayerCard(alphaColor: Float = 0.1f, border: BorderStroke, orientation: Orientation = Orientation.Horizontal, player: Player, onPlayerTap:() -> Unit, onPlayerLongPress:() -> Unit, modifier: Modifier = Modifier) {
    val backgroundColor = player.role.color.copy(alpha = alphaColor)

    val cardModifier = Modifier
        .fillMaxSize()
        .background(backgroundColor)

    OutlinedCard(
        border = border,
        modifier = modifier
            .combinedClickable(
                onClick = { onPlayerTap() },
                onLongClick = { onPlayerLongPress() }
            )
    ) {
        if (Orientation.Vertical == orientation) {
            PlayerCardVertical(
                player = player,
                modifier = cardModifier
            )
        }
        if (Orientation.Horizontal == orientation) {
            PlayerCardHorizontal(
                player = player,
                modifier = cardModifier
            )
        }
    }
}

@Composable
fun PlayerCardVertical(
    player: Player,
    modifier: Modifier = Modifier,
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = modifier
    ) {
        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            Text(
                text = player.name,
                textAlign = TextAlign.Center,
                maxLines = 1,
                modifier = Modifier
                    .weight(4f)
                    .basicMarquee(velocity = 10.dp, repeatDelayMillis = 1000 * 10)
            )
            Image(
                painter = painterResource(id = player.role.image),
                contentDescription = null,
                modifier = Modifier
                    .weight(1f)
            )
        }
        Image(
            painter = player.imageSource.getPainter(),
            contentDescription = player.name,
            modifier = Modifier
                .weight(3f)
                .padding(vertical = 4.dp)
        )
    }
}

@Composable
fun PlayerCardHorizontal(
    player: Player,
    modifier: Modifier = Modifier,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
        modifier = modifier
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .weight(4f)
                .padding(4.dp)
                .fillMaxSize()
        ) {
            Image(
                painter = painterResource(id = player.role.image),
                contentDescription = null,
                modifier = Modifier
                    .size(32.dp)
            )
        }
        Image(
            painter = player.imageSource.getPainter(),
            contentDescription = player.name,
            modifier = Modifier
                .weight(7f)
                .fillMaxSize()
        )
    }
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


