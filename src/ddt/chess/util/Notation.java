package ddt.chess.util;

import ddt.chess.core.*;
import ddt.chess.core.pieces.*;
import ddt.chess.core.MoveValidator;

public class Notation {

    // from e.g "a3" to Square(5, 0);
    public static Square getSquareFromNotation(Board board, String notation) {
        if (notation.length() < 2) {
            return null;
        }
        int x = 7 - (notation.charAt(1) - '1');
        int y = notation.charAt(0) - 'a';
        if (x < 0 || x > 7 || y < 0 || y > 7) {
            return null;
        }
        return board.getSquare(x, y);
    }

    // the opposite
    public static String squareToNotation(Square square) {
        // empty string (for string type casting) + file char + rank char
        return "" + (char)('a' + square.getY()) + (char)('1' + (7 - square.getX()));
    }

    // case-sensitive piece letter, uppercase for white, lowercase for black
    public static char getPieceLetterFromPiece(Piece piece) {
        char res = piece.getType().getPieceLetter();
        if (piece.isWhite()) {
            res = Character.toUpperCase(res);
        }
        return res;
    }

    // case-sensitive, uppercase for white, lowercase for black
    public static Piece getPieceFromLetter(char letter) {
        PieceType type = getPieceTypeFromLetter(Character.toUpperCase(letter));
        PieceColor color = (Character.isUpperCase(letter)) ? PieceColor.WHITE : PieceColor.BLACK;
        switch (type) {
            case KING -> {
                return new King(color);
            }
            case QUEEN -> {
                return new Queen(color);
            }
            case ROOK -> {
                return new Rook(color);
            }
            case BISHOP -> {
                return new Bishop(color);
            }
            case KNIGHT -> {
                return new Knight(color);
            }
            case PAWN -> {
                return new Pawn(color);
            }
            default -> {
                return null;
            }
        }
    }

    public static char getUnicodePieceSymbolFromType(PieceType type) {
        return switch (type) {
            case PAWN -> '♙';
            case KNIGHT -> '♘';
            case BISHOP -> '♗';
            case ROOK -> '♖';
            case QUEEN -> '♕';
            case KING -> '♔';
        };
    }

    public static PieceType getPieceTypeFromLetter(char symbol) {
        return switch (Character.toUpperCase(symbol)) {
            case 'P' -> PieceType.PAWN;
            case 'N' -> PieceType.KNIGHT;
            case 'B' -> PieceType.BISHOP;
            case 'R' -> PieceType.ROOK;
            case 'Q' -> PieceType.QUEEN;
            case 'K' -> PieceType.KING;
            default -> throw new IllegalStateException("Unexpected value: " + symbol);
        };
    }


    public static String moveToAlgebraicNotation(Board board, Move move) {
        StringBuilder res = new StringBuilder();
        boolean isPawn = move.getMovingPiece().getType() == PieceType.PAWN;
        int fromSquareX = move.getFromSquare().getX();
        int fromSquareY = move.getFromSquare().getY();
        // castling
        if (move.getMoveType() == MoveType.CASTLING) {
            return (fromSquareY < move.getToSquare().getY()) ? "O-O" : "O-O-O";
        }
        if (!isPawn) {
            res.append(Character.toUpperCase(move.getMovingPiece().getType().getPieceLetter()));
        }
        if (isPawn) {
            // pawns only and always have to specify the file when capturing
            // so no checking is needed
            if (move.isCapture() || MoveValidator.isValidEnPassantPattern(move)) {
                res.append(Notation.squareToNotation(move.getFromSquare()).charAt(0));
            }
        } else {
            for (int i = 0; i < 8; i++) {
                for (int j = 0; j < 8; j++) {
                    if (i == fromSquareX && j == fromSquareY) {
                        continue;
                    }
                    Square otherSquare = board.getSquare(i, j);
                    Piece otherPiece = otherSquare.getPiece();
                    Move otherMove = new Move(otherSquare, move.getToSquare());
                    if (otherPiece != null
                            && otherPiece.getColor() == move.getMovingPiece().getColor()
                            && otherPiece.getType() == move.getMovingPiece().getType()) {
                        if (MoveValidator.isValidNormalMove(board, otherMove)) {
                            if (otherSquare.getX() == fromSquareX) {
                                // if there is another piece of the same type in the same rank
                                // then specify the file
                                res.append(Notation.squareToNotation(move.getFromSquare()).charAt(0));
                            }
                            if (otherSquare.getY() == fromSquareY) {
                                // if there is another piece of the same type in the same file
                                // then specify the rank
                                res.append(Notation.squareToNotation(move.getFromSquare()).charAt(1));
                            }
                            // if neither but the other piece can still move to that same square
                            // then specify the rank
                            res.append(Notation.squareToNotation(move.getFromSquare()).charAt(0));
                        }
                    }
                }
            }
        }
        if (move.isCapture() || MoveValidator.isValidEnPassantPattern(move)) {
            res.append('x');
        }
        res.append(Notation.squareToNotation(move.getToSquare()));
        // check for promotion
        if (MoveValidator.isValidPromotion(move)) {
            char promotedToPieceLetter = move.getToSquare().getPiece().getType().getPieceLetter();
            res.append('=').append(Character.toUpperCase(promotedToPieceLetter));
        }
        return res.toString();
    }

