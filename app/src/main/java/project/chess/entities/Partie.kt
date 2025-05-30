package project.chess.entities

import java.time.LocalDateTime

data class Partie(
    val id: String,
    val dateDebut: LocalDateTime,
    var dateFin: LocalDateTime? = null,
    var tourActuel: Couleur,
    val historique: MutableList<Coup> = mutableListOf()
) {
    fun commencerPartie() {}
    fun terminerPartie() {}
    fun estEchecEtMat(): Boolean = false
    fun ajouterCoup(coup: Coup) {
        historique.add(coup)
    }
    

}
