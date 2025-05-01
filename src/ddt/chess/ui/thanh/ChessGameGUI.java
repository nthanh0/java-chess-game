package ddt.chess.ui.thanh;

import ddt.chess.core.*;

import javax.swing.*;
import java.awt.*;
import java.security.spec.InvalidKeySpecException;
import java.util.ArrayList;
import java.util.Arrays;

public class ChessGameGUI extends JFrame {
    private final Game game;
    private final BoardPanel boardPanel;
    private final HistoryPanel historyPanel;

    public ChessGameGUI(boolean isComputerGame) {
        this.setResizable(false);
        this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        int screenHeight = Toolkit.getDefaultToolkit().getScreenSize().height;
//        screenHeight = 2160;
        int squareSize = findClosest(new int[]{32, 64, 96, 128, 256, 512, 1024}, screenHeight / 12);

        if (isComputerGame) {
            game = new ComputerGame(PieceColor.WHITE, 2000) {
                @Override
                public PieceType askForPromotion() {
                    PromotionPrompt prompt = new PromotionPrompt(boardPanel);
                    prompt.setVisible(true); // show dialog
                    return prompt.getResult();
                }
            };
        } else {
            game = new Game() {
                @Override
                public PieceType askForPromotion() {
                    PromotionPrompt prompt = new PromotionPrompt(boardPanel);
                    prompt.setVisible(true); // show dialog
                    return prompt.getResult();
                }
            };
        }

        historyPanel = new HistoryPanel(game, squareSize);
        boardPanel = new BoardPanel(game, historyPanel, squareSize);

        this.add(boardPanel, BorderLayout.CENTER);
        this.add(historyPanel, BorderLayout.NORTH);

        this.pack();
        this.setLocationRelativeTo(null);
        this.setVisible(true);
    }

    public static int findClosest(int[] arr, int target) {
        int closest = arr[0];
        for (int n : arr) {
            if (Math.abs(n - target) < Math.abs(closest - target)) {
                closest = n;
            }
        }
        return closest;
    }

    public void setSquareSize() { }

}



