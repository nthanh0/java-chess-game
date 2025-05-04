package ddt.chess.core;
import ddt.chess.util.TimerClock;

public class Game {
    private final Board board;
    private final MoveHistory history;

    private TimerClock whiteClock;
    private TimerClock blackClock;
    private Thread whiteTimerThread;
    private Thread blackTimerThread;

    private boolean clocksAreRunning = false;
    private boolean isOver = false;

    private PieceColor turn = PieceColor.WHITE;
    private int halfMoves = 0; // tracking for 50 move rule, draw if it reaches 100

    private String winner; // "white", "black" (winning) or "none" (draw)
    private String gameOverCause; // "checkmate", "50" (fifty move rule), "stalemate", "time"

    private Thread clockWatcherThread; // thread to check if either of the 2 clocks run out

    private Runnable onGameEnd;
    private Runnable onMoveMade;

    public Game() {
        board = new Board();
        board.setupPieces();
        history = new MoveHistory();
    }

    // timed game
    public Game(double whiteTimeMinutes, double blackTimeMinutes) {
        board = new Board();
        board.setupPieces();
        history = new MoveHistory();
        this.whiteClock = new TimerClock(whiteTimeMinutes);
        whiteTimerThread = new Thread(whiteClock);
        this.blackClock = new TimerClock(blackTimeMinutes);
        blackTimerThread = new Thread(blackClock);

        whiteTimerThread.start();
        whiteClock.pause();
        blackTimerThread.start();
        blackClock.pause();

        startTimerWatcher();
    }

    public Game(double timeMinutes) {
        board = new Board();
        board.setupPieces();
        history = new MoveHistory();

        this.whiteClock = new TimerClock(timeMinutes);
        whiteTimerThread = new Thread(whiteClock);
        this.blackClock = new TimerClock(timeMinutes);
        blackTimerThread = new Thread(blackClock);

        whiteTimerThread.start();
        whiteClock.pause();
        blackTimerThread.start();
        blackClock.pause();

        startTimerWatcher();
    }

    public boolean makeMove(Move move) {
        // check if piece color aligns with turn
        if (move.getMovingPiece() != null && move.getMovingPiece().getColor() == turn) {
            // check if move is valid
            if (MoveValidator.isValidMove(board, move, history)) {
                // determine move type
                setMoveType(move);
                // add move to history
                history.addMove(board, move);
                if (!executeMove(move)) {
                    history.undoLastMove();
                    return false;
                }
                // set hasMoved to true
                move.getMovingPiece().setHasMoved(true);
            } else {
                // is invalid move
                return false;
            }
            // old history place

            // start/switch timers
            if (isTimedGame() && history.getSize() >= 2) {
                // only start the clocks if both sides have made a move
                if (!clocksAreRunning) {
                    clocksAreRunning = true;
                }
                switchClocks();
            }
            // update half move count, reset if moving piece is a pawn or a capture, else increment
            updateHalfMoves(move);
            // switch turns
            switchTurns();
            // is valid move
            checkIfGameIsOver();
            if (onMoveMade != null) {
                onMoveMade.run();
            }
            return true;
        }
        // if wrong turn then is invalid move
        return false;
    }

    public void setMoveType(Move move) {
        // determine type of move and save it in the move
        if (MoveValidator.isValidCastling(board, move)) {
            move.setMoveType(MoveType.CASTLING);
        } else if (MoveValidator.isValidPromotion(move)) {
            move.setMoveType(MoveType.PROMOTION);
        } else if (MoveValidator.isValidEnPassant(board, move, history)) {
            move.setMoveType(MoveType.EN_PASSANT);
        } else {
            if (move.isCapture()) {
                move.setMoveType(MoveType.CAPTURE);
            } else {
                move.setMoveType(MoveType.NORMAL);
            }
        }
    }

    public boolean executeMove(Move move) {
        switch(move.getMoveType()) {
            case CASTLING -> {
                board.performCastling(move);
            }
            case PROMOTION -> {
                PieceType promoteTo = askForPromotion();
                board.promotePawn(move, promoteTo);
                if (promoteTo == null) {
                    return false;
                }
            }
            case EN_PASSANT -> {
                board.performEnPassant(move);
            }
            default -> board.makeMove(move);
        }
        return true;
    }

    public void undoLastMove() {
        if (!history.isEmpty()) {
            // switch clocks back
            if (isTimedGame() && clocksAreRunning) {
                switchClocks();
            }
            // switch turns back
            switchTurns();
            Move lastMove = history.getLastMove();
            // restore hasMoved flag
            if (lastMove.isFirstMoveOfPiece()) {
                lastMove.getMovingPiece().setHasMoved(false);
            }
            switch (lastMove.getMoveType()) {
                case CASTLING -> board.undoCastling(lastMove);
                case EN_PASSANT -> board.undoEnPassant(lastMove);
                default -> board.undoMove(lastMove);
            }
            history.undoLastMove();
            restoreHalfMoves();
        }
    }

