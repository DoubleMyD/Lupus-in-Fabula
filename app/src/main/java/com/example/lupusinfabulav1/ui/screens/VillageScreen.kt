package com.example.lupusinfabulav1.ui.screens

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.lupusinfabulav1.R
import com.example.lupusinfabulav1.data.FakePlayersRepository
import com.example.lupusinfabulav1.model.PlayerDetails
import com.example.lupusinfabulav1.ui.VillageUiState
import com.example.lupusinfabulav1.ui.playerCard.PlayerCardInfo

//private const val TAG = "VillageScreen"


@Composable
fun VillageScreen7(
    uiState: VillageUiState,
    onPlayerTap: (voter: PlayerDetails, voted: PlayerDetails) -> Unit,
    onCenterIconTap: () -> Unit,
    onCenterIconLongPress: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Remember the state of the dialog
    var playerDetailsToShowInfo by remember { mutableStateOf<PlayerDetails?>(null) }
    val onPlayerLongPress = { playerDetails: PlayerDetails -> playerDetailsToShowInfo = playerDetails }

    // Show player info dialog
    playerDetailsToShowInfo?.let { player ->
        PlayerInfoDialog(
            { playerDetailsToShowInfo = null },
            playerDetails = player
        )
    }
    
    val pageState = rememberPagerState {2}
    HorizontalPager(
        state = pageState,
        modifier = modifier
    ) { page ->
        when (page) {
            0 -> VillageContent(
                uiState = uiState,
                onPlayerTap = onPlayerTap,
                onCenterIconClick = onCenterIconTap,
                onCenterIconLongPress = onCenterIconLongPress,
                onPlayerLongPress = onPlayerLongPress,
                modifier = Modifier.fillMaxSize()
            )
            1 -> PlayersListScreen(playerDetails = uiState.playerDetails)
        }
    }
}

@Composable
private fun PlayerInfoDialog(onDismiss: () -> Unit, playerDetails: PlayerDetails) {
    Dialog(
        onDismissRequest = { onDismiss() },
        properties = DialogProperties(dismissOnBackPress = true, dismissOnClickOutside = true)
    ) {
        PlayerCardInfo(
            playerDetails = playerDetails,
            backgroundColor = playerDetails.role.color.copy(alpha = 0.1f),
            modifier = Modifier
                .size(400.dp)
                .padding(dimensionResource(id = R.dimen.padding_medium))
        )
    }
}


@Preview(showBackground = true)
@Composable
fun VillageScreen7Preview() {
    VillageScreen7(
        onPlayerTap = { _, _ -> },
        onCenterIconTap = {},
        onCenterIconLongPress = {},
        uiState = VillageUiState().copy(playerDetails = FakePlayersRepository.playerDetails),
        modifier = Modifier
            .fillMaxSize()
            .padding(dimensionResource(id = R.dimen.padding_small))
    )
}

@Preview(showBackground = true)
@Composable
fun VillageScreenContentPreview() {
    VillageContent(
        onPlayerTap = { _, _ -> },
        onCenterIconClick = {},
        onPlayerLongPress = {},
        onCenterIconLongPress = {},
        uiState = VillageUiState().copy(playerDetails = FakePlayersRepository.playerDetails),
        modifier = Modifier
            .fillMaxSize()
            .padding(dimensionResource(id = R.dimen.padding_small))
    )
}



