package com.example.lupusinfabulav1.ui.playerCard

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.lupusinfabulav1.R
import com.example.lupusinfabulav1.data.FakePlayersRepository
import com.example.lupusinfabulav1.model.PlayerDetails
import com.example.lupusinfabulav1.model.getPainter

@Composable
fun PlayerCardInfo(
    playerDetails: PlayerDetails,
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
                    text = playerDetails.name,
                    maxLines = 1,
                    modifier = Modifier
                        .weight(3f)
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
        playerDetails = FakePlayersRepository.playerDetails[0],
        modifier = Modifier
            .padding(8.dp)
    )
}