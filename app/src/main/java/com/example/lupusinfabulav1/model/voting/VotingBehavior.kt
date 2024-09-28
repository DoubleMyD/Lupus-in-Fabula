package com.example.lupusinfabulav1.model.voting

import com.example.lupusinfabulav1.model.Player

interface VotingBehavior {
    fun vote(votedPlayers: List<Player>): List<Player>
}