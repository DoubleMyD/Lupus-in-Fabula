package com.example.lupusinfabulav1.ui.playerCard

import androidx.compose.foundation.Image
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.lupusinfabulav1.model.PlayerDetails
import com.example.lupusinfabulav1.model.getPainter

@Composable
fun PlayerCardVertical(
    playerDetails: PlayerDetails,
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
                text = playerDetails.name,
                textAlign = TextAlign.Center,
                maxLines = 1,
                modifier = Modifier
                    .weight(4f)
                    .basicMarquee(velocity = 10.dp, repeatDelayMillis = 1000 * 10)
            )
            Image(
                painter = painterResource(id = playerDetails.role.image),
                contentDescription = null,
                modifier = Modifier
                    .weight(1f)
            )
        }
        Image(
            painter = playerDetails.imageSource.getPainter(),
            contentDescription = playerDetails.name,
            modifier = Modifier
                .weight(3f)
                .padding(vertical = 4.dp)
        )
    }
}