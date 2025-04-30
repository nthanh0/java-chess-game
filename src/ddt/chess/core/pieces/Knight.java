package ddt.chess.core.pieces;
import ddt.chess.core.*;

public class Knight extends Piece {
    public Knight(PieceColor color) { super(color, PieceType.KNIGHT); }

    @Override
    public boolean isValidPattern(Move move) {
        Square fromSquare = move.getFromSquare();
        Square toSquare = move.getToSquare();
        if (fromSquare.xDistanceTo(toSquare) == 2) {
            // if the vertical difference is 2 squares
            // then the horizontal difference must be 1 for the move toSquare be valid knight move
            return (fromSquare.yDistanceTo(toSquare) == 1);
        } else if (fromSquare.xDistanceTo(toSquare) == 1) {
            // likewise, if horizontal difference is 1
            // then the vertical difference must be 2
            return (fromSquare.yDistanceTo(toSquare) == 2);
        } else {
            // if neither of the above then obviously not a knight move
            return false;
        }
    }
}
