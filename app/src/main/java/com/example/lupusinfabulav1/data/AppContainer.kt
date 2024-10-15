package com.example.lupusinfabulav1.data

import android.content.Context
import com.example.lupusinfabulav1.data.database.ImageIO
import com.example.lupusinfabulav1.data.database.LupusInFabulaDatabase
import com.example.lupusinfabulav1.model.PlayerManager
import com.example.lupusinfabulav1.model.VoteManager

interface AppContainer {
    val playersRepository: PlayersRepository
    val playersListsRepository: PlayersListsRepository
    val playerManager: PlayerManager
    val voteManager: VoteManager
    val imageIO: ImageIO
}

class DefaultAppContainer(private val context: Context) : AppContainer {

    override val playersRepository: PlayersRepository by lazy {
        OfflinePlayersRepository(LupusInFabulaDatabase.getDatabase(context).playerDao())
    }

    override val playersListsRepository: PlayersListsRepository by lazy {
        OfflinePlayersListsRepository(
            playersRepository = playersRepository,
            playersListDao = LupusInFabulaDatabase.getDatabase(context).playersListDao())
    }

    override val playerManager: PlayerManager by lazy {
        PlayerManager()
    }

    override val voteManager: VoteManager by lazy {
        VoteManager()
    }

    override val imageIO: ImageIO by lazy {
        ImageIO()
    }

}