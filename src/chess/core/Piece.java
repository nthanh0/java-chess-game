package chess.core;

public abstract class Piece {
    private final PieceColor color;
    private final PieceType type;
    private boolean hasMoved = false;

    public Piece(PieceColor color, PieceType type) {
        this.color = color;
        this.type = type;
    }

    public PieceColor getColor() { return color; }
    public PieceType getType() { return type; }
    public boolean hasMoved() {
        return hasMoved;
    }

    public void setHasMoved(boolean moved) {
        this.hasMoved = moved;
    }

    public boolean isWhite() { return color == PieceColor.WHITE; }
    public abstract boolean isValidPattern(Move move);
}
