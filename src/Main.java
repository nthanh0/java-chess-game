import ddt.chess.ui.ChessGameGUI;
import ddt.chess.ui.NewGameDialog;

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