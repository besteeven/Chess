package project.chess.entities

data class Joueur(
    val couleur: Couleur,
    val utilisateur: Utilisateur
) {
    fun win() {}
    fun lose() {}
    fun draw() {}
    fun jouerCoup(coup: Coup) {}
}
