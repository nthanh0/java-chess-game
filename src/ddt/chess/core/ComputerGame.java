package ddt.chess.core;

import ddt.chess.util.Notation;
import ddt.chess.util.Stockfish;
import ddt.chess.util.TimerClock;

public class ComputerGame extends Game {
    PieceColor playerSide;
    String stockfishPath = "resources/stockfish/stockfish-ubuntu-x86-64-avx2";
    Stockfish stockfish;

    public ComputerGame(PieceColor playerSide, int computerElo) {
        super();
        this.playerSide = playerSide;
        stockfish = new Stockfish();
        stockfish.startEngine(stockfishPath);
        stockfish.setEloLevel(computerElo);
    }

    public Move executeComputerMove() {
        int waitTime;
        String fen = Notation.gameToFEN(this);
        String bestMoveString;
        if (isTimedGame()) {
            bestMoveString = stockfish.getBestMoveWithTimeManagement(fen, getWhiteClock().getRemainingTimeMillis(), getBlackClock().getRemainingTimeMillis());
        } else {
            bestMoveString = stockfish.getBestMove(Notation.gameToFEN(this), 500);
        }
        Move computerMove = Notation.stockfishOutputToMove(getBoard(), bestMoveString);
        if (bestMoveString.length() == 5) {
            PieceType promoteTo = Notation.getPieceTypeFromLetter(bestMoveString.charAt(4));
            getBoard().promotePawn(computerMove, promoteTo);
<<<<<<< HEAD
            switchTurns();
=======
>>>>>>> ba34ded1284ad0aca60705e735f11c78359416b9
        } else {
            makeMove(computerMove);
        }
        return computerMove;
    }

    public TimerClock getComputerClock() {
        return (playerSide == PieceColor.WHITE) ? getBlackClock() : getWhiteClock();
    }

    public TimerClock getPlayerClock() {
        return (playerSide == PieceColor.WHITE) ? getWhiteClock() : getBlackClock();
    }

    public PieceColor getPlayerSide() {
        return playerSide;
    }
}
