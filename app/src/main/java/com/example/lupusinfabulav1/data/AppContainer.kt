package com.example.lupusinfabulav1.data

import com.example.lupusinfabulav1.model.PlayerManager
import com.example.lupusinfabulav1.model.VoteManager

interface AppContainer {
    val playersRepository: PlayersRepository
    val playerManager: PlayerManager
    val voteManager: VoteManager
}

class DefaultAppContainer : AppContainer {
    override val playersRepository: PlayersRepository by lazy {
        DatabasePlayerRepository()
    }

    override val playerManager: PlayerManager by lazy {
        PlayerManager()
    }

    override val voteManager: VoteManager by lazy {
        VoteManager()
    }
}