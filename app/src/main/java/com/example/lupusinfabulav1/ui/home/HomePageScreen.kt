package com.example.lupusinfabulav1.ui.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.lupusinfabulav1.R
import com.example.lupusinfabulav1.ui.LupusInFabulaScreen
import com.example.lupusinfabulav1.ui.commonui.LupusInFabulaAppBar
import com.example.lupusinfabulav1.ui.navigation.GameScreen
import com.example.lupusinfabulav1.ui.navigation.PlayerScreen
import com.example.lupusinfabulav1.ui.navigation.PlayersListScreen

@Composable
fun HomePageScreen(
    onNavigateToPlayers: () -> Unit,
    onNavigateToPlayersLists: () -> Unit,
    onNavigateToGame: () -> Unit,
    //onNavigateToPlayersList: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        topBar = {
            LupusInFabulaAppBar(
                title = LupusInFabulaScreen.HOME_PAGE.title,
                canNavigateBack = false
            )
        },
        modifier = modifier
    ) { innerPadding ->
        Column(
            modifier = Modifier.padding(innerPadding),
        ) {
            val modifierColumn =
                Modifier
                    .padding(dimensionResource(id = R.dimen.padding_small))
                    .weight(1f)

            ScreenCard(
                modifier = modifierColumn,
                name = PlayerScreen.PLAYERS.name,
                onClick = { onNavigateToPlayers() }
            )
            ScreenCard(
                modifier = modifierColumn,
                name = PlayersListScreen.PLAYERS_LISTS.name,
                onClick = { onNavigateToPlayersLists() }
            )
            ScreenCard(
                modifier = modifierColumn,
                name = GameScreen.VILLAGE.name,
                onClick = { onNavigateToGame() }
            )

//            ScreenCard(
//                modifier = modifierColumn,
//                name = LupusInFabulaScreen.PLAYERS_FOR_ROLE.name,
//                onClick = { onNavigateToPlayersForRole() })
//            ScreenCard(
//                modifier = modifierColumn,
//                name = LupusInFabulaScreen.NEW_PLAYER.name,
//                onClick = { onNavigateToNewPlayer() })
//            ScreenCard(
//                modifier = modifierColumn,
//                name = LupusInFabulaScreen.VILLAGE.name,
//                onClick = { onNavigateToVillage() })
//            ScreenCard(
//                modifier = modifierColumn,
//                name = LupusInFabulaScreen.PLAYERS_LISTS.name,
//                onClick = { onNavigateToPlayersList() })
        }
    }
}

@Composable
fun ScreenCard(
    name: String,
    onClick: () -> Unit,
    //imageRes: Int,
    modifier: Modifier = Modifier,
){
    Card(
        modifier = modifier,
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ){
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth()
        ) {
            Image(
                modifier = Modifier
                    .padding(dimensionResource(id = R.dimen.padding_small))
                    .weight(2f),
                painter = painterResource(id = R.drawable.android_superhero1), contentDescription = null)
            Button(onClick = onClick,
                modifier = Modifier
                    .padding(dimensionResource(id = R.dimen.padding_small))
                    .weight(1f)) {
                Text(text = name)
            }
        }
    }
}