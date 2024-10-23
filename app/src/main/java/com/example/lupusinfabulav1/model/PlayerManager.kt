package com.example.lupusinfabulav1.model

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class PlayerManager() {
    private val _players: MutableStateFlow<List<PlayerDetails>> = MutableStateFlow(emptyList())// FakePlayersRepository.playerDetails
    val players: StateFlow<List<PlayerDetails>> = _players.asStateFlow()

    fun initializePlayers(playerDetails: List<PlayerDetails>) {
        _players.value = playerDetails
    }
    // Use vararg to handle single or multiple players
    fun addPlayers(vararg playerDetails: PlayerDetails) {
        _players.value += playerDetails // vararg automatically works as an array, which can be added directly
    }


    fun removePlayer(playerDetails: PlayerDetails) {
        _players.value -= playerDetails
    }

    fun assignRole(playerId: Int, role: Role) {
        _players.update { players ->
            players.map { singlePlayer ->
                if (singlePlayer.id == playerId) {
                    singlePlayer.copy(role = role)
                } else {
                    singlePlayer
                }
            }
        }
    }

    fun assignRoleToPlayers(playersForRole: Map<Role, Int>) {
        // Shuffle the list of player IDs to randomize the order
        val shuffledPlayers = players.value.shuffled()

        // Track the current index in the shuffled list
        var currentIndex = 0

        // Iterate through the roles and assign the correct count of players for each role
        playersForRole.forEach { (role, count) ->
            repeat(count) {
                // Assign role to the player at the current index in the shuffled list
                val playerId = shuffledPlayers[currentIndex].id
                assignRole(playerId, role)

                // Move to the next player
                currentIndex++
            }
        }
    }


}
