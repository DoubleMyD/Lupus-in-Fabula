package com.example.lupusinfabulav1.ui.game

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.lupusinfabulav1.model.PlayerManager
import com.example.lupusinfabulav1.model.Randomizer
import com.example.lupusinfabulav1.model.Role
import com.example.lupusinfabulav1.model.ValidRangeManager
import com.example.lupusinfabulav1.ui.PlayersForRoleUiState
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

//private const val TAG = "PlayersForRoleViewModel"

// Define possible UI events, such as showing a message
sealed class PlayersForRoleEvent {
    data object ErrorNotAllPlayersSelected : PlayersForRoleEvent()
    data object ErrorTooManyPlayersSelected : PlayersForRoleEvent()
}

class PlayersForRoleViewModel(
    private val playerManager: PlayerManager,
    private val validRangeManager: ValidRangeManager,
    private val randomizer: Randomizer,
) : ViewModel() {
    private val _uiState = MutableStateFlow(PlayersForRoleUiState())
    val uiState: StateFlow<PlayersForRoleUiState> = _uiState.asStateFlow()

    // Event channel for UI interactions like Toasts or Dialogs
    private val _uiEvent = MutableSharedFlow<PlayersForRoleEvent>()
    val uiEvent: SharedFlow<PlayersForRoleEvent> = _uiEvent.asSharedFlow()

    private val idealDistribution = mapOf(
        Role.ASSASSINO to 3,
        Role.MEDIUM to 1,
        Role.FACILI_COSTUMI to 2,
        Role.CUPIDO to 1,
        Role.VEGGENTE to 1,
        Role.CITTADINO to 8
    )

    init {
        viewModelScope.launch {
            playerManager.players.collect { players ->
                _uiState.value = _uiState.value.copy(playersSize = players.size)
                updateRemainingPlayers()
            }
        }
    }

    fun assignRoleToPlayers() {
        playerManager.assignRoleToPlayers(_uiState.value.playersForRole)
    }

    fun checkIfAllPlayersSelected(): Boolean {
        val isAllSelected = _uiState.value.playersForRole.values.sum() == _uiState.value.playersSize

        if (!isAllSelected) {
            // Trigger event to notify that players are missing
            emitEvent(PlayersForRoleEvent.ErrorNotAllPlayersSelected)
        }

        return isAllSelected
    }

    private fun emitEvent(event: PlayersForRoleEvent) {
        viewModelScope.launch {
            _uiEvent.emit(event)
        }
    }

    /**
     * update the slider value and the playersForRole map
     */
    /*fun checkAndUpdateSliderValue(newValue: Float) {
        val validValue = newValue.coerceIn(_uiState.value.minAllowedValue.toFloat(), _uiState.value.maxAllowedValue.toFloat())

        if(newValue.toInt() > _uiState.value.maxAllowedValue) {
            viewModelScope.launch {
                _uiEvent.emit(PlayersForRoleEvent.ErrorTooManyPlayersSelected)
            }
        }

        updateSliderValue(validValue)
    }*/

    fun updateSliderValue(newValue: Float) {
        //val validValue = validRangeManager.getValidValue(newValue)
        _uiState.update { currentState ->
            currentState.copy(
                sliderValue = newValue, //newValue,
            )
        }

        updatePlayersForRole(_uiState.value.currentRole, newValue.toInt())//newValue.toInt())
        updateMaxAndMinAllowedValue()
        updateRemainingPlayers()
    }

    private fun updateRemainingPlayers() {
        val newValue = _uiState.value.playersSize - _uiState.value.playersForRole.values.sum()

        _uiState.update { currentState ->
            currentState.copy(
                remainingPlayers = newValue,
            )
        }
    }

    private fun updateMaxAndMinAllowedValue() {
        validRangeManager.updateMaxAndMinAllowedValue(
            maxSize = _uiState.value.playersSize,
            selectedValues = _uiState.value.playersForRole.values.sum(),
            currentValue = _uiState.value.sliderValue.toInt(),
        )
    }

    /*private fun updateMaxAndMinAllowedValue() {
        _uiState.update { currentState ->
            val currentSelectedPlayer = currentState.playersForRole.values.sum()
            val remainPlayers = abs(_uiState.value.playersSize - currentSelectedPlayer)
            val maxAllowedValue = (remainPlayers + currentState.sliderValue.toInt()).coerceAtMost(currentState.finishRange)

            currentState.copy(
                minAllowedValue = 1,
                maxAllowedValue = maxAllowedValue,
            )
        }
    }*/

    /**
     * select a random value for the slider between the range of minAllowedValue and MaxAllowedValue
     */
    fun onRandomNumberClick() {
        val randomValue =
            (validRangeManager.validRangeState.value.minAllowedValue..validRangeManager.validRangeState.value.maxAllowedValue).random()
        //checkAndUpdateSliderValue(randomValue.toFloat())
        val validValue = getValidValue(randomValue.toFloat())
        updateSliderValue(validValue)
    }


    fun onRandomizeAllClick() {
        val updatedPlayersForRole = randomizer.randomize(
            _uiState.value.playersForRole,
            remainingPlaces = _uiState.value.playersSize,
            idealDistribution = idealDistribution, // Pass your ideal distribution map here
            maxValue = validRangeManager.validRangeState.value.finishRange
        )

        updatePlayersForRole(updatedPlayersForRole)
        updateMaxAndMinAllowedValue()
        updateSliderValue(updatedPlayersForRole[_uiState.value.currentRole]?.toFloat() ?: 0f)
    }

    /*fun onRandomizeAllClick() {
        val updatedPlayersForRole = _uiState.value.playersForRole.toMutableMap()
        var remainingPlayers = _uiState.value.playersSize// Total number of players

        // Ideal player count per role
        val idealDistribution = mapOf(
            Role.ASSASSINO to 3,
            Role.MEDIUM to 1,
            Role.FACILI_COSTUMI to 2,
            Role.CUPIDO to 1,
            Role.VEGGENTE to 1,
            Role.CITTADINO to 8
        )

        // Calculate total weight (sum of ideal numbers)
        val totalWeight = idealDistribution.values.sum()

        // Weights map to store the relative weight (i.e., likelihood) of each role being selected
        val weights = idealDistribution.mapValues { (_, idealCount) ->
            idealCount.toFloat() / totalWeight // Normalized weight for each role
        }

        val finishRange = validRangeManager.validRangeState.value.finishRange

        // Assign players based on weighted random distribution
        updatedPlayersForRole.forEach { (role, _) ->
            if (remainingPlayers <= 0) return//@forEach // Stop if no players are left

            val idealValue = idealDistribution[role] ?: 1 // Fallback to 1 if role isn't specified

            // Maximum allowed players for the role is bounded by remaining players and the finish range
            val maxAllowedValue = minOf(remainingPlayers, idealValue + 2, finishRange)

            // Calculate the weighted random value
            val randomValue = getWeightedRandomValue(role, maxAllowedValue, weights, remainingPlayers)

            updatedPlayersForRole[role] = randomValue
            remainingPlayers -= randomValue // Decrease remaining players count
        }

        // If remaining players are more than zero, distribute them
        if (remainingPlayers > 0) {
            val completedPlayersForRole = distributeRemainingPlayers(updatedPlayersForRole, remainingPlayers)
            updatedPlayersForRole.putAll(completedPlayersForRole)
        }

        updatePlayersForRole(updatedPlayersForRole)
        updateMaxAndMinAllowedValue()
        updateSliderValue(updatedPlayersForRole[_uiState.value.currentRole]!!.toFloat())
    }

    // Helper function to calculate a weighted random value biased towards the ideal value
    private fun getWeightedRandomValue(
        role: Role,
        maxAllowedValue: Int,
        weights: Map<Role, Float>,
        remainingPlayers: Int
    ): Int {
        // Calculate weighted range for the role
        val weight = weights[role] ?: 1.0f
        val weightedMax = (weight * remainingPlayers).toInt().coerceIn(1, maxAllowedValue)

        // Return a random number within the weighted range
        return (1..weightedMax).random()
    }

    // Function to distribute remaining players in case they are left after initial assignment
    private fun distributeRemainingPlayers(
        playersForRole: Map<Role, Int>, remainingPlayers: Int,
    ): Map<Role, Int> {
        var remaining = remainingPlayers
        val roles = playersForRole.keys.shuffled() // Randomize role selection for distribution
        val updatedPlayersForRole = playersForRole.toMutableMap()

        while (remaining > 0) {
            for (role in roles) {
                if (remaining <= 0) break
                val currentCount = updatedPlayersForRole[role] ?: continue
                updatedPlayersForRole[role] = currentCount + 1
                remaining--
            }
        }
        return updatedPlayersForRole
    }*/

    fun updateCurrentRole(selectedRole: Role) {
        _uiState.update { currentState -> currentState.copy(currentRole = selectedRole) }
        val currentPlayersForRole = _uiState.value.playersForRole[selectedRole] ?: 1f

        val validValue = getValidValue(currentPlayersForRole.toFloat())
        updateSliderValue(validValue)
    }

    private fun updatePlayersForRole(role: Role, newValue: Int) {
        val updatedPlayersForRole = _uiState.value.playersForRole.toMutableMap()
        updatedPlayersForRole[role] = newValue
        _uiState.update { currentState ->
            currentState.copy(playersForRole = updatedPlayersForRole)
        }
    }

    private fun updatePlayersForRole(updatedPlayersForRole: Map<Role, Int>) {
        _uiState.update { currentState ->
            currentState.copy(playersForRole = updatedPlayersForRole)
        }
    }

    fun getValidValue(newValue: Float): Float {
        return validRangeManager.getValidValue(newValue)
    }
}