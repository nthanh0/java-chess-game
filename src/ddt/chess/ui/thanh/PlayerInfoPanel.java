package ddt.chess.ui.thanh;

import ddt.chess.core.ComputerGame;
import ddt.chess.core.Game;
import ddt.chess.core.PieceColor;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;
import java.awt.*;

public class PlayerInfoPanel extends JPanel {
    JLabel playerInfoLabel;
    Game game;
    PieceColor side;
    public PlayerInfoPanel(Game game, PieceColor side, int squareSize) {
        this.game = game;
        this.side = side;

        this.setLayout(new BorderLayout());
        this.setPreferredSize(new Dimension(squareSize * 4, squareSize / 2));
        this.setMaximumSize(this.getPreferredSize());
        this.setAlignmentX(LEFT_ALIGNMENT);
        this.setBackground(new Color(50, 50, 50));;
        this.setBorder(new MatteBorder(1, 0, 1, 0, new Color(100, 100, 100)));

        if (game instanceof ComputerGame computerGame) {
            if (side == computerGame.getComputerSide()) {
                playerInfoLabel = new JLabel(String.format("Computer (%d)", computerGame.getComputerElo()));
            } else {
                playerInfoLabel = new JLabel("Player");
            }
        } else {
            if (side == PieceColor.WHITE) {
                playerInfoLabel = new JLabel("White");
            } else {
                playerInfoLabel = new JLabel("Black");
            }
        }

        playerInfoLabel.setForeground(Color.WHITE);
        playerInfoLabel.setVerticalTextPosition(SwingConstants.CENTER);
        playerInfoLabel.setPreferredSize(this.getPreferredSize());
        playerInfoLabel.setFont(new Font("Courier", Font.PLAIN, squareSize / 5));
        playerInfoLabel.setBorder(new EmptyBorder(0, squareSize / 6, 0, 0));

        this.add(playerInfoLabel, BorderLayout.CENTER);
    }

    public void updateComputerThinking() {
        if (game instanceof ComputerGame computerGame && side == computerGame.getComputerSide()) {
            if (game.isOver()) {
                playerInfoLabel.setText(String.format("Computer (%d)", computerGame.getComputerElo()));
            }
            if (computerGame.getCurrentTurn() == computerGame.getComputerSide()) {
                playerInfoLabel.setText(String.format("Computer (%d)   ...thinking", computerGame.getComputerElo()));
            } else {
                playerInfoLabel.setText(String.format("Computer (%d)", computerGame.getComputerElo()));
            }
        }
    }
}
