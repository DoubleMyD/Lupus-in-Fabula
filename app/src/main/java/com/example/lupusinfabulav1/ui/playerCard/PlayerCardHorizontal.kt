package com.example.lupusinfabulav1.ui.playerCard

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.lupusinfabulav1.model.Player
import com.example.lupusinfabulav1.model.getPainter

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