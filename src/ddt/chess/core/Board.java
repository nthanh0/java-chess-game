package ddt.chess.core;
import ddt.chess.core.pieces.*;

import java.util.ArrayList;

public class Board {
    private final Square[][] board;

    public Board() {
        // create empty board
        board = new Square[8][8];
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                board[i][j] = new Square(i, j);
            }
        }
    }

    public Square getSquare(int x, int y) {
        return board[x][y];
    }

    public void setupPieces() {
        for (int i = 0; i < 8; i++) {
            // setup pawn pieces
            board[1][i].setPiece(new Pawn(PieceColor.BLACK));
            board[6][i].setPiece(new Pawn(PieceColor.WHITE));
        }
        // black rooks
        board[0][0].setPiece(new Rook(PieceColor.BLACK));
        board[0][7].setPiece(new Rook(PieceColor.BLACK));
        // white rooks
        board[7][0].setPiece(new Rook(PieceColor.WHITE));
        board[7][7].setPiece(new Rook(PieceColor.WHITE));
        // black knights
        board[0][1].setPiece(new Knight(PieceColor.BLACK));
        board[0][6].setPiece(new Knight(PieceColor.BLACK));
        // white knights
        board[7][1].setPiece(new Knight(PieceColor.WHITE));
        board[7][6].setPiece(new Knight(PieceColor.WHITE));
        // black bishops
        board[0][2].setPiece(new Bishop(PieceColor.BLACK));
        board[0][5].setPiece(new Bishop(PieceColor.BLACK));
        // white bishops
        board[7][2].setPiece(new Bishop(PieceColor.WHITE));
        board[7][5].setPiece(new Bishop(PieceColor.WHITE));
        // queens
        board[7][3].setPiece(new Queen(PieceColor.WHITE));
        board[0][3].setPiece(new Queen(PieceColor.BLACK));
        // kings
        board[7][4].setPiece(new King(PieceColor.WHITE));
        board[0][4].setPiece(new King(PieceColor.BLACK));
    }

    public void makeMove(Move move) {
        // just moves the piece to a square
        move.getToSquare().setPiece(move.getMovingPiece());
        move.getFromSquare().setPiece(null);
    }

    public void emptyBoard() {
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                board[i][j].setPiece(null);
            }
        }
    }

    public void undoMove(Move move) {
        // set the moving piece back to the square it moved from
        move.getFromSquare().setPiece(move.getMovingPiece());
        // put the captured piece back to where it was
        move.getToSquare().setPiece(move.getCapturedPiece());
    }

    public Square findKingSquare(PieceColor color) {
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                Square square = board[i][j];
                if (square.isEmpty()) {
                    // skip empty squares;
                    continue;
                }
                Piece piece = square.getPiece();
                if (piece.getType() == PieceType.KING
                        && piece.getColor() == color) {
                    return square;
                }
            }
        }
        return null;
    }


    public boolean isCheck(PieceColor color) {
        Square kingSquare = findKingSquare(color);
        if (kingSquare == null) {
            return false;
        }
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                Square square = board[i][j];
                if (square.isEmpty()) {
                    // skip empty squares;
                    continue;
                }
                Piece piece = square.getPiece();
                if (piece.getColor() == color) {
                    // skip pieces of the same color
                    continue;
                }
                Move moveToKing = new Move(square, kingSquare);
                if (MoveValidator.isPossibleMove(this, moveToKing)) {
                    return true;
                }
            }
        }
        return false;
    }

    // excluding en passant and castling
    public ArrayList<Move> generateAllValidNormalMoves(PieceColor color) {
        ArrayList<Move> res = new ArrayList<>();
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                Square square = getSquare(i, j);
                if (square.isEmpty()) {
                    // skip empty squares;
                    continue;
                }
                if (square.getPiece().getColor() != color) {
                    // ignore pieces of the opposite color
                    continue;
                }
                for (int k = 0; k < 8; k++) {
                    for (int l = 0; l < 8; l++) {
                        Square otherSquare = getSquare(k, l);
                        Move move = new Move(square, otherSquare);
                        if (MoveValidator.isValidNormalMove(this, move)) {
                            res.add(move);
                        }
                    }
                }
            }
        }
        return res;
    }

    // including en passant and castling
    public ArrayList<Move> generateAllValidMoves(PieceColor color, MoveHistory history) {
        ArrayList<Move> res = new ArrayList<>();
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                Square square = getSquare(i, j);
                if (square.isEmpty()) {
                    // skip empty squares;
                    continue;
                }
                if (square.getPiece().getColor() != color) {
                    // ignore pieces of the opposite color
                    continue;
                }
                for (int k = 0; k < 8; k++) {
                    for (int l = 0; l < 8; l++) {
                        Square otherSquare = getSquare(k, l);
                        Move move = new Move(square, otherSquare);
                        if (MoveValidator.isValidMove(this, move, history)) {
                            res.add(move);
                        }
                    }
                }
            }
        }
        return res;
    }

    public boolean isSafeAfterMove(Move move) {
        boolean res;
        // simulate move
        makeMove(move);
        // if king is in check after move then it's not valid
        res = !isCheck(move.getMovingPiece().getColor());
        // undo simulated move
        undoMove(move);
        return res;
    }

    public void promotePawn(Move move, PieceType newType) {
        if (newType == null) {
            return;
        }
        if (move.getMovingPiece().getType() != PieceType.PAWN) {
            return;
        }
        Piece newPiece = null;
        switch (newType) {
            case BISHOP -> newPiece = new Bishop(move.getMovingPiece().getColor());
            case KNIGHT -> newPiece = new Knight(move.getMovingPiece().getColor());
            case ROOK -> newPiece = new Rook(move.getMovingPiece().getColor());
            case QUEEN -> newPiece = new Queen(move.getMovingPiece().getColor());
        }
        move.getToSquare().setPiece(newPiece);
        move.getFromSquare().setPiece(null);
    }

    public void performEnPassant(Move move) {
        int xDirection = (move.getMovingPiece().isWhite()) ? -1 : 1;
        // store captured pawn in move
        Piece capturedPawn = getSquare(move.getToSquare().getX() - xDirection, move.getToSquare().getY()).getPiece();
        move.setCapturedPiece(capturedPawn);
        // move pawn to destination
        makeMove(move);
        // remove pawn captured by en passant
        getSquare(move.getToSquare().getX() - xDirection, move.getToSquare().getY()).setPiece(null);
    }

    public void performCastling(Move move) {
        makeMove(move);
        int fromX = move.getFromSquare().getX();
        int yDirection = (move.getFromSquare().getY() < move.getToSquare().getY()) ? 1 : -1;
        // 1 for king side, -1 for queen side
        Square oldRookSquare;
        Square newRookSquare;
        if (yDirection == 1) {
            // king side castle
            oldRookSquare = getSquare(fromX, 7);
            newRookSquare = getSquare(fromX, 5);
        } else {
            // queen side castle
            oldRookSquare = getSquare(fromX, 0);
            newRookSquare = getSquare(fromX, 3);
        }
        // move rook to next to king
        newRookSquare.setPiece(oldRookSquare.getPiece());
        oldRookSquare.setPiece(null);
    }

    public void undoEnPassant(Move move) {
        // place moving pawn back to where it was
        undoMove(move);
        // removing excess pawn
        move.getToSquare().setPiece(null);
        // place captured pawn back to where it was
        int xDirection = (move.getMovingPiece().isWhite()) ? -1 : 1;
        int toX = move.getToSquare().getX();
        int toY = move.getToSquare().getY();
        getSquare(toX - xDirection, toY).setPiece(move.getCapturedPiece());
    }

    public void undoCastling(Move move) {
        // place king back to where it was
        undoMove(move);
        int fromX = move.getFromSquare().getX();
        int yDirection = (move.getFromSquare().getY() < move.getToSquare().getY()) ? 1 : -1;
        // 1 for king side, -1 for queen side
        Square oldRookSquare;
        Square newRookSquare;
        if (yDirection == 1) {
            // king side castle
            oldRookSquare = getSquare(fromX, 7);
            newRookSquare = getSquare(fromX, 5);
        } else {
            // queen side castle
            oldRookSquare = getSquare(fromX, 0);
            newRookSquare = getSquare(fromX, 3);
        }
        // move rook back to the corner
        oldRookSquare.setPiece(newRookSquare.getPiece());
        newRookSquare.setPiece(null);
    }


}