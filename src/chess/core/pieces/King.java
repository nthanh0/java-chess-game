package chess.core.pieces;
import chess.core.*;

public class King extends Piece {
    public King(PieceColor color) { super(color, PieceType.KING); }

    @Override
    public boolean isValidPattern(Move move) {
        Square fromSquare = move.getFromSquare();
        Square toSquare = move.getToSquare();
        // the king can only move 1 square in any direction
        return (fromSquare.xDistanceTo(toSquare) <= 1 && fromSquare.yDistanceTo(toSquare) <= 1);
    }
}
