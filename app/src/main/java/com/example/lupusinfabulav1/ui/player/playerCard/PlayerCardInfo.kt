package com.example.lupusinfabulav1.ui.player.playerCard

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.lupusinfabulav1.R
import com.example.lupusinfabulav1.data.fake.FakePlayersRepository
import com.example.lupusinfabulav1.model.PlayerDetails
import com.example.lupusinfabulav1.model.getPainter

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun PlayerCardInfo(
    playerDetails: PlayerDetails,
    modifier: Modifier = Modifier,
    onCardClick: () -> Unit = {},
    onCardLongClick: () -> Unit = {},
    showRoleIcon: Boolean = false,
    backgroundColor: Color = Color.White,
    borderColor: Color = Color.Black,
) {
    OutlinedCard(
        border = BorderStroke(dimensionResource(id = R.dimen.border_width_medium), borderColor),
        modifier = modifier
            .combinedClickable(
                onClick = { onCardClick() },
                onLongClick = { onCardLongClick() }
            )
    ) {
        Row(
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxSize()
                .background(backgroundColor)
                .padding(dimensionResource(id = R.dimen.padding_medium))

        ) {
            Image(
                painter = playerDetails.imageSource.getPainter(),
                contentDescription = playerDetails.name,
                modifier = Modifier
                    .weight(1f)
                    .padding(dimensionResource(id = R.dimen.padding_small))
            )
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(dimensionResource(id = R.dimen.padding_small))
            ) {
                Text(
                    text = playerDetails.name,
                    maxLines = 1,
                    modifier = Modifier
                        .padding(dimensionResource(id = R.dimen.padding_small))
                        //.weight(3f)
                )
                if(showRoleIcon) {
                    Image(
                        painter = painterResource(id = playerDetails.role.image),
                        contentDescription = null,
                        modifier = Modifier
                            .size(dimensionResource(id = R.dimen.image_medium) )
                            .padding(dimensionResource(id = R.dimen.padding_small) )
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PlayerCardInfoPreview(){
    PlayerCardInfo(
        playerDetails = FakePlayersRepository.playerDetails[0],
        showRoleIcon = true,
        modifier = Modifier
            .height(160.dp)
            .fillMaxWidth()
            .padding(8.dp)
    )
}