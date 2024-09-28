package com.example.lupusinfabulav1.ui.util

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.lupusinfabulav1.R
import com.example.lupusinfabulav1.RoleCard
import com.example.lupusinfabulav1.model.Player
import com.example.lupusinfabulav1.model.Role
import com.example.lupusinfabulav1.model.getPainter
import kotlin.math.pow

class VillageDisposition {
    @Composable
    fun VillageScreenPaddingVersion(
        playerSize: Int,
        modifier: Modifier = Modifier,
    ) {
        val rowSize = (playerSize - 2) / 2
        val totalWidth = 100f
        val cardWidth = 30f
        val totalPadding = totalWidth - (cardWidth * 2)
        val singlePadding = totalPadding / (rowSize / 2)

        Column(
            modifier = modifier
        ) {
            RoleCard(
                role = Role.MEDIUM,
                playerRoleNumber = 1,
                isSelected = true,
                onRoleSelected = {},
                modifier = Modifier
                    .padding(horizontal = (singlePadding * (rowSize)).dp)
                    .weight(1f)
            )
            for (i in 0 until rowSize) {
                val leftPadding = when {
                    i < rowSize / 2 -> singlePadding * (rowSize / 2 - i)
                    else -> singlePadding * (i - (rowSize / 2))
                }
                val rightPadding = when {
                    i < rowSize / 2 -> singlePadding * i
                    else -> singlePadding * (rowSize - i)
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 1.dp)//dimensionResource(id = R.dimen.padding_small))
                        .weight(1f)
                ) {
                    RoleCard(modifier = Modifier
                        .weight(3f)
                        .padding(start = leftPadding.dp, end = rightPadding.dp),
                        playerRoleNumber = leftPadding.toInt(),
                        role = Role.MEDIUM,
                        isSelected = true,
                        onRoleSelected = {})
                    Spacer(modifier = Modifier.weight(1f))
                    RoleCard(modifier = Modifier
                        .weight(3f)
                        .padding(start = rightPadding.dp, end = leftPadding.dp),
                        playerRoleNumber = 1,
                        role = Role.MEDIUM,
                        isSelected = true,
                        onRoleSelected = {})
                }
            }
            RoleCard(
                role = Role.MEDIUM,
                playerRoleNumber = 1,
                isSelected = true,
                onRoleSelected = {},
                modifier = Modifier
                    .padding(horizontal = (singlePadding * (rowSize)).dp)
                    .weight(1f)
            )
        }
    }


    @Preview(showBackground = true)
    @Composable
    fun VillageScreenPaddingVersionPreview() {
        VillageScreenPaddingVersion(
            playerSize = 16,
            modifier = Modifier
                .fillMaxSize()
                .padding(dimensionResource(id = R.dimen.padding_small))
        )
    }


    @Composable
    fun VillageScreen2(
        playerSize: Int,
        modifier: Modifier = Modifier,
    ) {
        val rowSize = (playerSize - 2) / 2

        val totalWeight = 100
        val singleCardWeight = 36
        val remainingWeight = totalWeight - (singleCardWeight * 2)
        val singleWeightIncrement = remainingWeight / (rowSize / 2)

        var singleEdgeWeight = 1 + singleWeightIncrement * (rowSize / 2)
        var middleWeight = singleWeightIncrement

        /*
    first row : middle = 4, edge = 16
    second row : middle = 6, edge = 14
    third row : middle = 8, edge = 12
    fourth row : middle = 10, edge = 10
     */
        Column(modifier = modifier) {
            RoleCard2(modifier = Modifier.weight(1f))
            for (i in 0 until (rowSize)) {
                if (i <= (rowSize / 2)) {
                    singleEdgeWeight -= singleWeightIncrement
                    middleWeight += singleWeightIncrement
                } else {
                    singleEdgeWeight += singleWeightIncrement
                    middleWeight -= singleWeightIncrement
                }
                singleEdgeWeight = singleEdgeWeight.coerceAtLeast(1)
                middleWeight = middleWeight.coerceAtLeast(1)

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                ) {
                    Spacer(modifier = Modifier.weight(singleEdgeWeight.toFloat()))
                    RoleCard2(
                        number = singleEdgeWeight,
                        modifier = Modifier.weight(singleCardWeight.toFloat())
                    )
                    Spacer(modifier = Modifier.weight(middleWeight.toFloat()))
                    RoleCard2(
                        number = middleWeight,
                        modifier = Modifier.weight(singleCardWeight.toFloat())
                    )
                    Spacer(modifier = Modifier.weight(singleEdgeWeight.toFloat()))
                }
            }
            RoleCard2(modifier = Modifier.weight(1f))
        }
    }

    @Preview(showBackground = true)
    @Composable
    fun VillageScreen2Preview() {
        VillageScreen2(
            playerSize = 16,
            modifier = Modifier
                .fillMaxSize()
                .padding(dimensionResource(id = R.dimen.padding_small))
        )
    }

    @Composable
    fun VillageScreen4(
        playerSize: Int,
        modifier: Modifier = Modifier
    ) {
        val numRows = playerSize / 2
        val totalWeight = 200f
        val singleCardWeight = 70f
        val totalSpacingWeight = totalWeight - (singleCardWeight * 2f)

        val middleWeights = mutableListOf<Float>()
        val edgeWeights = mutableListOf<Float>()
        val residualWeights = mutableListOf<Float>()

        val maxMiddleWeight = totalSpacingWeight - 2  // 58

        // Assume the cubic function is parameterized for simplicity
        fun cubicCurve(t: Float): Float {
            // Cubic function coefficients, you may need to adjust
            val a = 96  // this affects the steepness of the curve
            val b = -144
            val c = 104
            val d = 8   // Starting middle weight
            return a * t.pow(3) + b * t.pow(2) + c * t + d
        }

        // Scaled cubic function that ensures weights fit the desired bounds
        fun scaledCubicCurveFastToSlow(t: Float, minValue: Float, maxValue: Float): Float {
            // Scaled cubic curve: ranges from min_value to max_value over t in [0, 1]
            return minValue + (maxValue - minValue) * (3 * t.pow(2) - 2 * t.pow(3))  // S-shape cubic
        }

        // Scaled cubic function that ensures weights fit the desired bounds
        fun scaledCubicCurveSlowToFast(t: Float, minValue: Float, maxValue: Float): Float {
            // Scaled cubic curve: ranges from min_value to max_value over t in [0, 1]
            return minValue + (maxValue - minValue) * (t.pow(3))  // S-shape cubic
        }

        // Fast start using square root of t (t^0.5)
        fun fastStartCurve(t: Float, minValue: Float, maxValue: Float): Float {
            // Fast start curve using square root: ranges from min_value to max_value over t in [0, 1]
            return minValue + (maxValue - minValue) * (t.pow(0.2f))
        }

        for (i in 0 until numRows) {
            val t = i / (numRows - 1f)  // Normalize index to range [0, 1]

            //val middleWeight = cubicCurve(t)
            //val middleWeight = scaledCubicCurveSlowToFast(t, 8f, maxMiddleWeight)
            //val middleWeight = scaledCubicCurveFastToSlow(t, 8f, maxMiddleWeight)
            val middleWeight = fastStartCurve(t, 24f, maxMiddleWeight)

            val edgeWeight = (totalSpacingWeight - middleWeight) / 2f

            middleWeights.add(middleWeight)
            edgeWeights.add(edgeWeight)

            residualWeights.add(totalSpacingWeight - (edgeWeight * 2f) - middleWeight)
        }

        val middleRow = numRows / 2
        Column(modifier = modifier) {
            RoleCard2(modifier = Modifier.weight(1f))
            for (i in 0 until middleRow) {
                RowRoleCards(
                    edgeWeight = edgeWeights[i],
                    middleWeight = middleWeights[i],
                    singleCardWeight = singleCardWeight,
                    modifier = Modifier.weight(1f)
                )
            }
            for (i in 0 until middleRow) {
                RowRoleCards(
                    edgeWeight = edgeWeights[middleRow - i],
                    middleWeight = middleWeights[middleRow - i],
                    singleCardWeight = singleCardWeight,
                    modifier = Modifier.weight(1f)
                )
            }
            RoleCard2(modifier = Modifier.weight(1f))
        }
    }


    @Composable
    fun VillageScreen5(
        playerSize: Int,
        modifier: Modifier = Modifier
    ) {
        val totalWeight = 200f
        val singleCardWeight = 70f
        val totalSpacingWeight = totalWeight - (singleCardWeight * 2f)

        val middleWeights = mutableListOf<Float>()
        val edgeWeights = mutableListOf<Float>()
        val residualWeights = mutableListOf<Float>()

        val maxMiddleWeight = totalSpacingWeight - 2  // 58
        val minSingleEdgeWeight = 1f  // Minimum weight per edge in the final row
        val maxSingleEdgeWeight = (totalSpacingWeight - 8) / 2f // 26

        //Split the rowSize into two halves
        val halfRowSize = playerSize / 2

        val middleIndex = when {
            playerSize % 2 == 1 -> halfRowSize  // Middle row for odd rowSize
            else -> -1  // No middle row for even rowSize
        }

        // First half: Increase the middle weight
        for (i in 0 until halfRowSize) {
            val t = i / (halfRowSize - 1f)  // Normalize index to [0, 1] for the first half
            val middleWeight = fastStartCurve(t, 8f, maxMiddleWeight)
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
        Column(modifier = modifier) {
            Row(modifier = Modifier.weight(1f)) {
                Spacer(modifier = Modifier.weight(totalSpacingWeight / 2f))
                RoleCard2(
                    number = edgeWeights.size,
                    modifier = Modifier.weight(singleCardWeight)
                )
                Spacer(modifier = Modifier.weight(totalSpacingWeight / 2f))
            }
            for (i in 0 until playerSize) {
                RowRoleCards(
                    edgeWeight = edgeWeights[i],
                    middleWeight = middleWeights[i],
                    singleCardWeight = singleCardWeight,
                    modifier = Modifier.weight(1f)
                )
            }
            Row(modifier = Modifier.weight(1f)) {
                Spacer(modifier = Modifier.weight(totalSpacingWeight / 2f))
                RoleCard2(
                    number = edgeWeights.size,
                    modifier = Modifier.weight(singleCardWeight)
                )
                Spacer(modifier = Modifier.weight(totalSpacingWeight / 2f))
            }
        }
    }

    @Preview(showBackground = true)
    @Composable
    fun VillageScreen5Preview() {
        VillageScreen5(
            playerSize = 14,
            modifier = Modifier
                .fillMaxSize()
                .padding(dimensionResource(id = R.dimen.padding_small))
        )
    }

    @Composable
    fun RowRoleCards(
        edgeWeight: Float,
        middleWeight: Float,
        singleCardWeight: Float,
        modifier: Modifier = Modifier
    ) {
        Row(modifier = modifier) {
            Spacer(modifier = Modifier.weight(edgeWeight))
            RoleCard2(number = edgeWeight.toInt(), modifier = Modifier.weight(singleCardWeight))
            Spacer(modifier = Modifier.weight(middleWeight))
            RoleCard2(number = middleWeight.toInt(), modifier = Modifier.weight(singleCardWeight))
            Spacer(modifier = Modifier.weight(edgeWeight))
        }
    }

    @Composable
    fun RoleCard2(number: Int = 1, modifier: Modifier) {
        RoleCard(
            role = Role.MEDIUM,
            playerRoleNumber = number,
            isSelected = true,
            onRoleSelected = {},
            modifier = modifier
        )
    }

    @Composable
    fun VillageScreen5(
        players: List<Player>,
        modifier: Modifier = Modifier
    ) {
        val rowPlayers = players.subList(1, players.size - 1)
        val isOdd = players.size % 2 == 1

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
        //Split the rowSize into two halves
        //val halfRowSize = ceil(numRows / 2f).toInt()
        val halfRowSize = numRows / 2

        val middleIndex = when {
            numRows % 2 == 1 -> halfRowSize  // Middle row for odd rowSize
            else -> -1  // No middle row for even rowSize
        }

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

        Column(modifier = modifier) {
            Row(modifier = Modifier.weight(1f)) {
                Spacer(modifier = Modifier.weight(totalSpacingWeight / 2f))
                //RoleCard2(number = middleIndex, modifier = Modifier.weight(singleCardWeight))
                PlayerCard(
                    player = players.first(),
                    onClick = {  },
                    modifier = Modifier.weight(singleCardWeight)
                )
                Spacer(modifier = Modifier.weight(totalSpacingWeight / 2f))
            }
            for (i in 0 until numRows) {
                RowPlayerCards(
                    onClick = {},
                    firstPlayer = rowPlayers[i * 2],
                    secondPlayer = rowPlayers[i * 2 + 1],
                    edgeWeight = edgeWeights[i],
                    middleWeight = middleWeights[i],
                    singleCardWeight = singleCardWeight,
                    modifier = Modifier.weight(1f)
                )
            }
            Row(modifier = Modifier.weight(1f)) {
                Spacer(modifier = Modifier.weight(totalSpacingWeight / 2f))
                if (isOdd) {
                    PlayerCard(
                        player = players[players.size - 2],
                        onClick = {  },
                        modifier = Modifier.weight(singleCardWeight)
                    )
                    Spacer(modifier = Modifier.weight(edgeWeights[numRows - 1] * 0.3f))
                }
                PlayerCard(
                    player = players[players.size - 1],
                    onClick = {  },
                    modifier = Modifier.weight(singleCardWeight)
                )
                Spacer(modifier = Modifier.weight(totalSpacingWeight / 2f))
            }
        }
    }

    @Composable
    fun RowPlayerCards(
        onClick: () -> Unit,
        firstPlayer: Player,
        secondPlayer: Player,
        edgeWeight: Float,
        middleWeight: Float,
        singleCardWeight: Float,
        modifier: Modifier = Modifier
    ) {
        Row(modifier = modifier) {
            Spacer(modifier = Modifier.weight(edgeWeight))
            PlayerCard(player = firstPlayer, onClick = {onClick()}, modifier = Modifier.weight(singleCardWeight) )
            Spacer(modifier = Modifier.weight(middleWeight))
            PlayerCard(player = secondPlayer, onClick = {onClick()}, modifier = Modifier.weight(singleCardWeight) )
            Spacer(modifier = Modifier.weight(edgeWeight))
        }
    }

    @Composable
    fun PlayerCard(player: Player, onClick: () -> Unit, modifier: Modifier = Modifier) {
        OutlinedCard(
            onClick = { onClick() },
            modifier = modifier.padding(vertical = 1.dp)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxSize()
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxSize()
                        .padding(2.dp)
                ) {
                    Text(
                        text = player.name,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .weight(2f)
                            .fillMaxWidth()
                    )
                    Image(
                        painter = painterResource(id = player.role.image),
                        contentDescription = null,
                        modifier = Modifier
                            .padding(1.dp)
                            .weight(1f)
                    )
                }
                Image(
                    painter = player.imageSource.getPainter(),
                    contentDescription = player.name,
                    modifier = Modifier
                        .padding(1.dp)
                        .weight(3f)
                        .fillMaxSize()
                )
            }
        }
    }

    // Assume the cubic function is parameterized for simplicity
    fun cubicCurve(t: Float): Float {
        // Cubic function coefficients, you may need to adjust
        val a = 96  // this affects the steepness of the curve
        val b = -144
        val c = 104
        val d = 8   // Starting middle weight
        return a * t.pow(3) + b * t.pow(2) + c * t + d
    }

    // Scaled cubic function that ensures weights fit the desired bounds
    fun scaledCubicCurveFastToSlow(t: Float, minValue: Float, maxValue: Float): Float {
        // Scaled cubic curve: ranges from min_value to max_value over t in [0, 1]
        return minValue + (maxValue - minValue) * (3 * t.pow(2) - 2 * t.pow(3))  // S-shape cubic
    }

    // Scaled cubic function that ensures weights fit the desired bounds
    fun scaledCubicCurveSlowToFast(t: Float, minValue: Float, maxValue: Float): Float {
        // Scaled cubic curve: ranges from min_value to max_value over t in [0, 1]
        return minValue + (maxValue - minValue) * (t.pow(3))  // S-shape cubic
    }

    // Fast start using square root of t (t^0.5)
    private fun fastStartCurve(t: Float, minValue: Float, maxValue: Float): Float {
        // Fast start curve using square root: ranges from min_value to max_value over t in [0, 1]
        return minValue + (maxValue - minValue) * (t.pow(0.2f))
    }
}