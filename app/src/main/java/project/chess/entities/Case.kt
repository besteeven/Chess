package project.chess.entities

data class Case(
    val x: Int,
    val y: Int,
    val couleur: Couleur, var piece: Piece? = null) {
    fun estValide(): Boolean {
        return x in 0..7 && y in 0..7
    }

    override fun toString(): String {
        val colonne = 'a' + x
        val ligne = 8 - y
        return "$colonne$ligne"  // Ex: "e4"
    }
}


