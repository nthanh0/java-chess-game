package ddt.chess.core;

public enum PieceType {
    PAWN('p'), KNIGHT('n'), BISHOP('b'), ROOK('r'), QUEEN('q'), KING('k');
    private final char pieceLetter;
    PieceType(char letter) {
        this.pieceLetter = letter;
    }
    public char getPieceLetter() {
        return pieceLetter;
    }

}
