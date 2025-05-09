package chess.ui;

import chess.core.*;
import chess.util.SoundPlayer;

import javax.swing.*;
import java.awt.*;

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
            if (game instanceof ComputerGame computerGame
                && computerGame.getCurrentTurn() == computerGame.getPlayerSide()) {
                boardPanel.resetHighlights();
                boardPanel.repaint();
            }
            sidePanel.getBlackInfoPanel().updateComputerThinking();
            sidePanel.getHistoryScrollPane().refresh();
            soundPlayer.playMoveSound(game);
        });

        game.getHistory().setOnUndo(() -> {
            boardPanel.resetHighlights();
            boardPanel.repaint();
            sidePanel.getBlackInfoPanel().updateComputerThinking();
            sidePanel.getHistoryScrollPane().refresh();
        });

        game.setOnGameEnd(() -> {
            sidePanel.hideGameControlPanel();
            sidePanel.getGameOverPanel().update();
            boardPanel.clearMoveHints();
            boardPanel.repaint();
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