    public void switchClocks() {
        if (turn == PieceColor.WHITE) {
            whiteClock.pause();
            blackClock.resume();
        } else {
            blackClock.pause();
            whiteClock.resume();
        }
    }

    public Board getBoard() {
        return board;
    }

    public MoveHistory getHistory() {
        return history;
    }

    public PieceColor getCurrentTurn() {
        return turn;
    }

    public boolean isCheckMate() {
        // has to be in check to be a checkmate
        if (!board.isCheck(turn)) {
            return false;
        }
        // if there is no valid move then it's checkmate
        if (board.generateAllValidNormalMoves(turn).isEmpty()) {
            return true;
        }
        return false;
    }

    public boolean isStalemate() {
        // has to not be in check to be a stalemate
        if (board.isCheck(turn)) {
            return false;
        }
        // if there is no valid move then it's stalemate
        if (board.generateAllValidNormalMoves(turn).isEmpty()) {
            return true;
        }
        return false;
    }

    public boolean checkIfGameIsOver() {
        if (isCheckMate()) {
            winner = (turn == PieceColor.WHITE) ? "black" : "white";
            gameOverCause = "checkmate";
            endGame();
            return true;
        } else if (isStalemate()) {
            winner = "none";
            gameOverCause = "stalemate";
            endGame();
            return true;
        } else if (halfMoves == 100) {
            winner = "none";
            gameOverCause = "50";
            endGame();
            return true;
        } else if (isTimedGame()) {
            if (blackClock.isFinished()) {
                winner = "white";
                gameOverCause = "time";
                endGame();
                return true;
            } else if (whiteClock.isFinished()) {
                winner = "black";
                gameOverCause = "time";
                endGame();
                return true;
            }
        }
        return false;
    }

    public String getWinner() {
        return winner;
    }

    public void resetBoard() {
        history.resetHistory();
        board.emptyBoard();
        board.setupPieces();
        turn = PieceColor.WHITE;
    }

    public void switchTurns() {
        turn = (turn == PieceColor.WHITE) ? PieceColor.BLACK : PieceColor.WHITE;
    }

    public void updateHalfMoves(Move move) {
        // update half move count, reset if moving piece is a pawn or a capture, else increment
        if (move.getMovingPiece().getType() == PieceType.PAWN || move.getMoveType() == MoveType.CAPTURE) {
            halfMoves = 0;
        } else {
            halfMoves++;
        }
    }

    public TimerClock getWhiteClock() {
        return whiteClock;
    }

    public TimerClock getBlackClock() {
        return blackClock;
    }

    public boolean isTimedGame() {
        return (whiteClock != null && blackClock != null);
    }

    // placeholder for promotion handling, must be overridden
    public PieceType askForPromotion() {
        return null;
    }

    public int getHalfMoves() {
        return halfMoves;
    }

    public String getGameOverCause() {
        return gameOverCause;
    }

    public void restoreHalfMoves() {
        // expensive way of doing it but will improve later
        halfMoves = 0;
        for (Move move : history.getHistory()) {
            updateHalfMoves(move);
        }
    }

    public boolean isOver() {
        return isOver;
    }

    public void endGame() {
        isOver = true;
        if (isTimedGame()) {
            whiteTimerThread.interrupt();
            blackTimerThread.interrupt();
        }
        if (onGameEnd != null) {
            onGameEnd.run();
        }
    }

    public void resign() {
        winner = (getCurrentTurn() == PieceColor.WHITE) ? "black" : "white";
        gameOverCause = "resign";
        endGame();
    }

    public boolean clocksAreRunning() {
        return clocksAreRunning;
    }

    private void startTimerWatcher() {
        clockWatcherThread = new Thread(() -> {
            while (!isOver) {
                if (whiteClock != null && whiteClock.isFinished()) {
                    winner = "black";
                    gameOverCause = "time";
                    endGame();
                    break;
                } else if (blackClock != null && blackClock.isFinished()) {
                    winner = "white";
                    gameOverCause = "time";
                    endGame();
                    break;
                }
                try {
                    Thread.sleep(100); // check every 100ms
                } catch (InterruptedException e) {
                    break;
                }
            }
        });
        clockWatcherThread.setDaemon(true);
        clockWatcherThread.start();
    }

    public void setOnGameEnd(Runnable onGameEnd) {
        this.onGameEnd = onGameEnd;
    }

    public void setOnMoveMade(Runnable onMoveMade) {
        this.onMoveMade = onMoveMade;
    }

    public Runnable getOnMoveMade() {
        return onMoveMade;
    }
}