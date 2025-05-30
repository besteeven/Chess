package project.chess.entities

class Pion(couleur: Couleur, position: Case) : Piece(couleur, position) {
    fun peutPromouvoir(): Boolean = false

    override fun deplacer(caseArrivee: Case): Boolean = false
    override fun getMouvementsValides(): List<Case> = listOf()
    override fun getSymbol(): String {
        return when (couleur) {
            Couleur.BLANC -> "♙"
            Couleur.NOIR -> "♟"
        }
    }
}

