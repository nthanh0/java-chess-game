package ddt.chess.ui.thanh;

import ddt.chess.core.*;
import ddt.chess.util.Notation;

import java.util.ArrayList;
import java.util.Scanner;

public class TerminalUIComputer {
    ComputerGame game = new ComputerGame(PieceColor.WHITE, 0.25, 2000);
    Board board = game.getBoard();
    Scanner scanner = new Scanner(System.in);
    public TerminalUIComputer() {
        while (!game.checkIfGameIsOver()) {
            printBoard(board);
            printTime(game);
            if (game.getCurrentTurn() == game.getPlayerSide()) {
                System.out.print("Enter starting square: ");
                String startString = scanner.nextLine().toLowerCase().trim();
                switch (startString) {
                    case "undo":
                        game.undoLastMove();
                        game.undoLastMove();
                        continue;
                    case "reset":
                        game.resetBoard();
                        continue;
                }

                Square fromSquare = Notation.getSquareFromNotation(board, startString);
                if (fromSquare == null) {
                    continue;
                }

                System.out.print("Enter destination square: ");
                // Add another check here
                if (!scanner.hasNextLine()) {
                    System.out.println("No more input available. Exiting game.");
                    break;
                }

                String destString = scanner.nextLine().toLowerCase().trim();
                Square toSquare = Notation.getSquareFromNotation(board, destString);
                if (toSquare == null) {
                    continue;
                }
                Move move = new Move(fromSquare, toSquare);
                boolean isValidMove = game.makeMove(move);
            } else {
                System.out.println("Computer is thinking...");
                game.executeComputerMove();
            }
        }
    }
    static public void printBoard(Board board) {
        for (int i = 0; i < 8; i++) {
            System.out.print(8 - i);
            System.out.print(' ');
            for (int j = 0; j < 8; j++) {
                Square square = board.getSquare(i, j);
                if (square.isEmpty()) {
                    System.out.print(". "); // Use a consistent placeholder for empty squares
                } else {
                    Piece piece = square.getPiece();
                    char pieceChar = Notation.getPieceLetterFromPiece(piece);
                    System.out.print(pieceChar + " ");
                }
            }
            System.out.println();
        }
        System.out.println("  a b c d e f g h");
    }

    public void printHistory(Board board, MoveHistory history) {
        System.out.println("History: " + history.getUnicodeString());
    }

    public static PieceType askForPromotion() {
        char choice;
        Scanner scanner = new Scanner(System.in);
        System.out.println("Choose piece to promote to: Bishop(b), Knight(k), Rook(r), Queen(q): ");
        choice = Character.toUpperCase(scanner.next().charAt(0));
        return Notation.getPieceTypeFromLetter(choice);
    }

    public void printAllValidMoves(Board board, PieceColor turn, MoveHistory history) {
        ArrayList<Move> validMoves = board.generateAllValidMoves(turn, history);
        System.out.print("Valid moves: ");
        for (Move move : validMoves) {
            if (MoveValidator.isValidEnPassantPattern(move)) {
                System.out.printf("Valid en passant move from %s to %s\n"
                        , Notation.squareToNotation(move.getFromSquare())
                        , Notation.squareToNotation(move.getToSquare()));
            }
            System.out.print(Notation.moveToAlgebraicNotation(board, move) + " ");
        }
        System.out.println();
    }
    public void printTime(Game game) {
        System.out.print("White time left: ");
        System.out.println(game.getWhiteClock().getTimeLeftString());
        System.out.print("Black time left: ");
        System.out.println(game.getBlackClock().getTimeLeftString());
    }
}
