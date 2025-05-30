package project.chess.entities

import java.time.LocalDateTime


data class Coup(
    val joueur: Joueur,
    val piece: Piece,
    val depart: Case,
    val arrivee: Case,
    val date: LocalDateTime
)
