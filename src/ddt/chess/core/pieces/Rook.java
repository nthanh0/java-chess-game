package ddt.chess.core.pieces;
import ddt.chess.core.*;

public class Rook extends Piece {
    public Rook(PieceColor color) { super(color, PieceType.ROOK); }

    @Override
    public boolean isValidPattern(Move move) {
        Square fromSquare = move.getFromSquare();
        Square toSquare = move.getToSquare();
        if (fromSquare.getX() == toSquare.getX()) {
            // equal x's means moving horizontally
            // and in this case the move is only valid when the y's are different
            return fromSquare.getY() != toSquare.getY();
        } else if (fromSquare.getY() == toSquare.getY()) {
            // equal y's means moving horizontally
            // and in this case the move is only valid when the x's are different
            return fromSquare.getX() != toSquare.getX();
        } else {
            // if neither the x's nor the y's are equal, then the move is obviously not valid rook move
            return false;
        }
    }
}
