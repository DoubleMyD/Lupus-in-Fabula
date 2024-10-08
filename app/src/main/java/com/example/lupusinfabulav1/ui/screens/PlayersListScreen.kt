package com.example.lupusinfabulav1.ui.screens

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.lupusinfabulav1.data.FakePlayersRepository
import com.example.lupusinfabulav1.model.Player
import com.example.lupusinfabulav1.ui.playerCard.PlayerCardInfo

@Composable
fun PlayersListScreen(players: List<Player>, modifier: Modifier = Modifier){

    LazyColumn(modifier = modifier){
        items(players){ player ->
            PlayerCardInfo(player = player, modifier = Modifier.height(128.dp).fillMaxWidth())
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PlayersListScreenPreview() {
    PlayersListScreen(players = FakePlayersRepository.players)
}