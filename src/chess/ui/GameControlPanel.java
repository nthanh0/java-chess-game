package chess.ui;

import chess.core.ComputerGame;
import chess.core.Game;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class GameControlPanel extends JPanel {
    private Game game;
    private int squareSize;

    private JButton undoButton;
    private JButton resignButton;

    public GameControlPanel(Game game, int squareSize) {
        this.game = game;
        this.squareSize = squareSize;

        this.setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
        this.setPreferredSize(new Dimension(squareSize * 4, 2 * squareSize / 3));
        this.setBackground(new Color(50, 50, 50));
        this.setMaximumSize(this.getPreferredSize());
        this.setAlignmentX(LEFT_ALIGNMENT);
//        this.setBorder(new MatteBorder(0, 0, 1, 0, new Color(120, 120, 120)));

        ToolTipManager.sharedInstance().setInitialDelay(500);
        ToolTipManager.sharedInstance().setDismissDelay(2000);

        undoButton = new JButton("<<");
        undoButton.setToolTipText("Undo");
        undoButton.addActionListener(e -> {
            if (game instanceof ComputerGame computerGame) {
                if (computerGame.getCurrentTurn() == computerGame.getComputerSide()) {
                    computerGame.undoLastMove();
                } else {
                    game.undoLastMove();
                }
            } else {
                game.undoLastMove();
            }
        });
        setupButton(undoButton);

        resignButton = new JButton("\uD83C\uDFF3");
        resignButton.setToolTipText("Resign");
        resignButton.addActionListener(e -> {
            if (game instanceof ComputerGame computerGame) {
                game.resign(computerGame.getPlayerSide());
            } else {
                game.resign(game.getCurrentTurn());
            }
        });
        setupButton(resignButton);

        this.add(Box.createHorizontalGlue());

        this.add(undoButton);
        this.add(resignButton);

        this.add(Box.createHorizontalGlue());

    }

    private void setupButton(JButton button) {
        button.setAlignmentX(CENTER_ALIGNMENT);
        button.setAlignmentY(CENTER_ALIGNMENT);
        button.setBorderPainted(false);
        button.setFont(new Font("Courier", Font.PLAIN,  squareSize / 3));
        button.setFocusable(false);
        button.setForeground(Color.WHITE);
        button.setBackground(null);
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(new Color(120, 120, 120));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(null);
            }
        });
    }

    public void removeUndo() {
        this.remove(undoButton);
    }

}