    public static String moveToUnicodeAlgebraicNotation(Board board, Move move) {
        StringBuilder res = new StringBuilder();
        boolean isPawn = move.getMovingPiece().getType() == PieceType.PAWN;
        int fromSquareX = move.getFromSquare().getX();
        int fromSquareY = move.getFromSquare().getY();
        // castling
        if (move.getMoveType() == MoveType.CASTLING) {
            return (fromSquareY < move.getToSquare().getY()) ? "O-O" : "O-O-O";
        }
        if (!isPawn) {
            res.append(getUnicodePieceSymbolFromType(move.getMovingPiece().getType()));
        }
        if (isPawn) {
            // pawns only and always have to specify the file when capturing
            // so no checking is needed
            if (move.isCapture() || MoveValidator.isValidEnPassantPattern(move)) {
                res.append(Notation.squareToNotation(move.getFromSquare()).charAt(0));
            }
        } else {
            for (int i = 0; i < 8; i++) {
                for (int j = 0; j < 8; j++) {
                    if (i == fromSquareX && j == fromSquareY) {
                        continue;
                    }
                    Square otherSquare = board.getSquare(i, j);
                    Piece otherPiece = otherSquare.getPiece();
                    Move otherMove = new Move(otherSquare, move.getToSquare());
                    if (otherPiece != null
                            && otherPiece.getColor() == move.getMovingPiece().getColor()
                            && otherPiece.getType() == move.getMovingPiece().getType()) {
                        if (MoveValidator.isValidNormalMove(board, otherMove)) {
                            if (otherSquare.getX() == fromSquareX) {
                                // if there is another piece of the same type in the same rank
                                // then specify the file
                                res.append(Notation.squareToNotation(move.getFromSquare()).charAt(0));
                            }
                            if (otherSquare.getY() == fromSquareY) {
                                // if there is another piece of the same type in the same file
                                // then specify the rank
                                res.append(Notation.squareToNotation(move.getFromSquare()).charAt(1));
                            }
                            // if neither but the other piece can still move to that same square
                            // then specify the rank
                            res.append(Notation.squareToNotation(move.getFromSquare()).charAt(0));
                        }
                    }
                }
            }
        }
        if (move.isCapture() || MoveValidator.isValidEnPassantPattern(move)) {
            res.append('x');
        }
        res.append(Notation.squareToNotation(move.getToSquare()));
        // check for promotion
        if (MoveValidator.isValidPromotion(move)) {
            char promotedToPieceLetter = move.getToSquare().getPiece().getType().getPieceLetter();
            res.append('=').append(Character.toUpperCase(promotedToPieceLetter));
        }
        return res.toString();
    }

    public static String gameToFEN(Game game) {
        String res = "";
        Board board = game.getBoard();

        // turn
        char turn = (game.getCurrentTurn() == PieceColor.WHITE) ? 'w' : 'b';

        // possible en passant target
        String possibleEnPassantTarget = "";
        if (!game.getHistory().isEmpty()) {
            Move lastMove = game.getHistory().getLastMove();
            if (MoveValidator.isDoublePawnPush(lastMove)) {
                int xDirection = (lastMove.getMovingPiece().isWhite()) ? -1 : 1;
                Square enPassantCaptureSquare = board.getSquare(lastMove.getToSquare().getX() - xDirection, lastMove.getToSquare().getY());
                possibleEnPassantTarget += squareToNotation(enPassantCaptureSquare);
            }
        }
        if (possibleEnPassantTarget.isEmpty()) {
            possibleEnPassantTarget += '-';
        }

        // castling rights
        String castlingRights = "";
        if (MoveValidator.canCastleKingside(board, PieceColor.WHITE)) {
            castlingRights += 'K';
        }
        if (MoveValidator.canCastleQueenside(board, PieceColor.WHITE)) {
            castlingRights += 'Q';
        }
        if (MoveValidator.canCastleKingside(board, PieceColor.BLACK)) {
            castlingRights += 'k';
        }
        if (MoveValidator.canCastleQueenside(board, PieceColor.BLACK)) {
            castlingRights += 'q';
        }
        if (castlingRights.isEmpty()) {
            castlingRights += "-";
        }

        // piece placement
        String placement = "";
        for (int i = 0; i < 8; i++) {
            int emptySquares = 0;
            for (int j = 0; j < 8; j++) {
                Square square = board.getSquare(i, j);
                if (square.isEmpty()) {
                    emptySquares++;
                } else {
                    if (emptySquares != 0) {
                        placement += emptySquares;
                        emptySquares = 0;
                    }
                    placement += getPieceLetterFromPiece(square.getPiece());
                }
            }
            if (emptySquares != 0) {
                placement += emptySquares;
            }
            if (i != 7 ) {
                placement += '/';
            }
        }

        // halfmove and fullmove count
        int halfMoves = game.getHalfMoves();
        int fullMoves = game.getHistory().getSize() / 2 + 1;
        res += placement + ' ' + turn + ' ' + castlingRights + ' '
            + possibleEnPassantTarget + ' ' + halfMoves + ' ' + fullMoves;
        return res;
    }

    // used for both game restore and stockfish
    public static Move stockfishOutputToMove(Board board, String moveString) {
        return new Move(Notation.getSquareFromNotation(board, moveString.substring(0, 2)),
                        Notation.getSquareFromNotation(board, moveString.substring(2)));
    }



}
