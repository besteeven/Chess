package project.chess.entities

data class Utilisateur(
    val id: String,
    var nom: String,
    var elo: Int,
    val amis: MutableList<Utilisateur> = mutableListOf()
) {
    fun ajouterAmi(ami: Utilisateur) {
        amis.add(ami)
    }

    fun supprimerAmi(ami: Utilisateur) {
        amis.remove(ami)
    }

    fun mettreAJourElo(adversaire: Utilisateur, resultat: Resultat) {
        // À implémenter
    }
}
