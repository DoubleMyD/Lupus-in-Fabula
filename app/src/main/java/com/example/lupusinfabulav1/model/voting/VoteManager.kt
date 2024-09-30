package com.example.lupusinfabulav1.model.voting

import android.util.Log
import com.example.lupusinfabulav1.model.Player
import com.example.lupusinfabulav1.model.Role

private const val TAG = "VoteManager"

data class VotePairPlayers(
    val voter: Player,
    val votedPlayer: Player
)

data class RoleVotes(
    val role: Role,
    val voters: List<Player>,
    val votedPlayers: List<Player>,
    val votesPairPlayers: List<VotePairPlayers>
)

sealed class MostVotedPlayer {
    data class SinglePlayer(val player: Player) : MostVotedPlayer()
    data class PairPlayers(val player1: Player, val player2: Player) : MostVotedPlayer()
}

class VoteManager {
    private val votingHistory: MutableList<RoleVotes> = mutableListOf()
    private var lastVote: Boolean = false
    private var voterIndex: Int = 0

    fun startVoting(role: Role, voters: List<Player>) {
        lastVote = false
        voterIndex = 0
        val voting = RoleVotes(role, voters, emptyList(), emptyList())
        votingHistory.add(voting)
    }

    fun vote(voter: Player, votedPlayer: Player) {
        val currentVoting = getLastVotingState()

        val newVotes = currentVoting.votedPlayers + votedPlayer
        val newPairPlayer = currentVoting.votesPairPlayers + VotePairPlayers(voter, votedPlayer)

        val newVoting = currentVoting.copy(
            votedPlayers = newVotes,
            votesPairPlayers = newPairPlayer
        )
        votingHistory[votingHistory.lastIndex] = newVoting

        if (newVoting.role == Role.CUPIDO) {
            if (newVoting.voters.size * 2 == newVoting.votedPlayers.size) {
                lastVote = true
                //getMostVotedPlayer()
            }
        } else if (newVoting.voters.size == newVoting.votedPlayers.size) {
            lastVote = true
            //getMostVotedPlayer()
        }
    }

    fun getMostVotedPlayer(): MostVotedPlayer? {
        if (lastVote.not()) return null

        val lastVoting = getLastVotingState()

        if (lastVoting.role == Role.CUPIDO) {
            // Map to store the count of each pair (ignoring order)
            val pairCountMap = mutableMapOf<Pair<Player, Player>, Int>()
            val votesPairPlayers = lastVoting.votesPairPlayers

            for (i in 0 until votesPairPlayers.size / 2) {
                val firstVote = votesPairPlayers[i * 2].votedPlayer
                val secondVote = votesPairPlayers[i * 2 + 1].votedPlayer
                val normalizedPair = if (firstVote.name > secondVote.name) {
                    Pair(secondVote, firstVote)
                } else {
                    Pair(firstVote, secondVote)
                }

                pairCountMap[normalizedPair] = pairCountMap.getOrDefault(normalizedPair, 0) + 1
                Log.d(
                    TAG,
                    "Pair: ${firstVote.name}, ${secondVote.name}, NormalizedPair: ${normalizedPair.first.name}, ${normalizedPair.second.name}, Count: ${pairCountMap[normalizedPair]}"
                )
            }

            Log.d(
                TAG,
                "started PairPlayers: ${lastVoting.votesPairPlayers.map { "${it.voter.name} voted ${it.votedPlayer.name}" }}"
            )
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
        } else {

            val voteCounts = lastVoting.votedPlayers
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
    }

    fun getNextVoter(): Player {
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

    fun handleTie(role: Role, votedPlayers: List<Player>) {
        startVoting(role, votedPlayers)
    }


}