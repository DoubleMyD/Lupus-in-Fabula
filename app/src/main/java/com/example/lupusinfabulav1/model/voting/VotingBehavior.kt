package com.example.lupusinfabulav1.model.voting

import com.example.lupusinfabulav1.model.Player
import com.example.lupusinfabulav1.model.RoleVotedPlayers

interface VotingBehavior {
    fun vote(players: List<Player>): RoleVotedPlayers
}