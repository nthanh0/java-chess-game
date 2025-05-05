package chess.core;

import chess.util.Notation;
import chess.util.Stockfish;
import chess.util.TimerClock;

public class ComputerGame extends Game {
    PieceColor playerSide;
    String stockfishPath = determineStockfishPath();

    Stockfish stockfish;
    private Thread stockfishThread = null;
    private volatile boolean calculationCanceled = false;

    public ComputerGame(PieceColor playerSide, int computerElo) {
        super();
        this.playerSide = playerSide;
        stockfish = new Stockfish();
        stockfish.startEngine(stockfishPath);
        stockfish.setEloLevel(computerElo);
    }

    public ComputerGame(int computerElo) {
        super();
        this.playerSide = PieceColor.WHITE;
        stockfish = new Stockfish();
        stockfish.startEngine(stockfishPath);
        stockfish.setEloLevel(computerElo);
    }

    public ComputerGame(PieceColor playerSide, double timeMinutes, int computerElo) {
        super(timeMinutes);
        this.playerSide = playerSide;
        stockfish = new Stockfish();
        stockfish.startEngine(stockfishPath);
        stockfish.setEloLevel(computerElo);
    }

    public ComputerGame(double timeMinutes, int computerElo) {
        super(timeMinutes);
        this.playerSide = PieceColor.WHITE;
        stockfish = new Stockfish();
        stockfish.startEngine(stockfishPath);
        stockfish.setEloLevel(computerElo);
    }

    public Move executeComputerMove() {
        stockfishThread = Thread.currentThread();
        calculationCanceled = false;

        String fen = Notation.gameToFEN(this);
        String bestMoveString;
        if (isTimedGame()) {
            bestMoveString = stockfish.getBestMoveWithTimeManagement(fen, getWhiteClock().getRemainingTimeMillis(), getBlackClock().getRemainingTimeMillis());
        } else {
            bestMoveString = stockfish.getBestMoveWithSimulatedTime(fen, 600000); // 10 mins
        }

        if (calculationCanceled || Thread.currentThread().isInterrupted()) {
            return null;
        }

        if (bestMoveString == null) {
            System.out.println("BEST MOVE STRING IS NULL");
            return null;
        }

        Move computerMove = Notation.stockfishOutputToMove(getBoard(), bestMoveString);

        if (getCurrentTurn() != getComputerSide()) {
            System.out.print("NOT COMPUTER'S TURN");
            return null;
        }

        if (isOver()) {
            return null;
        }
        if (bestMoveString.length() == 5) {
            computerMove.setMoveType(MoveType.PROMOTION);
            PieceType promoteTo = Notation.getPieceTypeFromLetter(bestMoveString.charAt(4));
            computerMove.setPromotionPieceType(promoteTo);
            getBoard().promotePawn(computerMove, promoteTo);
            getHistory().addMove(getBoard(), computerMove);
            if (isTimedGame()) {
                switchClocks();
            }
            updateHalfMoves(computerMove);
            switchTurns();
            if (getOnMoveMade() != null) {
                getOnMoveMade().run();
            }
            checkIfGameIsOver();
        } else {
            makeMove(computerMove);
        }
        stockfishThread = null;
        return computerMove;
    }

    public TimerClock getComputerClock() {
        return (playerSide == PieceColor.WHITE) ? getBlackClock() : getWhiteClock();
    }

    public void undoLastMove() {
        cancelCalculation();
        if (getCurrentTurn() == getPlayerSide()) {
            super.undoLastMove();
            super.undoLastMove();
        } else {
            super.undoLastMove();
        }
    }

    public TimerClock getPlayerClock() {
        return (playerSide == PieceColor.WHITE) ? getWhiteClock() : getBlackClock();
    }

    public PieceColor getPlayerSide() {
        return playerSide;
    }

    public PieceColor getComputerSide() {
        return (playerSide == PieceColor.WHITE) ? PieceColor.BLACK : PieceColor.WHITE;
    }

    public int getComputerElo() {
        return stockfish.getElo();
    }

    public void cancelCalculation() {
        calculationCanceled = true;
        stockfish.stopCalculation();
        if (stockfishThread != null && stockfishThread.isAlive()) {
            stockfishThread.interrupt();
            // Give a moment for the interruption to take effect
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                System.out.println("COMPUTER CALCULATION CANCELED");
            }
        }
    }

    private String determineStockfishPath() {
        String res = "";
        String osName = System.getProperty("os.name").toLowerCase();

        if (osName.contains("win")) {
            // windows
            res = "resources/stockfish-win/stockfish-windows-x86-64-avx2.exe";
        } else  {
            // linux
            res = "resources/stockfish/stockfish-ubuntu-x86-64-avx2";
        }
        return res;
    }
}
