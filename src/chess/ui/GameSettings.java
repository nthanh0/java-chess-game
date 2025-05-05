package chess.ui;

public class GameSettings {
    public boolean isTimedGame;
    public double timeMinutes;
    public boolean isComputerGame;
    public int computerElo;
    public boolean allowUndo;

    // default settings
    public GameSettings() {
        isTimedGame = false;
        timeMinutes = 0;
        isComputerGame = false;
        computerElo = 0;
        allowUndo = true;
    }
    public GameSettings(boolean isComputerGame, int computerElo, boolean isTimedGame, double timeMinutes, boolean allowUndo) {
        this.isComputerGame = isComputerGame;
        this.computerElo = computerElo;
        this.isTimedGame = isTimedGame;
        this.timeMinutes = timeMinutes;
        this.allowUndo = allowUndo;
    }
}
