package chess.ui;

import chess.core.Game;
import chess.util.Notation;

import javax.swing.*;
import javax.swing.border.MatteBorder;
import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Objects;

public class GameOverPanel extends JPanel {

    private Game game;
    private int squareSize;
    private JButton newGameButton;
    private JButton PGNtoClipBoardButton;
    JLabel gameOverLabel;

    public GameOverPanel(Game game, int squareSize) {
        this.setLayout(new GridLayout(3, 1));
        this.setVisible(false);
        this.game = game;
        this.squareSize = squareSize;
        this.setBackground(new Color(50, 50, 50));
        this.setPreferredSize(new Dimension(squareSize * 4, 9 * squareSize / 4));
        this.setMinimumSize(this.getPreferredSize());
        this.setMaximumSize(this.getPreferredSize());
        this.setAlignmentX(LEFT_ALIGNMENT);
        this.setBorder(new MatteBorder(0, 0, 1, 0, new Color(120, 120, 120)));

        JPanel labelPanel = new JPanel(new GridBagLayout());
        labelPanel.setBackground(new Color(50, 50, 50));
        gameOverLabel = new JLabel("", SwingConstants.CENTER);
        gameOverLabel.setFont(new Font("Arial", Font.PLAIN, squareSize / 5));
        gameOverLabel.setForeground(Color.WHITE);
        gameOverLabel.setHorizontalAlignment(SwingConstants.CENTER);
        labelPanel.add(gameOverLabel);
        this.add(labelPanel);

        newGameButton = new JButton("New game");
        this.add(newGameButton);
        newGameButton.setFocusable(false);
        newGameButton.setPreferredSize(new Dimension(squareSize * 4, 3 * squareSize / 4));
        newGameButton.setMinimumSize(newGameButton.getPreferredSize());
        newGameButton.setMaximumSize(newGameButton.getPreferredSize());
        newGameButton.setBackground(new Color(70, 70, 70));
        newGameButton.setFont(new Font("Arial", Font.PLAIN, squareSize / 5));
        newGameButton.setForeground(Color.WHITE);
        newGameButton.setBorderPainted(false);
        newGameButton.addActionListener(e -> {
            NewGameDialog dialog = NewGameDialog.showDialog(squareSize);
            ChessGameGUI newGame = ChessGameGUI.createFromDialog(dialog);
            if (newGame != null) {
                // close previous window
                Window window = SwingUtilities.getWindowAncestor(GameOverPanel.this);
                if (window instanceof JFrame) {
                    window.dispose();
                }
                newGame.setVisible(true);
            }
        });
        newGameButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                newGameButton.setBackground(new Color(100, 100, 100));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                newGameButton.setBackground(new Color(70, 70, 70));
            }
        });
        
        PGNtoClipBoardButton = new JButton("Copy PGN to Clipboard");
        PGNtoClipBoardButton.setFocusable(false);
        PGNtoClipBoardButton.setPreferredSize(new Dimension(squareSize * 4, 3 * squareSize / 4));
        PGNtoClipBoardButton.setMinimumSize(PGNtoClipBoardButton.getPreferredSize());
        PGNtoClipBoardButton.setMaximumSize(PGNtoClipBoardButton.getPreferredSize());
        PGNtoClipBoardButton.setBackground(new Color(70, 70, 70));
        PGNtoClipBoardButton.setFont(new Font("Arial", Font.PLAIN, squareSize / 5));
        PGNtoClipBoardButton.setForeground(Color.WHITE);
        PGNtoClipBoardButton.setBorderPainted(false);
        PGNtoClipBoardButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                PGNtoClipBoardButton.setBackground(new Color(100, 100, 100));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                PGNtoClipBoardButton.setBackground(new Color(70, 70, 70));
            }
        });
        PGNtoClipBoardButton.addActionListener(e -> {
            StringSelection pgn = new StringSelection(Notation.gameToPGN(game));
            Toolkit.getDefaultToolkit().getSystemClipboard().setContents(pgn, null);
        });

        this.add(PGNtoClipBoardButton);
    }

    public void update() {
        String endCause = game.getGameOverCause();
        switch (endCause) {
            case "checkmate" -> {
                if (Objects.equals(game.getWinner(), "white")) {
                    gameOverLabel.setText("Checkmate. White wins.");
                } else {
                    gameOverLabel.setText("Checkmate. Black wins.");
                }
            }
            case "stalemate" -> {
                gameOverLabel.setText("Stalemate. Draw.");
            }
            case "50" -> {
                gameOverLabel.setText("Draw by fifty-move rule");
            }
            case "resign" -> {
                if (Objects.equals(game.getWinner(), "white")) {
                    gameOverLabel.setText("Black resigned. White wins.");
                } else {
                    gameOverLabel.setText("White resigned. Black wins.");
                }
            }
            case "time" -> {
                if (Objects.equals(game.getWinner(), "white")) {
                    gameOverLabel.setText("Black time out. White wins");
                } else {
                    gameOverLabel.setText("White time out. Black wins");
                }
            }
        }
        this.setVisible(true);
    }

}
