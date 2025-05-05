package chess.ui;

import javax.swing.*;
import javax.swing.border.MatteBorder;
import javax.swing.plaf.basic.BasicScrollBarUI;
import java.awt.*;

public class CustomGUIComponents {
    public static BasicScrollBarUI customScrollBarUI() {
        BasicScrollBarUI customScrollBarUI = new BasicScrollBarUI() {
            @Override
            protected void configureScrollBarColors() {
                this.thumbColor = new Color(120, 120, 120); // scrollbar handle
                this.trackColor = new Color(50, 50, 50);    // scrollbar background

            }
            @Override
            protected JButton createDecreaseButton(int orientation) {
                JButton button = new JButton();
                button.setPreferredSize(new Dimension(0, 0));
                return button;
            }

            @Override
            protected JButton createIncreaseButton(int orientation) {
                JButton button = new JButton();
                button.setPreferredSize(new Dimension(0, 0));
                return button;
            }
        };
        return customScrollBarUI;
    }

    public static JPopupMenu customJPopupMenu() {
        final Color BACKGROUND_COLOR = new Color(50, 50, 50);
        final Color BORDER_COLOR = new Color(100, 100, 100);
        JPopupMenu popupMenu = new JPopupMenu();
        popupMenu.setBackground(BACKGROUND_COLOR);
        popupMenu.setBorder(new MatteBorder(1, 1, 1, 1, BORDER_COLOR));
        return popupMenu;
    }
}
