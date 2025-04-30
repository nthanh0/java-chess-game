package ddt.chess.core;

public class Player {
    private final PieceColor color;
    private final boolean isComputer;

    public Player(PieceColor color, boolean isComputer) {
        this.color = color;
        this.isComputer = isComputer;
    }


    public PieceColor getColor() {
        return color;
    }

    public boolean isComputer() {
        return isComputer;
    }

}
