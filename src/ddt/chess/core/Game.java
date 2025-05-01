package ddt.chess.core;
import ddt.chess.util.Notation;
import ddt.chess.util.TimerClock;

import java.util.Timer;

public class Game {
    private final Board board;
    private final MoveHistory history;
    private TimerClock whiteClock;
    private TimerClock blackClock;

    boolean timersStarted = false;

    private Thread whiteTimerThread;
    private Thread blackTimerThread;

    private PieceColor turn = PieceColor.WHITE;
    private int halfMoves = 0; // tracking for 50 move rule, draw if it reaches 100

    private String winner; // "white", "black" (winning) or "none" (draw)
    private String gameOverCause; // "checkmate", "50" (fifty move rule), "stalemate", "time"

    public Game() {
        board = new Board();
        board.setupPieces();
        history = new MoveHistory();
    }

    // timed game
    public Game(String whiteTime, String blackTime) {
        board = new Board();
        board.setupPieces();
        history = new MoveHistory();
        this.whiteClock = new TimerClock(whiteTime);
        whiteTimerThread = new Thread(whiteClock);
        this.blackClock = new TimerClock(blackTime);
        blackTimerThread = new Thread(blackClock);

        whiteTimerThread.start();
        whiteClock.pause();
        blackTimerThread.start();
        blackClock.pause();
    }

    public Game(String time) {
        board = new Board();
        board.setupPieces();
        history = new MoveHistory();
        this.whiteClock = new TimerClock(time);
        whiteTimerThread = new Thread(whiteClock);
        this.blackClock = new TimerClock(time);
        blackTimerThread = new Thread(blackClock);

        whiteTimerThread.start();
        whiteClock.pause();
        blackTimerThread.start();
        blackClock.pause();
    }

    public boolean makeMove(Move move) {
        // check if piece color aligns with turn
        if (move.getMovingPiece() != null && move.getMovingPiece().getColor() == turn) {
            // check if move is valid
            if (MoveValidator.isValidMove(board, move, history)) {
                // determine type of move and perform corresponding methods in Board if valid
                if (MoveValidator.isValidCastling(board, move)) {
                    // set move type
                    move.setMoveType(MoveType.CASTLING);
                    board.performCastling(move);
                } else if (MoveValidator.isValidPromotion(move)) {
                    move.setMoveType(MoveType.PROMOTION);
                    PieceType promoteToPiece = askForPromotion();
                    // skip if askForPromotion() return null
                    // (happens when it's unimplemented or the promotion is cancelled)
                    if (promoteToPiece == null) {
                        return false;
                    }
                    board.promotePawn(move, promoteToPiece);
                } else if (MoveValidator.isValidEnPassant(board, move, history)) {
                    // set move type
                    move.setMoveType(MoveType.EN_PASSANT);
                    board.performEnPassant(move);
                } else {
                    // set move type
                    if (move.isCapture()) {
                        move.setMoveType(MoveType.CAPTURE);
                    } else {
                        move.setMoveType(MoveType.NORMAL);
                    }
                    board.makeMove(move);
                }
                // set hasMoved to true
                move.getMovingPiece().setHasMoved(true);
            } else {
                // is invalid move
                return false;
            }
            // add move to history
            history.addMove(board, move);
            // start/switch timers
            if (isTimedGame()) {
                switchClocks();
            }
            // update half move count, reset if moving piece is a pawn or a capture, else increment
            updateHalfMoves(move);
            // switch turns
            switchTurns();
            // is valid move
            return true;
        }
        // if wrong turn then is invalid move
        return false;
    }

    public void undoLastMove() {
        if (!history.isEmpty()) {
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

    public boolean isOver() {
        if (isCheckMate()) {
            winner = (turn == PieceColor.WHITE) ? "black" : "white";
            gameOverCause = "checkmate";
            return true;
        } else if (isStalemate()) {
            winner = "none";
            gameOverCause = "stalemate";
            return true;
        } else if (halfMoves == 100) {
            winner = "none";
            gameOverCause = "50";
            return true;
        } else if (isTimedGame()) {
            if (blackClock.isFinished()) {
                winner = "white";
                gameOverCause = "time";
                return true;
            } else if (whiteClock.isFinished()) {
                winner = "black";
                gameOverCause = "time";
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


}