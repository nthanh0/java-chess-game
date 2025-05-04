package ddt.chess.ui.thanh;

import ddt.chess.util.TimerClock;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class TimePanel extends JPanel {
    private TimerClock clock;
    private int squareSize;
    private JLabel timeLabel;
    private Timer timer;

    private final Color backgroundColor = new Color(50, 50, 50);

    public TimePanel(TimerClock clock, int squareSize) {
        this.clock = clock;
        this.squareSize = squareSize;

        this.setLayout(new BorderLayout());
        this.setPreferredSize(new Dimension(2 * squareSize, 3 * squareSize / 4));
        this.setOpaque(false);

        timeLabel = new JLabel(clock.getTimeLeftString(), JLabel.CENTER);
        timeLabel.setFont(new Font("Courier", Font.PLAIN, squareSize / 2));
        timeLabel.setForeground(Color.WHITE);
        timeLabel.setOpaque(true);
        timeLabel.setBackground(backgroundColor);

        this.add(timeLabel, BorderLayout.CENTER);

        this.setMaximumSize(this.getPreferredSize());
        this.setAlignmentX(LEFT_ALIGNMENT);

        // update every 100ms
        timer = new Timer(100, e -> {
            if (clock.isFinished()) {
                stop();
            }
            updateTime();
            highlightClock();
        });
        timer.start();
    }

    public void updateTime() {
        timeLabel.setText(clock.getTimeLeftString());
        updateSizeToFitTextIfNeeded();
        revalidate();
        repaint();
    }

    private void updateSizeToFitTextIfNeeded() {
        FontMetrics fm = timeLabel.getFontMetrics(timeLabel.getFont());
        String currentText = timeLabel.getText();
        int requiredWidth = fm.stringWidth(currentText) + squareSize / 2;
        int currentWidth = this.getWidth();

        if (requiredWidth > currentWidth) {
            int height = fm.getHeight() + 8;
            Dimension newSize = new Dimension(requiredWidth, height);
            this.setMaximumSize(newSize);
            this.setPreferredSize(newSize);
            this.setMinimumSize(newSize);
        }
    }

    public void setSquareSize(int squareSize) {
        this.squareSize = squareSize;
        timeLabel.setFont(new Font("Courier", Font.BOLD, squareSize / 3));
        this.setPreferredSize(new Dimension(2 * squareSize, 3 * squareSize / 4));
        revalidate();
        repaint();
    }

    public void stop() {
        timer.stop();
    }

    public void highlightClock() {
        if (clock.getRemainingTimeMillis() < 10000) {
            timeLabel.setBackground(new Color(100, 50, 50));
        } else if (!clock.isPaused()) {
            timeLabel.setBackground(new Color(50, 100, 50));
        } else {
            timeLabel.setBackground(backgroundColor);
        }
    }

}
