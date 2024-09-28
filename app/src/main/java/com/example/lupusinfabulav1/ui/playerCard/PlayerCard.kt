package com.example.lupusinfabulav1.ui.playerCard

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.OutlinedCard
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.lupusinfabulav1.model.Player

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun PlayerCard(
    border: BorderStroke,
    player: Player,
    onPlayerTap: () -> Unit,
    onPlayerLongPress: () -> Unit,
    modifier: Modifier = Modifier,
    orientation: Orientation = Orientation.Horizontal,
    alphaColor: Float = 0.1f,
    ) {
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



