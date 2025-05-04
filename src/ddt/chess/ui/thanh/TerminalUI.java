package ddt.chess.ui.thanh;

import ddt.chess.core.*;
import ddt.chess.core.MoveHistory;
import ddt.chess.core.MoveValidator;
import ddt.chess.util.Notation;
import ddt.chess.util.TimerClock;
import javax.sound.sampled.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Scanner;

public class TerminalUI {
    private final Scanner scanner;
    Game game;

    public TerminalUI(InputStream in) {
        this.scanner = new Scanner(in);
    }

    public void run() {
        game = new Game(1) {
            @Override
            public PieceType askForPromotion() {
                return TerminalUI.askForPromotion();
            }
        };
//        game = new Game(new TimerClock("00:00:15"), new TimerClock("00:00:15"));
        if (game.isTimedGame()) {
        }
        TimerClock whiteClock = game.getWhiteClock();
        TimerClock blackClock = game.getBlackClock();

        Board board = game.getBoard();
        MoveHistory history = game.getHistory();

        label:
        while (!game.checkIfGameIsOver()) {
            printAllValidMoves(board, game.getCurrentTurn(), history);
            if (game.isTimedGame()) {
                printTime(game);
                if (whiteClock.isFinished()) {
                    System.out.println("White ran out of time. Black wins!");
                    break;
                } else if (blackClock.isFinished()) {
                    System.out.println("Black ran out of time. White wins!");
                    break;
                }
            }
            printHistory(board, history);
            printFEN();
            printBoard(board);
            System.out.print("Enter starting square: ");

            // Add check for scanner.hasNextLine() to prevent NoSuchElementException
            if (!scanner.hasNextLine()) {
                System.out.println("No more input available. Exiting game.");
                break;
            }

            String startString = scanner.nextLine().toLowerCase().trim();
            switch (startString) {
                case "undo":
                    game.undoLastMove();
                    continue;
                case "reset":
                    game.resetBoard();
                    continue;
                case "quit":
                case "exit":
                    System.out.println(game.getHistory().getRawString());
                    System.out.println("Game terminated by user.");
                    break label;
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
            String filePath = "";
            if (isValidMove) {
                if (board.isCheck(game.getCurrentTurn())) {
                    filePath = "resources/sound/move-check.wav";
                } else if (move.getMoveType() == MoveType.PROMOTION) {
                    filePath = "resources/sound/promote.wav";
                } else {
                    if (move.isCapture()) {
                        filePath = "resources/sound/capture.wav";
                    } else {
                        filePath = "resources/sound/move.wav";
                    }
                }
            } else {
                if (move.getMovingPiece() != null && move.getMovingPiece().getColor() == game.getCurrentTurn()
                        && board.isCheck(game.getCurrentTurn())) {
                    filePath = "resources/sound/illegal.wav";
                }
            }
            File file = new File(filePath);
            try (AudioInputStream audioStream = AudioSystem.getAudioInputStream(file)) {
                Clip clip = AudioSystem.getClip();
                clip.open(audioStream);
                clip.start();
            }
            catch (FileNotFoundException e) {
                System.out.println("File not found");
            }
            catch (UnsupportedAudioFileException e) {
                System.out.println("Unsupported audio file");
            }
            catch (IOException e) {
                System.out.println("Something went wrong");
            }
            catch (LineUnavailableException e) {
                System.out.println("Unable to access audio resource");
            }
        }

        printBoard(board);
        String filePath = "resources/sound/game-end.wav";
        File file = new File(filePath);
        try (AudioInputStream audioStream = AudioSystem.getAudioInputStream(file)) {
            Clip clip = AudioSystem.getClip();
            clip.open(audioStream);
            clip.start();
        }
        catch (FileNotFoundException e) {
            System.out.println("File not found");
        }
        catch (UnsupportedAudioFileException e) {
            System.out.println("Unsupported audio file");
        }
        catch (IOException e) {
            System.out.println("Something went wrong");
        }
        catch (LineUnavailableException e) {
            System.out.println("Unable to access audio resource");
        }
        System.out.println(game.checkIfGameIsOver() ? game.getWinner() : "Game terminated early.");
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
        System.out.println("History: " + history.getHistoryString());
    }

    public void printTime(Game game) {
        System.out.print("White time left: ");
        System.out.println(game.getWhiteClock().getTimeLeftString());
        System.out.print("Black time left: ");
        System.out.println(game.getBlackClock().getTimeLeftString());
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

    public void printFEN() {
        System.out.println(Notation.gameToFEN(game));
    }


}