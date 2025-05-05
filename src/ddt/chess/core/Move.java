package ddt.chess.core;

// a class that stores information about a move
public class Move {
    private final Square fromSquare;
    private final Square toSquare;
    private final Piece movingPiece;
    private Piece capturedPiece;
    private MoveType moveType;
    private final boolean isFirstMoveOfPiece;

    private PieceType promotionPieceType;

    public Move(Square fromSquare, Square toSquare) {
        this.fromSquare = fromSquare;
        this.toSquare = toSquare;
        this.movingPiece = fromSquare.getPiece();
        this.capturedPiece = toSquare.getPiece();
        this.isFirstMoveOfPiece = movingPiece != null && !movingPiece.hasMoved();
    }

    public Square getFromSquare() {
        return fromSquare;
    }
    public Square getToSquare() {
        return toSquare;
    }
    public Piece getMovingPiece() {
        return movingPiece;
    }
    public Piece getCapturedPiece() {
        return capturedPiece;
    }

    public MoveType getMoveType() {
        return moveType;
    }

    public void setCapturedPiece(Piece piece) { this.capturedPiece = piece; } // for probably only en passant
    public void setMoveType(MoveType type) { this.moveType = type; }


    public boolean isCapture() {
        return (capturedPiece != null
                && movingPiece.getColor() != capturedPiece.getColor());
    }

    public boolean isFirstMoveOfPiece() {
        return isFirstMoveOfPiece;
    }

    public void setPromotionPieceType(PieceType promotionPieceType) {
        this.promotionPieceType = promotionPieceType;
    }

    public PieceType getPromotionPieceType() {
        return promotionPieceType;
    }

}
