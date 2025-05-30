package project.chess.entities

abstract class Piece(
    val couleur: Couleur,
    var position: Case

) {
    abstract fun deplacer(caseArrivee: Case): Boolean
    abstract fun getMouvementsValides(): List<Case>
    abstract fun getSymbol(): String

}
