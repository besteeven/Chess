package project.chess.entities

class Plateau {
    val cases: Array<Array<Case?>> = Array(8) { y ->
        Array(8) { x -> Case(x, y, if ((x + y) % 2 == 0) Couleur.BLANC else Couleur.NOIR, null, this)
        }
    }
    var caseEnPassant: Case? = null

        init {
            // Pions blancs et noirs
            for (x in 0..7) {
                cases[1][x]?.piece = Pion(Couleur.BLANC, cases[1][x]!!)
                cases[6][x]?.piece = Pion(Couleur.NOIR, cases[6][x]!!)
            }
            // Tours
            cases[0][0]?.piece = Tour(Couleur.BLANC, cases[0][0]!!)
            cases[0][7]?.piece = Tour(Couleur.BLANC, cases[0][7]!!)
            cases[7][0]?.piece = Tour(Couleur.NOIR, cases[7][0]!!)
            cases[7][7]?.piece = Tour(Couleur.NOIR, cases[7][7]!!)
            // Cavaliers
            cases[0][1]?.piece = Cavalier(Couleur.BLANC, cases[0][1]!!)
            cases[0][6]?.piece = Cavalier(Couleur.BLANC, cases[0][6]!!)
            cases[7][1]?.piece = Cavalier(Couleur.NOIR, cases[7][1]!!)
            cases[7][6]?.piece = Cavalier(Couleur.NOIR, cases[7][6]!!)
            // Fous
            cases[0][2]?.piece = Fou(Couleur.BLANC, cases[0][2]!!)
            cases[0][5]?.piece = Fou(Couleur.BLANC, cases[0][5]!!)
            cases[7][2]?.piece = Fou(Couleur.NOIR, cases[7][2]!!)
            cases[7][5]?.piece = Fou(Couleur.NOIR, cases[7][5]!!)
            // Dames
            cases[0][3]?.piece = Reine(Couleur.BLANC, cases[0][3]!!)
            cases[7][3]?.piece = Reine(Couleur.NOIR, cases[7][3]!!)
            // Rois
            cases[0][4]?.piece = Roi(Couleur.BLANC, cases[0][4]!!)
            cases[7][4]?.piece = Roi(Couleur.NOIR, cases[7][4]!!)
        }

    fun initialiser() {}
    fun getPiece(position: Case): Piece? = null
    fun deplacerPiece(debut: Case, fin: Case): Boolean = false

    fun getBoardMatrix(): Array<Array<Piece?>> {
        return Array(8) { y ->
            Array(8) { x ->
                cases[y][x]?.piece // case peut être null, pièce aussi
            }
        }
    }

    fun roiEnEchec(couleur: Couleur): Boolean {
        // Trouve le roi de la couleur
        val roiCase = cases.flatten().find { it?.piece is Roi && it.piece?.couleur == couleur }
        if (roiCase == null) return false
        // Pour chaque pièce adverse, regarde si elle peut atteindre le roi
        val adversaire = if (couleur == Couleur.BLANC) Couleur.NOIR else Couleur.BLANC
        for (ligne in cases) {
            for (case in ligne) {
                val piece = case?.piece
                if (piece != null && piece.couleur == adversaire) {
                    if (piece.getMouvementsValides().contains(roiCase)) {
                        return true
                    }
                }
            }
        }
        return false
    }

    fun estEchecEtMat(couleur: Couleur): Boolean {
        if (!roiEnEchec(couleur)) return false
        // Pour chaque pièce du joueur, regarde s'il existe un coup qui enlève l'échec
        for (ligne in cases) {
            for (case in ligne) {
                val piece = case?.piece
                if (piece != null && piece.couleur == couleur) {
                    for (move in piece.getMouvementsValides()) {
                        // Simule le coup
                        val anciennePiece = move.piece
                        move.piece = piece
                        case.piece = null
                        val echec = roiEnEchec(couleur)
                        // Annule le coup
                        case.piece = piece
                        move.piece = anciennePiece
                        if (!echec) return false
                    }
                }
            }
        }
        return true
    }

    fun estPat(couleur: Couleur): Boolean {
        if (roiEnEchec(couleur)) return false
        // Si aucune pièce ne peut jouer, c'est pat
        for (ligne in cases) {
            for (case in ligne) {
                val piece = case?.piece
                if (piece != null && piece.couleur == couleur) {
                    if (piece.getMouvementsValides().isNotEmpty()) return false
                }
            }
        }
        return true
    }

    fun caseEstAttaquee(case: Case, couleur: Couleur): Boolean {
        val adversaire = if (couleur == Couleur.BLANC) Couleur.NOIR else Couleur.BLANC
        for (ligne in cases) {
            for (c in ligne) {
                val piece = c?.piece
                if (piece != null && piece.couleur == adversaire) {
                    if (piece.getMouvementsValides().contains(case)) {
                        return true
                    }
                }
            }
        }
        return false
    }


}
