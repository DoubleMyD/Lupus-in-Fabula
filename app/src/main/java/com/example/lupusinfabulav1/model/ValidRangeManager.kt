package com.example.lupusinfabulav1.model

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class ValidRange(
    val startRange: Int = 1,
    val finishRange: Int = 10,
    val minAllowedValue: Int = 1,
    val maxAllowedValue: Int = finishRange//abs(playersSize - Role.entries.size + 1),
)

class ValidRangeManager() {
    private val _validRangeState = MutableStateFlow(ValidRange())
    val validRangeState: StateFlow<ValidRange> = _validRangeState.asStateFlow()

//    fun updateMaxAndMinAllowedValue(maxSize: Int, selectedValues: Int, currentValue: Int ) {
//        _validRangeState.update { currentState ->
//            val currentSelectedPlayer = selectedValues //currentState.playersForRole.values.sum()
//            val remainPlayers = abs( /*_uiState.value.playersSize*/ maxSize - currentSelectedPlayer)
//            val maxAllowedValue = (remainPlayers + currentValue /*currentState.sliderValue.toInt()*/ ).coerceAtMost(currentState.finishRange)
//
//            currentState.copy(
//                minAllowedValue = 1,
//                maxAllowedValue = maxAllowedValue,
//            )
//        }
//    }

    fun updateMaxAndMinAllowedValue(maxSize: Int, selectedValues: Int, currentValue: Int) {
        val remainingPlayers = maxSize - selectedValues
        val maxAllowedValue =
            (remainingPlayers + currentValue).coerceAtMost(_validRangeState.value.finishRange)

        _validRangeState.update { currentState ->
            currentState.copy(minAllowedValue = 1, maxAllowedValue = maxAllowedValue)
        }
    }

    fun checkValue(newValue: Float): Boolean {
        return newValue.toInt() <= _validRangeState.value.maxAllowedValue
    }

    fun getValidValue(newValue: Float): Float {
        return newValue.coerceIn(_validRangeState.value.minAllowedValue.toFloat(), _validRangeState.value.maxAllowedValue.toFloat())

    }
}