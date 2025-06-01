package project.chess.entities

class Cavalier(couleur: Couleur, position: Case) : Piece(couleur, position) {
    override fun deplacer(caseArrivee: Case): Boolean = false


    override fun getMouvementsValides(): List<Case> {
        val moves = listOf(
            Pair(2, 1), Pair(1, 2), Pair(-1, 2), Pair(-2, 1),
            Pair(-2, -1), Pair(-1, -2), Pair(1, -2), Pair(2, -1)
        )
        val result = mutableListOf<Case>()
        val x = position.x
        val y = position.y
        val plateau = position.plateau
        for ((dx, dy) in moves) {
            val nx = x + dx
            val ny = y + dy
            if (nx in 0..7 && ny in 0..7) {
                val target = plateau.cases[ny][nx]?.piece
                if (target == null || target.couleur != couleur) {
                    result.add(plateau.cases[ny][nx]!!)
                }
            }
        }
        return result
    }

    override fun getSymbol(): String {
        return when (couleur) {
            Couleur.BLANC -> "♘"
            Couleur.NOIR -> "♞"
        }
    }
}