package com.example.lupusinfabulav1.data

import com.example.lupusinfabulav1.R
import com.example.lupusinfabulav1.model.Player
import com.example.lupusinfabulav1.model.PlayerImageSource
import com.example.lupusinfabulav1.model.Role

object FakePlayersRepository {
    private var i = 1
    val players = listOf(
        Player(
            name = i++.toString(),
            role = Role.ASSASSINO,
            imageSource = PlayerImageSource.Resource(R.drawable.android_superhero1)
        ),
        Player(
            name = i++.toString(),
            role = Role.CITTADINO,
            imageSource = PlayerImageSource.Resource(R.drawable.android_superhero2)
        ),
        Player(
            name = i++.toString(),
            role = Role.VEGGENTE,
            imageSource = PlayerImageSource.Resource(R.drawable.android_superhero3)
        ),
        Player(
            name = i++.toString(),
            role = Role.FACILI_COSTUMI,
            imageSource = PlayerImageSource.Resource(R.drawable.android_superhero4)
        ),
        Player(
            name = i++.toString(),
            role = Role.ASSASSINO,
            imageSource = PlayerImageSource.Resource(R.drawable.android_superhero5)
        ),
        Player(
            name = i++.toString(),
            role = Role.CUPIDO,
            imageSource = PlayerImageSource.Resource(R.drawable.android_superhero6)
        ),
        Player(
            name = i++.toString(),
            role = Role.MEDIUM,
            imageSource = PlayerImageSource.Resource(R.drawable.ic_launcher_background)
        ),
        Player(
            name = i++.toString(),
            role = Role.CITTADINO,
            imageSource = PlayerImageSource.Resource(R.drawable.android_superhero1)
        ),

/*
        Player(
            name = i++.toString(),
            role = Role.MEDIUM,
            imageRes = R.drawable.ic_launcher_background
        ),
        Player(
            name = i++.toString(),
            role = Role.CITTADINO,
            imageRes = R.drawable.android_superhero1
        ),
        Player(
            name = i++.toString(),
            role = Role.MEDIUM,
            imageRes = R.drawable.ic_launcher_background
        ),*/





        Player(
            name = i++.toString(),
            role = Role.ASSASSINO,
            imageSource = PlayerImageSource.Resource(R.drawable.android_superhero1)
        ),
        Player(
            name = i++.toString(),
            role = Role.CITTADINO,
            imageSource = PlayerImageSource.Resource(R.drawable.android_superhero2)
        ),
        Player(
            name = i++.toString(),
            role = Role.VEGGENTE,
            imageSource = PlayerImageSource.Resource(R.drawable.android_superhero3)
        ),
        Player(
            name = i++.toString(),
            role = Role.FACILI_COSTUMI,
            imageSource = PlayerImageSource.Resource(R.drawable.android_superhero4)
        ),
        Player(
            name = i++.toString(),
            role = Role.ASSASSINO,
            imageSource = PlayerImageSource.Resource(R.drawable.android_superhero5)
        ),
        Player(
            name = i++.toString(),
            role = Role.CUPIDO,
            imageSource = PlayerImageSource.Resource(R.drawable.android_superhero6)
        ),
        Player(
            name = i++.toString(),
            role = Role.MEDIUM,
            imageSource = PlayerImageSource.Resource(R.drawable.ic_launcher_background)
        ),
        Player(
            name = i++.toString(),
            role = Role.CITTADINO,
            imageSource = PlayerImageSource.Resource(R.drawable.android_superhero1)
        ),

        Player(
            name = "fiorellone gigante",
            role = Role.CITTADINO,
            imageSource = PlayerImageSource.Resource(R.drawable.cupido_bow)
        ),


/*
        Player(
            name = i++.toString(),
            role = Role.ASSASSINO,
            imageRes = R.drawable.android_superhero1
        ),
        Player(
            name = i++.toString(),
            role = Role.CITTADINO,
            imageRes = R.drawable.android_superhero2
        ),
        Player(
            name = i++.toString(),
            role = Role.VEGGENTE,
            imageRes = R.drawable.android_superhero3
        ),
        Player(
            name = i++.toString(),
            role = Role.FACILI_COSTUMI,
            imageRes = R.drawable.android_superhero4
        ),
        Player(
            name = i++.toString(),
            role = Role.ASSASSINO,
            imageRes = R.drawable.android_superhero5
        ),

        Player(
            name = i++.toString(),
            role = Role.CUPIDO,
            imageRes = R.drawable.android_superhero6
        ),
        Player(
            name = i++.toString(),
            role = Role.MEDIUM,
            imageRes = R.drawable.ic_launcher_background
        ),
        Player(
            name = i++.toString(),
            role = Role.CITTADINO,
            imageRes = R.drawable.android_superhero1
        ),
*/
    )
}