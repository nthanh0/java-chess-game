package ddt.chess.ui.thanh;

import ddt.chess.core.MoveHistory;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;
import javax.swing.plaf.basic.BasicScrollBarUI;
import java.awt.*;

public class HistoryScrollPane extends JScrollPane {
    MoveHistory history;
    JTextArea historyTextArea;
    private int squareSize;

    public HistoryScrollPane(MoveHistory history, int squareSize) {
        this.squareSize = squareSize;
        this.history = history;
        this.setPreferredSize(new Dimension(4 * squareSize, 2 * squareSize));
        this.setMaximumSize(new Dimension(4 * squareSize, 2 * squareSize));
        JScrollBar scrollBar = this.getVerticalScrollBar();
        scrollBar.setBorder(new MatteBorder(0, 1, 0, 0, new Color(120, 120, 120)));

        historyTextArea = new JTextArea(history.getUnicodeString());
        historyTextArea.setForeground(Color.WHITE);
        historyTextArea.setBackground(new Color(50, 50, 50));
        historyTextArea.setLineWrap(true);
        historyTextArea.setFont(new Font("Courier", Font.PLAIN, squareSize / 5));
        historyTextArea.setEditable(false);
        historyTextArea.setCaretColor(new Color(0, 0, 0, 0));
        historyTextArea.setMargin(new Insets(squareSize / 6, squareSize / 6, squareSize / 6, squareSize / 6));

        this.setViewportView(historyTextArea);
        this.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
        this.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        this.setBorder(new EmptyBorder(0, 0, 0, 0));
        this.setAlignmentX(LEFT_ALIGNMENT);
    }

    public void refresh() {
        historyTextArea.setText(history.getHistoryString());
    }

    public void setSquareSize(int squareSize) {
        this.squareSize = squareSize;
        this.setPreferredSize(new Dimension(4 * squareSize, 2 * squareSize));
        this.setMaximumSize(new Dimension(4 * squareSize, 2 * squareSize));
        historyTextArea.setFont(new Font("Arial", Font.PLAIN, squareSize / 5));
        historyTextArea.setMargin(new Insets(squareSize / 6, squareSize / 6, squareSize / 6, squareSize / 6));
        revalidate();
        repaint();
    }
}
