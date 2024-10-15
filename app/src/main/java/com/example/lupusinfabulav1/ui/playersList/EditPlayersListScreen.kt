package com.example.lupusinfabulav1.ui.playersList

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import com.example.lupusinfabulav1.R
import com.example.lupusinfabulav1.ui.LupusInFabulaScreen
import com.example.lupusinfabulav1.ui.commonui.LupusInFabulaAppBar
import com.example.lupusinfabulav1.ui.navigation.NavigationDestination

object EditListDestination : NavigationDestination {
    override val route = "Players List"
    override val titleRes = R.string.app_name
    const val listIdArg = "listId"
    val routeWithArgs = "$route/{$listIdArg}"

    // Function to generate route with argument
    fun createRoute(listId: String): String = "$route/$listId"
}

@Composable
fun EditPlayerScreen(
    navigateBack: () -> Unit,
    navigateUp: () -> Unit,
    uiState: EditListUiState,
    modifier: Modifier = Modifier,
){
    Scaffold(
        topBar = {
            LupusInFabulaAppBar(
                title = LupusInFabulaScreen.VILLAGE.title,
                canNavigateBack = true,
                navigateUp = navigateUp
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    navigateUp()
                },
                shape = MaterialTheme.shapes.medium,
                modifier = Modifier
                    .padding(
                        end = WindowInsets.safeDrawing.asPaddingValues()
                            .calculateEndPadding(LocalLayoutDirection.current)
                    )
            ) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = null
                )
            }
        },
        modifier = modifier
    ) { innerPadding ->
        PlayersListContent(
            playersDetails = uiState.playersDetails,
            modifier = Modifier
                .padding(innerPadding)
        )
    }
}