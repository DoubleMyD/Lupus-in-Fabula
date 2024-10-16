package com.example.lupusinfabulav1.ui.playersList

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.lupusinfabulav1.model.PlayerDetails
import com.example.lupusinfabulav1.ui.player.playerCard.PlayerCardInfo

@Composable
fun PlayersListContent(
    playersDetails: List<PlayerDetails>,
    modifier: Modifier = Modifier,
    cardModifier: Modifier = Modifier.height(224.dp).fillMaxWidth(),
    showRoleIcon: Boolean = false,
    onPlayerClick: (PlayerDetails) -> Unit = {},
    onPlayerLongClick: (PlayerDetails) -> Unit = {},
    cardBackgroundColor: (PlayerDetails) -> Color = { Color.White },
    playerCardAlpha: (PlayerDetails) -> Float = { 1f },
) {
    LazyColumn(
        modifier = modifier
            .background(Color.Cyan)
    ) {
        items(playersDetails) { playerDetails ->
            val alpha = playerCardAlpha(playerDetails)
            PlayerCardInfo(
                playerDetails = playerDetails,
                onCardClick = { onPlayerClick(playerDetails) },
                onCardLongClick = { onPlayerLongClick(playerDetails) },
                showRoleIcon = showRoleIcon,
                backgroundColor = cardBackgroundColor(playerDetails),
                modifier = cardModifier.alpha(alpha)
                //.height(224.dp)
                //.fillMaxWidth()
                //.padding(dimensionResource(id = R.dimen.padding_very_small))
            )
        }
    }
}