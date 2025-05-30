package project.chess.entities

class Plateau {
    val cases: Array<Array<Case?>> = Array(8) { y ->
        Array(8) { x -> Case(x, y, if ((x + y) % 2 == 0) Couleur.BLANC else Couleur.NOIR) }
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
}
