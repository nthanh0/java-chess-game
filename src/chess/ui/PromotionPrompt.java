package chess.ui;

import chess.core.PieceColor;
import chess.core.PieceType;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class PromotionPrompt extends JDialog {

    int squareSize;
    PieceType result = null;
    final Color defaultBackgroundColor = new Color(255, 255, 255);
    private AWTEventListener clickOutsideListener;

    public PromotionPrompt(BoardPanel boardPanel) {
        this.squareSize = boardPanel.getSquareSize();
        this.setUndecorated(true);
        this.setFocusable(false);
        this.setBackground(defaultBackgroundColor);
        setLayout(new GridLayout(1, 4));

        // esc to close dialog
        this.getRootPane().registerKeyboardAction(
                e -> dispose(),
                KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0),
                JComponent.WHEN_IN_FOCUSED_WINDOW
        );

        JButton knightOption, bishopOption, rookOption, queenOption;
        if (boardPanel.getGame().getCurrentTurn() == PieceColor.WHITE) {
            knightOption = new JButton(new ImageIcon(boardPanel.getThemeLoader().getWhiteKnightImage()));
            bishopOption = new JButton(new ImageIcon(boardPanel.getThemeLoader().getWhiteBishopImage()));
            rookOption = new JButton(new ImageIcon(boardPanel.getThemeLoader().getWhiteRookImage()));
            queenOption = new JButton(new ImageIcon(boardPanel.getThemeLoader().getWhiteQueenImage()));
        } else {
            knightOption = new JButton(new ImageIcon(boardPanel.getThemeLoader().getBlackKnightImage()));
            bishopOption = new JButton(new ImageIcon(boardPanel.getThemeLoader().getBlackBishopImage()));
            rookOption = new JButton(new ImageIcon(boardPanel.getThemeLoader().getBlackRookImage()));
            queenOption = new JButton(new ImageIcon(boardPanel.getThemeLoader().getBlackQueenImage()));
        }

        knightOption.addActionListener(e -> {
            result = PieceType.KNIGHT;
            dispose();
        });
        bishopOption.addActionListener(e -> {
            result = PieceType.BISHOP;
            dispose();
        });
        rookOption.addActionListener(e -> {
            result = PieceType.ROOK;
            dispose();
        });
        queenOption.addActionListener(e -> {
            result = PieceType.QUEEN;
            dispose();
        });


        setupButton(knightOption);
        setupButton(bishopOption);
        setupButton(rookOption);
        setupButton(queenOption);

        this.add(knightOption);
        this.add(bishopOption);
        this.add(rookOption);
        this.add(queenOption);

        setupClickOutsideListener();

        this.setModal(true);
        this.pack();
        this.setLocationRelativeTo(boardPanel);
        getRootPane().setBorder(BorderFactory.createLineBorder(Color.BLACK));
    }

    public void setupButton(JButton button) {
        button.setFocusable(false);
        button.setBackground(defaultBackgroundColor);
        button.setBorder(null);
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(new Color(0, 0,0, 50));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(defaultBackgroundColor);
            }
        });
    }

    public PieceType getResult() {
        return result;
    }

    private void setupClickOutsideListener() {
        clickOutsideListener = event -> {
            if (event instanceof MouseEvent) {
                MouseEvent mouseEvent = (MouseEvent) event;
                // Check if the click is outside this dialog
                if (mouseEvent.getID() == MouseEvent.MOUSE_PRESSED &&
                        SwingUtilities.getWindowAncestor(mouseEvent.getComponent()) != this) {
                    // Click was outside the dialog
                    dispose();
                }
            }
        };

        // Add global listener when dialog becomes visible
        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowOpened(WindowEvent e) {
                Toolkit.getDefaultToolkit().addAWTEventListener(
                        clickOutsideListener, AWTEvent.MOUSE_EVENT_MASK);
            }

            @Override
            public void windowClosed(WindowEvent e) {
                // Remove listener when dialog closes
                Toolkit.getDefaultToolkit().removeAWTEventListener(clickOutsideListener);
            }
        });

        // Add escape key to close dialog
        this.getRootPane().registerKeyboardAction(
                e -> dispose(),  // Close dialog without setting result
                KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0),
                JComponent.WHEN_IN_FOCUSED_WINDOW
        );
    }

}
