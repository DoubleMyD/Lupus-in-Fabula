package com.example.lupusinfabulav1.ui.playerCard

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.OutlinedCard
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import com.example.lupusinfabulav1.model.PlayerDetails
import com.example.lupusinfabulav1.model.Role

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun PlayerCard(
    border: BorderStroke,
    playerDetails: PlayerDetails,
    rolesVotedBy: List<Role>,
    votedCount: Int,
    alphaColor: Float,
    onPlayerTap: () -> Unit,
    onPlayerLongPress: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val backgroundColor = playerDetails.role.color.copy(alpha = alphaColor)
    val cardModifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)


    val alphaCard = remember(key1 = playerDetails.alive) { if (playerDetails.alive) 1f else 0.5f }

    OutlinedCard(
        border = border,
        modifier = modifier
            .combinedClickable(
                onClick = { if (playerDetails.alive) onPlayerTap() },
                onLongClick = { onPlayerLongPress() }
            )
            .alpha(alphaCard)
    ) {
        PlayerCardHorizontal(
            playerDetails = playerDetails,
            rolesVotedBy = rolesVotedBy,
            votedCount = votedCount,
            modifier = cardModifier
        )
    }
}



