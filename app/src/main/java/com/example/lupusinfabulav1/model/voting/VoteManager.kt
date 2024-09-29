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

class VoteManager {
    private val votingHistory: MutableList<RoleVotes> = mutableListOf()
    private var votingFinished: Boolean = false
    private var lastVote: Boolean = false
    private var voterIndex: Int = 0

    fun startVoting(role: Role, voters: List<Player>) {
        votingFinished = false
        lastVote = false
        voterIndex = 0
        val voting = RoleVotes(role, voters, emptyList(), emptyList())
        votingHistory.add(voting)
    }

    fun vote(voter: Player, votedPlayer: Player){
        val currentVoting = getLastVotingState()
        val newVotes = currentVoting.votedPlayers + votedPlayer
        val newPairPlayer = currentVoting.votesPairPlayers + VotePairPlayers(voter, votedPlayer)

        val newVoting = currentVoting.copy(
            votedPlayers = newVotes,
            votesPairPlayers = newPairPlayer
        )
        votingHistory[votingHistory.lastIndex] = newVoting

        if(newVoting.voters.size - newVoting.votedPlayers.size == 0) {
            lastVote = true
            getMostVotedPlayer()
        }
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

    fun getNextVoter(): Player{
        val nextIndex = ++voterIndex
        val voters = getLastVotingState().voters
//        Log.d(TAG, "Voters: ${voters}")
//        Log.d(TAG, "nextIndex: ${nextIndex}")
//        Log.d(TAG, "getNextPlayerr: ${voters[nextIndex]}")
        return voters[nextIndex]
    }

    fun isVotingFinished(): Boolean {
        return votingFinished
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