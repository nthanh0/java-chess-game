import ddt.chess.core.Board;
import ddt.chess.core.Game;
import ddt.chess.ui.thanh.BoardPanel;
import ddt.chess.ui.thanh.ChessGameGUI;
import ddt.chess.ui.thanh.TerminalUI;
import ddt.chess.ui.thanh.TerminalUIComputer;
import ddt.chess.util.Notation;

import javax.swing.*;
import java.awt.*;

public class Main {
    public static void main(String[] args) {
//        new TerminalUI(System.in).run();
//        new TerminalUIComputer();
        new ChessGameGUI(true);
//        Game game = new Game();
//        System.out.println(Notation.gameToFEN(game));
    }
}