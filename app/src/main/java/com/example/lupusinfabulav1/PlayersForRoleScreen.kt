package com.example.lupusinfabulav1

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import com.example.lupusinfabulav1.data.PlayersForRoleUiState
import com.example.lupusinfabulav1.model.PlayersForRoleViewModel
import com.example.lupusinfabulav1.model.Role
import com.example.lupusinfabulav1.ui.commonui.CancelAndConfirmButtons

@Composable
fun PlayersForRoleScreen(
    onConfirmClick: () -> Unit,
    onCancelClick: () -> Unit,
    playerSize: Int,
    uiState: PlayersForRoleUiState,
    onSliderValueChange: (Float) -> Unit,
    onRandomNumberClick: () -> Unit,
    onRoleSelection: (Role) -> Unit,
    onRandomizeAllClick: () -> Unit,
    modifier : Modifier = Modifier,
    //viewModel: PlayersForRoleViewModel = viewModel(),
){
    Column(
        modifier = modifier
            .padding(dimensionResource(id = R.dimen.padding_medium))
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceAround,
            modifier = Modifier.weight(3f)
        ) {
            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.weight(5f)
            ) {
                PlayerCountSlider(
                    currentRole = uiState.currentRole,
                    sliderValue = uiState.sliderValue,
                    onSliderValueChange = { newValue -> onSliderValueChange(newValue) },
                    onRandomNumberClick = { onRandomNumberClick() },
                    minAllowedValue = uiState.minAllowedValue,
                    maxAllowedValue = uiState.maxAllowedValue,
                    modifier = Modifier.weight(1f)
                )
            }
        }
        Spacer( modifier = Modifier.weight(1f) )
        Text(
            text = stringResource(id = R.string.total_players, playerSize),
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )
        Text(
            text = stringResource(id = R.string.remaining_players, uiState.remainingPlayers),
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )
        RoleSelectionRow(
            onRoleSelection = { selectedRole: Role -> onRoleSelection(selectedRole) },
            currentRole = uiState.currentRole,
            playersForRole = uiState.playersForRole,
            modifier = Modifier.weight(2f)
        )
        RandomizeAllButton(
            onRandomizeAllClick = { onRandomizeAllClick() },
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .weight(1f)
        )

        Spacer( modifier = Modifier.weight(1f) )
        CancelAndConfirmButtons(
            onCancelClick = onCancelClick,
            onConfirmClick = onConfirmClick,
            modifier = Modifier.weight(1f)
        )


    }
}

@Composable
fun PlayerCountSlider(
    currentRole: Role,
    sliderValue: Float,
    onSliderValueChange: (Float) -> Unit,
    onRandomNumberClick: () -> Unit,
    minAllowedValue: Int,
    maxAllowedValue: Int,
    modifier: Modifier = Modifier,
    startRange: Float = 1f,
    finishRange: Float = 10f,
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = modifier
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Bottom,
            modifier = Modifier.weight(2f)
        ) {
            Image(
                painter = painterResource(id = currentRole.image),
                contentDescription = null,
                modifier = Modifier.weight(3f)
            )
            Text(
                text = sliderValue.toString(),
                modifier = Modifier
                    .padding(dimensionResource(id = R.dimen.padding_small))
            )
        }
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(1f)
            ) {
            Text(
                text = startRange.toInt().toString(),
                textAlign = TextAlign.Center,
                modifier = Modifier.weight(1f)
            )

            Slider(
                value = sliderValue,
                onValueChange = { newValue ->
                    val validValue = newValue.coerceIn(minAllowedValue.toFloat(), maxAllowedValue.toFloat())
                    onSliderValueChange(validValue)
                },
                valueRange = startRange..finishRange,
                steps = (finishRange - startRange).toInt() - 1, // Adjust steps according to the range
                modifier = Modifier.weight(8f)
                )

            Text(
                text = finishRange.toInt().toString(),
                textAlign = TextAlign.Center,
                modifier = Modifier.weight(1f)
            )
        }
        FilledIconButton(
            onClick = { onRandomNumberClick() },
            modifier = Modifier.weight(1f)
        ) {
            Icon(
                imageVector = Icons.Default.Refresh,
                contentDescription = null,
            )
        }
    }
}

@Composable
fun RoleSelectionRow(
    onRoleSelection: (Role) -> Unit,
    currentRole: Role,
    playersForRole: Map<Role, Int>,
    modifier: Modifier = Modifier
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceAround,
        modifier = modifier
    ) {
        playersForRole.forEach { (role, number) ->
            val isSelected = role == currentRole
            val weight = if (isSelected) 3f else 1f

            RoleCard(
                playerRoleNumber = number,
                role = role,
                isSelected = isSelected,
                onRoleSelected = onRoleSelection,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(weight)
                    .padding(dimensionResource(id = R.dimen.padding_small))
            )
        }
    }
}

@Composable
fun RandomizeAllButton(
    onRandomizeAllClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = { onRandomizeAllClick() },
        modifier = modifier
            .padding(dimensionResource(id = R.dimen.padding_medium))
            .fillMaxWidth()
    ) {
        Text(text = "Randomize All")
    }
}

@Composable
fun RoleCard(
    playerRoleNumber: Int,
    role: Role,
    isSelected: Boolean,
    onRoleSelected: (Role) -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedCard(
        onClick = { onRoleSelected(role) },
        modifier = modifier
    ) {
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxSize()
        ) {
            Text(text = playerRoleNumber.toString(),
                modifier = Modifier
                    .padding(dimensionResource(id = R.dimen.padding_small))
                    //.weight(1f)
            )
            Image(
                painter = painterResource(id = role.image),
                contentDescription = null,
                alignment = Alignment.Center,
                modifier = Modifier
                    .padding(dimensionResource(id = R.dimen.padding_small))
                    .weight(2f) // Control size ratio
            )

            // Only show text if the role is selected
            if (isSelected) {
                Text(
                    text = role.name,
                    modifier = Modifier
                        .padding(dimensionResource(id = R.dimen.padding_small))
                        .weight(1f)
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PlayersForRoleScreenPreview(){
    PlayersForRoleScreen(
        onConfirmClick = {},
        onCancelClick = {},
        uiState = PlayersForRoleUiState(),
        onSliderValueChange = {},
        onRandomNumberClick = {},
        onRoleSelection = {},
        onRandomizeAllClick = {},
        playerSize = 1
    )
}