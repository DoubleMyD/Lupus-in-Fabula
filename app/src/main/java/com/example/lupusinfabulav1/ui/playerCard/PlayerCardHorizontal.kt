package com.example.lupusinfabulav1.ui.playerCard

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CardColors
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.lupusinfabulav1.model.Player
import com.example.lupusinfabulav1.model.Role
import com.example.lupusinfabulav1.model.getPainter
import kotlin.math.ceil

@Composable
fun PlayerCardHorizontal(
    player: Player,
    rolesVotedBy: List<Role>,
    modifier: Modifier = Modifier,
    votedCount: Int = 0,
) {
    val roles = remember { Role.entries }
    val rowSize = ceil(roles.size / 2.0).toInt()

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
                    .size((32 * 10).dp)
                    .weight(1f)
            )
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = when (votedCount) {
                        0 -> ""
                        else -> votedCount.toString()
                    }
                )
            }
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxSize()
            ) {
                for (i in 0 until rowSize) {
                    Row(
                        horizontalArrangement = Arrangement.Center,
                        modifier = Modifier
                            .weight(1f)
                    ) {
                        for (j in 0..1) {
                            val index = i * 2 + j
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                            ) {
                                if (index < roles.size) {
                                    if (rolesVotedBy.contains(roles[index])) {
                                        OutlinedCard(
                                            colors = CardColors(
                                                containerColor = Color.Transparent,
                                                disabledContainerColor = Color.Transparent,
                                                disabledContentColor = Color.Transparent,
                                                contentColor = Color.Transparent
                                            )
                                        ) {
                                            Image(
                                                painter = painterResource(id = roles[index].image),
                                                contentDescription = null,
                                                modifier = Modifier
                                                    .size(32.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
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
        votedCount = 10
    )
}