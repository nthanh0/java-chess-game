package ddt.chess.ui;

import ddt.chess.core.Game;
import ddt.chess.core.PieceColor;

import javax.swing.*;
import java.awt.*;

public class SidePanel extends JPanel {
    int squareSize;
    final Color sidePanelBackgroundColor = new Color(30, 30, 30);
    HistoryScrollPane historyScrollPane;
    GameOverPanel gameOverPanel;
    GameControlPanel gameControlPanel;
    PlayerInfoPanel blackInfoPanel;

    public SidePanel(Game game, int squareSize, boolean allowUndo, BoardPanel boardPanel) {
        this.setBackground(sidePanelBackgroundColor);
        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        this.setPreferredSize(new Dimension(squareSize * 4, squareSize * 8));
        this.squareSize = squareSize;

        this.add(new SettingsPanel(boardPanel, squareSize));

        this.add(Box.createVerticalGlue()); // spacer

        if (game.isTimedGame()) {
            this.add(new TimePanel(game.getBlackClock(), squareSize));
        }

        blackInfoPanel = new PlayerInfoPanel(game, PieceColor.BLACK, squareSize);
        this.add(blackInfoPanel);

        historyScrollPane = new HistoryScrollPane(game.getHistory(), squareSize);
        this.add(historyScrollPane);

        gameOverPanel = new GameOverPanel(game, squareSize);
        this.add(gameOverPanel);

        gameControlPanel = new GameControlPanel(game, squareSize);
        if (!allowUndo) {
            gameControlPanel.removeUndo();
        }

        this.add(gameControlPanel);

        this.add(new PlayerInfoPanel(game, PieceColor.WHITE, squareSize));


        if (game.isTimedGame()) {
            this.add(new TimePanel(game.getWhiteClock(), squareSize));
        }

        this.add(Box.createVerticalGlue()); // spacer
        this.add(Box.createVerticalStrut(squareSize / 2));
    }

    public HistoryScrollPane getHistoryScrollPane() {
        return historyScrollPane;
    }

    public GameOverPanel getGameOverPanel() {
        return gameOverPanel;
    }

    public void hideGameControlPanel() {
        gameControlPanel.setVisible(false);
    }

    public PlayerInfoPanel getBlackInfoPanel() {
        return blackInfoPanel;
    }
}


