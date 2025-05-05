package chess.core.pieces;
import chess.core.*;

public class Queen extends Piece {
    public Queen(PieceColor color) { super(color, PieceType.QUEEN); }

    @Override
    public boolean isValidPattern(Move move) {
        // temporary instances of a rook and a bishop to use their isValidMove()
        Rook tempRook = new Rook(PieceColor.WHITE);
        Bishop tempBishop = new Bishop(PieceColor.WHITE);
        // a queen is basically a rook and a bishop combined
        return (tempBishop.isValidPattern(move) || tempRook.isValidPattern(move));
    }
}
