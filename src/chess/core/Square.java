package chess.core;

public class Square {
    // x is the rank, y is the file
    private final int y;
    private final int x;
    Piece piece;

    public Square(int x, int y) {
        this.x = x;
        this.y = y;
    }
    public Square(int x, int y, Piece piece) {
        this.x = x;
        this.y = y;
        this.piece = piece;
    }

    public int getX() {
        return x;
    }
    public int getY() {
        return y;
    }
    public Piece getPiece() { return piece; }
    public void setPiece(Piece piece) {
        this.piece = piece;
    }


    public boolean isEmpty() {
        return (piece == null);
    }
    public boolean isOccupied() { return !(isEmpty()); }

    public int xDistanceTo(Square other) {
        return Math.abs(x - other.getX());
    }

    public int yDistanceTo(Square other) {
        return Math.abs(y - other.getY());
    }
}