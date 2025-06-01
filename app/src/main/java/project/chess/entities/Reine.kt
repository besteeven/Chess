package project.chess.entities

class Reine(couleur: Couleur, position: Case) : Piece(couleur, position) {
    override fun deplacer(caseArrivee: Case): Boolean = false

    override fun getMouvementsValides(): List<Case> {
        val result = mutableListOf<Case>()
        val directions = listOf(
            Pair(1, 0), Pair(-1, 0), Pair(0, 1), Pair(0, -1),
            Pair(1, 1), Pair(1, -1), Pair(-1, 1), Pair(-1, -1)
        )
        val x = position.x
        val y = position.y
        val plateau = position.plateau
        for ((dx, dy) in directions) {
            var nx = x + dx
            var ny = y + dy
            while (nx in 0..7 && ny in 0..7) {
                val target = plateau.cases[ny][nx]?.piece
                if (target == null) {
                    result.add(plateau.cases[ny][nx]!!)
                } else {
                    if (target.couleur != couleur) result.add(plateau.cases[ny][nx]!!)
                    break
                }
                nx += dx
                ny += dy
            }
        }
        return result
    }
    override fun getSymbol(): String {
        return when (couleur) {
            Couleur.BLANC -> "♕"
            Couleur.NOIR -> "♛"
        }

    }
}