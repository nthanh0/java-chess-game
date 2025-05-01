package ddt.chess.ui.thanh;

import ddt.chess.core.Game;
import ddt.chess.core.MoveHistory;
import ddt.chess.core.Square;

import javax.swing.*;
import java.awt.*;

public class HistoryPanel extends JPanel {
    private Game game;
    private MoveHistory history;
    private JLabel label;
    private int squareSize;
    public HistoryPanel(Game game, int squareSize) {
        this.squareSize = squareSize;
        this.setPreferredSize(new Dimension(squareSize * 8, squareSize / 3));
        this.setLayout(new BorderLayout());
        this.game = game;
        this.setOpaque(false);
        history = game.getHistory();

        label = new JLabel(history.getUnicodeString(), JLabel.LEFT);
        label.setBackground(new Color(0, 0, 0, 200));
        label.setForeground(new Color(255, 255, 255, 200));
        label.setFont(new Font("Arial", Font.BOLD, squareSize / 5));
        label.setOpaque(true);

        this.add(label, BorderLayout.CENTER);
    }

    public void updateHistory() {
        label.setText(history.getUnicodeString());
        revalidate();
        repaint();
    }

    public void setSquareSize(int squareSize) {
        this.squareSize = squareSize;
        this.setPreferredSize(new Dimension(squareSize * 8, squareSize / 3));
        label.setFont(new Font("Arial", Font.BOLD, squareSize / 5));
        revalidate();
        repaint();
    }
}
