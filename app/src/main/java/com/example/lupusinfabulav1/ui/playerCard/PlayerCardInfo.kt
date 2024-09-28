package com.example.lupusinfabulav1.ui.playerCard

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.lupusinfabulav1.R
import com.example.lupusinfabulav1.data.PlayersRepository
import com.example.lupusinfabulav1.model.Player
import com.example.lupusinfabulav1.model.getPainter

@Composable
fun PlayerCardInfo(
    player: Player,
    modifier: Modifier = Modifier,
    backgroundColor: Color = Color.White,
    borderColor: Color = Color.Black,
) {
    OutlinedCard(
        border = BorderStroke(dimensionResource(id = R.dimen.border_width_medium), borderColor),
        modifier = modifier
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxSize()
                .background(backgroundColor)
                .padding(dimensionResource(id = R.dimen.padding_medium))
        ) {
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(dimensionResource(id = R.dimen.padding_medium))
            ) {
                Text(
                    text = player.name,
                    maxLines = 1,
                    modifier = Modifier
                        .weight(3f)
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
                    .weight(4f)
                    .padding(dimensionResource(id = R.dimen.padding_small))
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PlayerCardInfoPreview(){
    PlayerCardInfo(
        player = PlayersRepository.players[0],
        modifier = Modifier
            .padding(8.dp)
    )
}