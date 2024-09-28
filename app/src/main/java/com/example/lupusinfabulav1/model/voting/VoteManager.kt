package com.example.lupusinfabulav1.model.voting

import com.example.lupusinfabulav1.model.Player
import com.example.lupusinfabulav1.model.Role

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

class VoteManager {
    private val votingHistory: MutableList<RoleVotes> = mutableListOf()
    private var votingFinished: Boolean = false

    fun startVoting(role: Role, voters: List<Player>) {
        votingFinished = false
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
    }

    fun getMostVotedPlayer() : Player? {
        val lastVoting = getLastVotingState()

        val voteCounts = lastVoting.votedPlayers
            .groupingBy { it }
            .eachCount()
        val maxCount = voteCounts.maxOfOrNull { it.value } ?: 0
        val mostVotedPlayers = voteCounts.filter { it.value == maxCount }.keys

        val allHaveVoted = lastVoting.voters.size == lastVoting.votedPlayers.size

       if(mostVotedPlayers.size == 1 && allHaveVoted){
           votingFinished = true
           return mostVotedPlayers.first()
       }else{
           return null
       }
    }

    fun getLastVotingState(): RoleVotes {
        return votingHistory.last()
    }

    fun handleTie(votedPlayers: List<Player>) {

    }
}