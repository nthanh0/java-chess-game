package chess.ui.thanh;

import java.io.ByteArrayInputStream;

public class TerminalUITest {

    public static void main(String[] args) {
        // Uncomment the test you want to run

//         testCheckmate();
//         testCheck();
         testEnPassant();
//         testKingsideCastling();
//         testQueensideCastling();
//         testUndoMove();
//        testUndoEnPassantAndRepeat();
//         testUndoCastlingAndRepeat();
    }

    /**
     * Test for checkmate (Scholar's Mate)
     * 1. e4 e5
     * 2. Qh5 Nc6
     * 3. Bc4 Nf6??
     * 4. Qxf7#
     */
    public static void testCheckmate() {
        String input = "e2\ne4\n" +  // White pawn to e4
                "e7\ne5\n" +        // Black pawn to e5
                "d1\nh5\n" +        // White queen to h5
                "b8\nc6\n" +        // Black knight to c6
                "f1\nc4\n" +        // White bishop to c4
                "g8\nf6\n" +        // Black knight to f6
                "h5\nf7\n";         // White queen takes f7 pawn (checkmate)

        ByteArrayInputStream in = new ByteArrayInputStream(input.getBytes());
        TerminalUI gui = new TerminalUI(in);
        System.out.println("RUNNING CHECKMATE TEST (Scholar's Mate)");
        gui.run();
    }

    /**
     * Test for check (without checkmate)
     * 1. e4 e5
     * 2. Bc4 Nc6
     * 3. Qh5 (check)
     */
    public static void testCheck() {
        String input = "e2\ne4\n" +  // White pawn to e4
                "e7\ne5\n" +        // Black pawn to e5
                "f1\nc4\n" +        // White bishop to c4
                "b8\nc6\n" +        // Black knight to c6
                "d1\nh5\n" +        // White queen to h5 (check)
                "g7\ng6\n";         // Black pawn to g6 (blocks check)

        ByteArrayInputStream in = new ByteArrayInputStream(input.getBytes());
        TerminalUI gui = new TerminalUI(in);
        System.out.println("RUNNING CHECK TEST");
        gui.run();
    }

    /**
     * Test for en passant
     * 1. e4 d5
     * 2. e5 f5
     * 3. exf6 (en passant)
     */
    public static void testEnPassant() {
        String input = "e2\ne4\n" +  // White pawn to e4
                "d7\nd5\n" +        // Black pawn to d5
                "e4\ne5\n" +        // White pawn to e5
                "f7\nf5\n" +        // Black pawn to f5
                "e5\nf6\n";         // White pawn captures en passant

        ByteArrayInputStream in = new ByteArrayInputStream(input.getBytes());
        TerminalUI gui = new TerminalUI(in);
        System.out.println("RUNNING EN PASSANT TEST");
        gui.run();
    }

    /**
     * Test for kingside castling
     * 1. e4 e5
     * 2. Nf3 Nc6
     * 3. Bc4 Bc5
     * 4. O-O
     */
    public static void testKingsideCastling() {
        String input = "e2\ne4\n" +  // White pawn to e4
                "e7\ne5\n" +        // Black pawn to e5
                "g1\nf3\n" +        // White knight to f3
                "b8\nc6\n" +        // Black knight to c6
                "f1\nc4\n" +        // White bishop to c4
                "f8\nc5\n" +        // Black bishop to c5
                "e1\ng1\n";         // White castles kingside

        ByteArrayInputStream in = new ByteArrayInputStream(input.getBytes());
        TerminalUI gui = new TerminalUI(in);
        System.out.println("RUNNING KINGSIDE CASTLING TEST");
        gui.run();
    }

    /**
     * Test for queenside castling
     * 1. d4 d5
     * 2. Nc3 Nf6
     * 3. Bf4 Bf5
     * 4. Qd3 e6
     * 5. O-O-O
     */
    public static void testQueensideCastling() {
        String input = "d2\nd4\n" +  // White pawn to d4
                "d7\nd5\n" +        // Black pawn to d5
                "b1\nc3\n" +        // White knight to c3
                "g8\nf6\n" +        // Black knight to f6
                "c1\nf4\n" +        // White bishop to f4
                "c8\nf5\n" +        // Black bishop to f5
                "d1\nd3\n" +        // White queen to d3
                "e7\ne6\n" +        // Black pawn to e6
                "e1\nc1\n";         // White castles queenside

        ByteArrayInputStream in = new ByteArrayInputStream(input.getBytes());
        TerminalUI gui = new TerminalUI(in);
        System.out.println("RUNNING QUEENSIDE CASTLING TEST");
        gui.run();
    }

    /**
     * Test for undoing moves
     * 1. e4 e5
     * 2. undo (undo e5)
     * 3. undo (undo e4)
     * 4. d4 d5
     */
    public static void testUndoMove() {
        String input = "e2\ne4\n" +  // White pawn to e4
                "e7\ne5\n" +        // Black pawn to e5
                "undo\n" +          // Undo Black's move
                "undo\n" +          // Undo White's move
                "d2\nd4\n" +        // White pawn to d4
                "d7\nd5\n";         // Black pawn to d5

        ByteArrayInputStream in = new ByteArrayInputStream(input.getBytes());
        TerminalUI gui = new TerminalUI(in);
        System.out.println("RUNNING UNDO MOVE TEST");
        gui.run();
    }

    /**
     * Test for undoing en passant and repeating it
     * 1. e4 d5
     * 2. e5 f5
     * 3. exf6 (en passant)
     * 4. undo (undo en passant)
     * 5. e5xf6 (en passant again)
     */
    public static void testUndoEnPassantAndRepeat() {
        String input = "e2\ne4\n" +  // White pawn to e4
                "d7\nd5\n" +        // Black pawn to d5
                "e4\ne5\n" +        // White pawn to e5
                "f7\nf5\n" +        // Black pawn to f5
                "e5\nf6\n" +        // White pawn captures en passant
                "undo\n" +          // Undo en passant
                "e5\nf6\n";         // Try en passant again

        ByteArrayInputStream in = new ByteArrayInputStream(input.getBytes());
        TerminalUI gui = new TerminalUI(in);
        System.out.println("RUNNING UNDO EN PASSANT AND REPEAT TEST");
        gui.run();
    }

    /**
     * Test for undoing castling and repeating it
     * 1. e4 e5
     * 2. Nf3 Nc6
     * 3. Bc4 Bc5
     * 4. O-O (kingside castling)
     * 5. undo (undo castling)
     * 6. O-O (kingside castling again)
     */
    public static void testUndoCastlingAndRepeat() {
        String input = "e2\ne4\n" +  // White pawn to e4
                "e7\ne5\n" +        // Black pawn to e5
                "g1\nf3\n" +        // White knight to f3
                "b8\nc6\n" +        // Black knight to c6
                "f1\nc4\n" +        // White bishop to c4
                "f8\nc5\n" +        // Black bishop to c5
                "e1\ng1\n" +        // White castles kingside
                "undo\n" +          // Undo castling
                "e1\ng1\n";         // Try castling again

        ByteArrayInputStream in = new ByteArrayInputStream(input.getBytes());
        TerminalUI gui = new TerminalUI(in);
        System.out.println("RUNNING UNDO CASTLING AND REPEAT TEST");
        gui.run();
    }
}