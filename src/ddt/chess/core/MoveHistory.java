package ddt.chess.core;
import ddt.chess.util.Notation;

import java.util.ArrayList;

public class MoveHistory {
    private final ArrayList<Move> history;
    private final ArrayList<String> historyString; // use letters to present pieces
    private final ArrayList<String> unicodeHistoryString; // use Unicode symbols to present pieces

    public MoveHistory() {
        history = new ArrayList<>();
        historyString = new ArrayList<>();
        unicodeHistoryString = new ArrayList<>();

    }

    public void addMove(Board board, Move move) {
        history.add(move);
        // update history string
        String index = "";
        if (history.size() % 2 == 1) {
            // move index
            index += (history.size() / 2 + 1) + ". ";
        }
        historyString.add(index + Notation.moveToAlgebraicNotation(board, move) + ' ');
        unicodeHistoryString.add(index + Notation.moveToUnicodeAlgebraicNotation(board, move) + ' ');
    }

    public void undoLastMove() {
        if (!history.isEmpty()) {
            history.removeLast();
            historyString.removeLast();
            unicodeHistoryString.removeLast();
        }
    }

    public boolean isEmpty() {
        return history.isEmpty();
    }

    public Move getLastMove() {
        return history.getLast();
    }

    public String getHistoryString() {
        StringBuilder res = new StringBuilder();
        for (String str : historyString) {
            res.append(str);
        }
        return res.toString();
    }
    public String getUnicodeString() {
        StringBuilder res = new StringBuilder();
        for (String str : unicodeHistoryString) {
            res.append(str);
        }
        return res.toString();
    }
    public void resetHistory() {
        history.clear();
    }

    public int getSize() {
        return history.size();
    }

    public ArrayList<Move> getHistory() {
        return history;
    }
}
