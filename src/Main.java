import ddt.chess.core.Board;
import ddt.chess.core.Game;
import ddt.chess.ui.thanh.*;
import ddt.chess.util.Notation;
import ddt.chess.util.TimerClock;

import javax.swing.*;
import java.awt.*;

public class Main {
    public static void main(String[] args) {
//        new TerminalUI(System.in).run();
//        new TerminalUIComputer();
//        new ChessGameGUI(false, 2000, true, 1, true);
//        Game game = new Game();
//        System.out.println(Notation.gameToFEN(game));
        ChessGameGUI.createFromDialog(NewGameDialog.showDialog((int)Toolkit.getDefaultToolkit().getScreenSize().getHeight() / 12));
    }
}