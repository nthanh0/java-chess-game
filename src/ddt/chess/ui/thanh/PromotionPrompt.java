package ddt.chess.ui.thanh;

import ddt.chess.core.PieceColor;
import ddt.chess.core.PieceType;
import ddt.chess.core.Square;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class PromotionPrompt extends JDialog {

    int squareSize;
    PieceType result;
    final Color defaultBackgroundColor = new Color(0, 0, 0, 50);
    int hoveredOption = -1;

    public PromotionPrompt(ThemeLoader theme, PieceColor currentTurn, int squareSize) {
        this.squareSize = squareSize;
        this.setUndecorated(true);
        this.setFocusable(false);
        this.setBackground(defaultBackgroundColor);
        setLayout(new GridLayout(1, 4));
        JButton knightOption, bishopOption, rookOption, queenOption;
        if (currentTurn == PieceColor.WHITE) {
            knightOption = new JButton(new ImageIcon(theme.getWhiteKnightImage()));
            bishopOption = new JButton(new ImageIcon(theme.getWhiteBishopImage()));
            rookOption = new JButton(new ImageIcon(theme.getWhiteRookImage()));
            queenOption = new JButton(new ImageIcon(theme.getWhiteQueenImage()));
        } else {
            knightOption = new JButton(new ImageIcon(theme.getBlackKnightImage()));
            bishopOption = new JButton(new ImageIcon(theme.getBlackBishopImage()));
            rookOption = new JButton(new ImageIcon(theme.getBlackRookImage()));
            queenOption = new JButton(new ImageIcon(theme.getBlackQueenImage()));
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


        setupButton(knightOption, 0);
        setupButton(bishopOption, 1);
        setupButton(rookOption, 2);
        setupButton(queenOption, 3);

        this.add(knightOption);
        this.add(bishopOption);
        this.add(rookOption);
        this.add(queenOption);
        this.setLocationRelativeTo(null);
    }

    public void setupButton(JButton button, int optionIndex) {
        button.setOpaque(true);
        button.setFocusable(false);
        button.setBorder(null);
        button.setRolloverEnabled(false);
        button.setBackground(defaultBackgroundColor);
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                hoveredOption = optionIndex;
                repaint();
            }

            @Override
            public void mouseExited(MouseEvent e) {
                hoveredOption = -1;
                repaint();
            }
        });
    }

    public PieceType getResult() {
        return result;
    }

    public void drawHoverEffect(Graphics2D g2D) {
        g2D.setColor(new Color(0, 0, 0, 100));
        g2D.fillRect(hoveredOption * squareSize, 0, squareSize, squareSize);
    }


    @Override
    public void paint(Graphics g) {
        super.paint(g);
        Graphics2D g2D = (Graphics2D) g;
        drawHoverEffect(g2D);
    }
}
