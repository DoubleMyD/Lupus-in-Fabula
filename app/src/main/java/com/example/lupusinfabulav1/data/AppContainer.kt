package com.example.lupusinfabulav1.data

interface AppContainer {
    val playersRepository: PlayersRepository
}

class DefaultAppContainer : AppContainer {
    override val playersRepository: PlayersRepository by lazy {
        DatabasePlayerRepository()
    }
}