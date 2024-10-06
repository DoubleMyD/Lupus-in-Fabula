package com.example.lupusinfabulav1.ui.playerCard

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.lupusinfabulav1.R
import com.example.lupusinfabulav1.model.Player
import com.example.lupusinfabulav1.model.Role
import com.example.lupusinfabulav1.model.getPainter
import kotlin.enums.EnumEntries

@Composable
fun PlayerCardHorizontal(
    player: Player,
    rolesVotedBy: List<Role>,
    modifier: Modifier = Modifier,
    votedCount: Int = 0,
) {
    val roles = remember { Role.entries }
    val rowSize = remember(roles) { (roles.size + 1) / 2 }

    // Cache dimension resources to avoid recomputation
    val imageVeryBigSize = dimensionResource(id = R.dimen.image_very_big)
    val smallPadding = dimensionResource(id = R.dimen.padding_small)

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
                .padding(smallPadding)
                .fillMaxSize()
        ) {
            Image(
                painter = painterResource(id = player.role.image),
                contentDescription = null,
                modifier = Modifier
                    .size(imageVeryBigSize)
                    .weight(1f)
            )
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.weight(1f)
            ) {
                if (votedCount > 0) {
                    Text(text = votedCount.toString())
                }
            }
            RoleIconCards(
                rowSize = rowSize,
                roles = roles,
                rolesVotedBy = rolesVotedBy,
                modifier = Modifier.weight(1f).fillMaxSize()
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

@Composable
private fun RoleIconCards(
    rowSize: Int,
    roles: EnumEntries<Role>,
    rolesVotedBy: List<Role>,
    modifier: Modifier = Modifier
) {
    val imageSize = dimensionResource(id = R.dimen.image_medium)

    // Cache roles in voted list to minimize recomposition
    val rolesVotedBySet = remember(rolesVotedBy) { rolesVotedBy.toSet() }

    Column(modifier = modifier) {
        for (i in 0 until rowSize) {
            Row(
                horizontalArrangement = Arrangement.Center,
                modifier = modifier
            ) {
                // Loop for each role in a row (2 items per row)
                for (j in 0..1) {
                    val index = i * 2 + j
                    if (index < roles.size) {
                        val role = roles[index]

                        Box(
                            modifier = Modifier.weight(1f)
                        ) {
                            // Check if the current role is in the voted roles
                            if (rolesVotedBySet.contains(role)) {
                                OutlinedCard(
                                    colors = CardDefaults.outlinedCardColors(
                                        containerColor = Color.Transparent,
                                        disabledContainerColor = Color.Transparent,
                                        disabledContentColor = Color.Transparent,
                                        contentColor = Color.Transparent
                                    )
                                ) {
                                    Image(
                                        painter = painterResource(id = role.image),
                                        contentDescription = null,
                                        modifier = Modifier.sizeIn(imageSize)
                                    )
                                }
                            } else {
                                // Placeholder for roles not voted for
                                Spacer(modifier = Modifier.sizeIn(imageSize))
                            }
                        }
                    } else {
                        // Empty box for out-of-bound roles
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun PlayerCardHorizontalPreview(){
    PlayerCard(
        border = BorderStroke(2.dp, Color.Black),
        player = Player("ciao"),
        modifier = Modifier
            .size(200.dp)
            .padding(4.dp),
        onPlayerLongPress = {},
        onPlayerTap = {},
        rolesVotedBy = Role.entries,
        votedCount = 10,
        alphaColor = 0.1f
    )
}