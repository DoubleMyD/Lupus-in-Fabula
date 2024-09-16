package com.example.lupusinfabulav1.ui

enum class LupusInFabulaScreen(val title: String) {
    HOME_PAGE("Home Page"),  //Pagina di ingresso
    NEW_PLAYER("New Player"), //Pagina per aggiungere un giocatore
    PLAYERS_FOR_ROLE("Players for Role"),   //Pagina per assegnare il numero di giocatori per ruolo (3 lupi, 1 cupido ecc.)
    PLAYERS_ROLE("Players Role"),       //Pagina per assegnare un ruolo a un giocatore
    VILLAGE("Village"), //Pagina in cui avviene il gioco
    PLAYER_INFO("Player Info"),    //Pagina per vedere le informazioni di un giocatore
    PLAYER_LIST("Player List"),    //Pagina per vedere la lista dei giocatori
}