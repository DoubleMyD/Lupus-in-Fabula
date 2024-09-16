package com.example.lupusinfabulav1.ui

import androidx.lifecycle.ViewModel
import com.example.lupusinfabulav1.model.Player
import com.example.lupusinfabulav1.model.PlayerManager

class NewPlayerViewModel : ViewModel() {
    private val playerManager = PlayerManager()

    /**
     * Add a new player to the game.
     * return false if the name is not available or already taken, true if the player is added successfully
     */
    fun onAddPlayer(name: String, image: Int): Boolean{
        if(!checkName(name))
            return false/*TODO avvisa l'utente che il nome non è disponibile*/

        val newPlayer = Player(name = name, imageRes = image)
        return playerManager.addPlayer(newPlayer)
    }

    /**
     * Quando impari ad aggiungere una foto dalla galleria, cambia questa funzione
     */
    fun changeImage(): Int{
        return 1 /*TODO*/
    }

    private fun checkName(name: String) : Boolean{
        /*TODO*/
        return true
    }


}