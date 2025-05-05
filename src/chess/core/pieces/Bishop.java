package chess.core.pieces;
import chess.core.*;

public class Bishop extends Piece {
    public Bishop(PieceColor color) {
        super(color, PieceType.BISHOP);
    }

    @Override
    public boolean isValidPattern(Move move) {
        // difference between the x's and y's must be equal toSquare be a valid bishop move
        Square fromSquare = move.getFromSquare();
        Square toSquare = move.getToSquare();
        return (fromSquare.xDistanceTo(toSquare) == fromSquare.yDistanceTo(toSquare));
    }
}
