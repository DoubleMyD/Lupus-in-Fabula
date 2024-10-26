package com.example.lupusinfabulav1.model

import com.example.lupusinfabulav1.ui.game.VillageEvent

//private const val TAG = "VoteManager"

data class VotePairPlayers(
    val voter: PlayerDetails,
    val votedPlayerDetails: PlayerDetails
)

data class RoleVotes(
    val role: Role,
    val voters: List<PlayerDetails>,
    val votedPlayerDetails: List<PlayerDetails>,
    val votesPairPlayers: List<VotePairPlayers>
)

sealed class MostVotedPlayer {
    data class SinglePlayer(val playerDetails: PlayerDetails) : MostVotedPlayer()
    data class PairPlayers(val playerDetails1: PlayerDetails, val playerDetails2: PlayerDetails) : MostVotedPlayer()
}

class VoteManager {
    private val votingHistory: MutableList<RoleVotes> = mutableListOf()
    private var lastVote: Boolean = false
    private var voterIndex: Int = 0

    fun startVoting(role: Role, voters: List<PlayerDetails>) {
        lastVote = false
        voterIndex = 0
        val voting = RoleVotes(role, voters, emptyList(), emptyList())
        votingHistory.add(voting)
    }

    fun vote(voter: PlayerDetails, votedPlayerDetails: PlayerDetails) {
        val currentVoting = getLastVotingState()

        val newVotes = currentVoting.votedPlayerDetails + votedPlayerDetails
        val newPairPlayer = currentVoting.votesPairPlayers + VotePairPlayers(voter, votedPlayerDetails)

        val newVoting = currentVoting.copy(
            votedPlayerDetails = newVotes,
            votesPairPlayers = newPairPlayer
        )
        votingHistory[votingHistory.lastIndex] = newVoting

        if (newVoting.role == Role.CUPIDO) {
            if (newVoting.voters.size * 2 == newVoting.votedPlayerDetails.size) {
                lastVote = true
            }
        } else if (newVoting.voters.size == newVoting.votedPlayerDetails.size) {
            lastVote = true
        }
    }

    fun getMostVotedPlayer(): MostVotedPlayer? {
        if (lastVote.not()) return null

        val lastVoting = getLastVotingState()

        return if (lastVoting.role == Role.CUPIDO) {
            getMostVotedPairPlayers(lastVoting)
        } else {
            getMostVotedSinglePlayer(lastVoting)
        }
    }

    fun getNextVoter(): PlayerDetails {
        val votingState = getLastVotingState()
        val voters = votingState.voters

        var nextIndex = voterIndex
        if (votingState.role == Role.CUPIDO) {
            if (votingState.votesPairPlayers.count { it.voter == voters[voterIndex] } == 2) {
                nextIndex = ++voterIndex
            }
        } else {
            nextIndex = ++voterIndex
        }

        return voters[nextIndex]
    }

    fun isLastVote(): Boolean {
        return lastVote
    }

    fun getLastVotingState(): RoleVotes {
        return votingHistory.last()
    }

    fun isDuplicateCupidoVote(voter: PlayerDetails, votedPlayerDetails: PlayerDetails): Boolean {
        val lastVoting = getLastVotingState()
        return lastVoting.votesPairPlayers.any { it.voter == voter && it.votedPlayerDetails == votedPlayerDetails }

    }

    fun isValidVote(voter: PlayerDetails, votedPlayerDetails: PlayerDetails): VillageEvent {
        return when {
            !votedPlayerDetails.alive -> VillageEvent.VotedPlayerIsDead
            isLastVote() -> VillageEvent.AllPlayersHaveVoted
            ( getLastVotingState().role == Role.CUPIDO &&
                    isDuplicateCupidoVote(voter, votedPlayerDetails) )-> { VillageEvent.CupidoAlreadyVoted
            }
            else -> VillageEvent.NullEvent
        }
    }

    private fun getMostVotedSinglePlayer(lastVoting: RoleVotes): MostVotedPlayer.SinglePlayer? {
        val voteCounts = lastVoting.votedPlayerDetails
            .groupingBy { it }
            .eachCount()
        val maxCount = voteCounts.maxOfOrNull { it.value } ?: 0
        val mostVotedPlayers = voteCounts.filter { it.value == maxCount }.keys

        return if (mostVotedPlayers.size == 1) {
            MostVotedPlayer.SinglePlayer(mostVotedPlayers.first())
        } else {
            null
        }
    }

    private fun getMostVotedPairPlayers(lastVoting: RoleVotes): MostVotedPlayer.PairPlayers?{
        // Map to store the count of each pair (ignoring order)
        val pairCountMap = mutableMapOf<Pair<PlayerDetails, PlayerDetails>, Int>()
        val votesPairPlayers = lastVoting.votesPairPlayers

        for (i in 0 until votesPairPlayers.size / 2) {
            val firstVote = votesPairPlayers[i * 2].votedPlayerDetails
            val secondVote = votesPairPlayers[i * 2 + 1].votedPlayerDetails
            val normalizedPair = if (firstVote.name > secondVote.name) {
                Pair(secondVote, firstVote)
            } else {
                Pair(firstVote, secondVote)
            }

            pairCountMap[normalizedPair] = pairCountMap.getOrDefault(normalizedPair, 0) + 1
        }

        // Find the pair with the highest count
        val maxEntry = pairCountMap.maxByOrNull { it.value }

        // Check if there's a tie by finding how many pairs have the same max count
        val maxCount = maxEntry?.value ?: return null
        val pairsWithMaxCount = pairCountMap.filterValues { it == maxCount }

        // Return the pair with the highest count if no tie, else return null
        return if (pairsWithMaxCount.size == 1) {
            MostVotedPlayer.PairPlayers(maxEntry.key.first, maxEntry.key.second)
        } else {
            null
        }
    }


}