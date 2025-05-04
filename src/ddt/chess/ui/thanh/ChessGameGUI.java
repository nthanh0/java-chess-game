package ddt.chess.ui.thanh;

import ddt.chess.core.*;
import ddt.chess.util.SoundPlayer;

import javax.swing.*;
import java.awt.*;
import java.security.spec.InvalidKeySpecException;
import java.util.ArrayList;
import java.util.Arrays;

public class ChessGameGUI extends JFrame {
    private final Game game;
    private final BoardPanel boardPanel;
    private final SidePanel sidePanel;
    private final SoundPlayer soundPlayer;

    public ChessGameGUI(GameSettings settings) {
        this.setResizable(false);
        this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        int screenHeight = Toolkit.getDefaultToolkit().getScreenSize().height;
//        screenHeight = 2160;
        int squareSize = findClosest(new int[]{32, 64, 96, 128, 256, 512, 1024}, screenHeight / 12);

        if (settings.isComputerGame) {
            if (settings.isTimedGame) {
                game = new ComputerGame(settings.timeMinutes, settings.computerElo) {
                    public PieceType askForPromotion() {
                        PromotionPrompt prompt = new PromotionPrompt(boardPanel);
                        prompt.setVisible(true); // show dialog
                        return prompt.getResult();
                    }
                };
            } else {
                game = new ComputerGame(settings.computerElo) {
                    public PieceType askForPromotion() {
                        PromotionPrompt prompt = new PromotionPrompt(boardPanel);
                        prompt.setVisible(true); // show dialog
                        return prompt.getResult();
                    }
                };
            }
        } else {
            if (settings.isTimedGame) {
                game = new Game(settings.timeMinutes) {
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
        }

        boardPanel = new BoardPanel(game, squareSize);
        sidePanel = new SidePanel(game, squareSize, settings.allowUndo, boardPanel);
        soundPlayer = new SoundPlayer();

        game.setOnMoveMade(() -> {
            sidePanel.getBlackInfoPanel().updateComputerThinking(); // no idea why i have to
                                                                    // add this to both listeners
            soundPlayer.playMoveSound(game);
        });

        game.getHistory().setOnUpdate(() -> {
            boardPanel.resetHighlights();
            boardPanel.clearMoveHints();
            boardPanel.repaint();
            sidePanel.getHistoryScrollPane().refresh();
            sidePanel.getBlackInfoPanel().updateComputerThinking();
        });

        game.setOnGameEnd(() -> {
            sidePanel.hideGameControlPanel();
            sidePanel.getGameOverPanel().update();
            boardPanel.clearMoveHints();
            soundPlayer.playGameEndSound();
        });

        this.add(boardPanel, BorderLayout.CENTER);
        this.add(sidePanel, BorderLayout.EAST);

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

    public static ChessGameGUI createFromDialog(NewGameDialog dialog) {
        if (!dialog.isConfirmed()) {
            return null; // User cancelled the dialog
        }

        return new ChessGameGUI(
                new GameSettings(dialog.isComputerGame(),
                                dialog.getComputerElo(),
                                dialog.isTimedGame(),
                                dialog.getTimeMinutes(),
                                dialog.isAllowUndo())
        );
    }

}



