package project.chess.entities

class Pion(couleur: Couleur, position: Case) : Piece(couleur, position) {
    fun peutPromouvoir(): Boolean = false

    override fun deplacer(caseArrivee: Case): Boolean = false

    override fun getMouvementsValides(): List<Case> {
        val moves = mutableListOf<Case>()
        val dir = if (couleur == Couleur.BLANC) 1 else -1
        val x = position.x
        val y = position.y
        val plateau = position.plateau

        // Avance d'une case
        if (y + dir in 0..7 && plateau.cases[y + dir][x]?.piece == null) {
            moves.add(plateau.cases[y + dir][x]!!)
            // Avance de deux cases depuis la position initiale
            if ((couleur == Couleur.BLANC && y == 1) || (couleur == Couleur.NOIR && y == 6)) {
                if (plateau.cases[y + 2 * dir][x]?.piece == null) {
                    moves.add(plateau.cases[y + 2 * dir][x]!!)
                }
            }
        }
        // Captures diagonales et prise en passant
        for (dx in listOf(-1, 1)) {
            val nx = x + dx
            val ny = y + dir
            if (nx in 0..7 && ny in 0..7) {
                val target = plateau.cases[ny][nx]?.piece
                if (target != null && target.couleur != couleur) {
                    moves.add(plateau.cases[ny][nx]!!)
                }
                // Prise en passant
                val caseEnPassant = plateau.caseEnPassant
                if (caseEnPassant != null && caseEnPassant.x == nx && caseEnPassant.y == ny) {
                    moves.add(caseEnPassant)
                }
            }
        }
        return moves
    }
    
    override fun getSymbol(): String {
        return when (couleur) {
            Couleur.BLANC -> "♙"
            Couleur.NOIR -> "♟"
        }
    }
}

