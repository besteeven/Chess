package project.chess.entities

class Roi(couleur: Couleur, position: Case) : Piece(couleur, position) {
    var aBouge: Boolean = false

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
            val nx = x + dx
            val ny = y + dy
            if (nx in 0..7 && ny in 0..7) {
                val target = plateau.cases[ny][nx]?.piece
                if (target == null || target.couleur != couleur) {
                    result.add(plateau.cases[ny][nx]!!)
                }
            }
        }
        // Roque
        if (!aBouge && y in 0..7 && x == 4 ) {
            // Petit roque (côté roi)
            val tourDroite = plateau.cases[y][7]?.piece
            if (tourDroite is Tour && !tourDroite.aBouge) {
                if ((5..6).all { plateau.cases[y][it]?.piece == null }) {
                    // Vérifie que les cases ne sont pas attaquées
                    val casesRoi = listOf(plateau.cases[y][4], plateau.cases[y][5], plateau.cases[y][6])
                    if (casesRoi.all { it != null && !plateau.caseEstAttaquee(it, couleur) }) {
                        result.add(plateau.cases[y][6]!!) // Le roi va en g6 ou g1
                    }
                }
            }
            // Grand roque (côté dame)
            val tourGauche = plateau.cases[y][0]?.piece
            if (tourGauche is Tour && !tourGauche.aBouge) {
                if ((1..3).all { plateau.cases[y][it]?.piece == null }) {
                    val casesRoi = listOf(plateau.cases[y][4], plateau.cases[y][3], plateau.cases[y][2])
                    if (!plateau.roiEnEchec(couleur) && casesRoi.all { it != null && !plateau.caseEstAttaquee(it, couleur) }) {
                        result.add(plateau.cases[y][2]!!) // Le roi va en c1 ou c8
                    }
                }
            }
        }


        return result
    }

    override fun getSymbol(): String {
        return when (couleur) {
            Couleur.BLANC -> "♔"
            Couleur.NOIR -> "♚"
        }
    }
}