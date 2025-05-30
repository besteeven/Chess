package project.chess.entities

import kotlin.time.Duration

data class Chronometre(
    val tempsInitial: Duration,
    var tempsBlanc: Duration,
    var tempsNoir: Duration
) {
    fun demarrer(couleur: Couleur) {}
    fun arreter() {}
}

