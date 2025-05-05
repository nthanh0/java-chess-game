package chess.ui;

import chess.util.ThemeLoader;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.MatteBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

public class SettingsPanel extends JPanel {
    private JButton settingsButton;
    private int squareSize;
    private BoardPanel boardPanel;

    private static final String[] BOARD_THEMES = {
            "blue-marble", "ic", "green-plastic", "maple", "blue2", "purple",
            "canvas2", "metal", "blue", "pink-pyramid", "wood4", "maple2",
            "wood2", "grey", "horsey", "blue3", "brown", "wood3", "purple-diag",
            "leather", "marble", "olive", "green", "wood"
    };

    private static final String[] PIECE_THEMES = {
            "pixel", "companion", "reillycraig", "chess7", "dubrovny", "staunty",
            "spatial", "kosal", "riohacha", "anarcandy", "cardinal", "california",
            "xkcd", "chessnut", "letter", "kiwen-suwi", "alpha", "monarchy",
            "mpchess", "maestro", "celtic", "gioco", "horsey", "tatiana", "leipzig",
            "icpieces", "pirouetti", "merida", "governor", "rhosgfx", "shapes",
            "mono", "caliente", "cooke", "fantasy", "fresca", "cburnett"
    };

    private final Color menuBackGroundColor = new Color(50, 50, 50);
    private final Color hoverBackgroundColor = new Color(100, 100, 100);
    private final Color selectedBorderColor = new Color(100, 150, 100);
    private final Color selectedBackgroundColor = new Color(100, 150, 100);
    private final Color hoverBorderColor = new Color(150, 150, 150);

    public SettingsPanel(BoardPanel boardPanel, int squareSize) {
        this.boardPanel = boardPanel;
        this.squareSize = squareSize;
        this.setLayout(new FlowLayout(FlowLayout.RIGHT));
        this.setPreferredSize(new Dimension(squareSize * 4, squareSize / 2));
        this.setBackground(null);
        this.setMaximumSize(this.getPreferredSize());
        this.setAlignmentX(LEFT_ALIGNMENT);

        settingsButton = new JButton("\uD83D\uDEE0");
        settingsButton.setFont(new Font("Arial", Font.PLAIN, squareSize / 4));
        settingsButton.setVerticalTextPosition(SwingConstants.CENTER);
        settingsButton.setBackground(null);
        settingsButton.setFocusable(false);
        settingsButton.setBorderPainted(false);
        settingsButton.setForeground(Color.WHITE);
        settingsButton.setToolTipText("Settings");

        // Create popup menu
        JPopupMenu popupMenu = createSettingsMenu();

        settingsButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseExited(MouseEvent e) {
                settingsButton.setBackground(null);
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                settingsButton.setBackground(hoverBackgroundColor);
            }

            @Override
            public void mouseClicked(MouseEvent e) {
                int popupWidth = popupMenu.getPreferredSize().width;
                int buttonWidth = settingsButton.getWidth();
                int xOffset = -popupWidth + buttonWidth;
                int yOffset = settingsButton.getHeight();

                popupMenu.show(settingsButton, xOffset, yOffset);
            }
        });

        this.add(settingsButton);
    }

    private JPopupMenu createSettingsMenu() {
        JPopupMenu menu = CustomGUIComponents.customJPopupMenu();
        menu.setPreferredSize(new Dimension(squareSize * 2, squareSize));

        Font font = new Font("Arial", Font.PLAIN, squareSize / 6);

        JMenuItem boardMenuItem = new JMenuItem("Board...");
        boardMenuItem.setBackground(null);
        boardMenuItem.setBorderPainted(false);
        boardMenuItem.setForeground(Color.WHITE);
        boardMenuItem.setFont(font);
        boardMenuItem.addActionListener(e -> showBoardThemeDialog());

        JMenuItem pieceMenuItem = new JMenuItem("Piece Set...");
        pieceMenuItem.setForeground(Color.WHITE);
        pieceMenuItem.setBackground(null);
        pieceMenuItem.setFont(font);
        pieceMenuItem.addActionListener(e -> showPieceThemeDialog());
        pieceMenuItem.setBorderPainted(false);

        menu.add(boardMenuItem);
        menu.add(pieceMenuItem);

        return menu;
    }

    private void showBoardThemeDialog() {
        String currentTheme = boardPanel.getThemeLoader().getBoardTheme();
        ThemeChooserDialog dialog = new ThemeChooserDialog(
                SwingUtilities.getWindowAncestor(this),
                "Choose Board Theme",
                BOARD_THEMES,
                currentTheme,
                squareSize,
                ThemeType.BOARD
        );

        String selectedTheme = dialog.showDialog();
        if (selectedTheme != null) {
            boardPanel.setBoardTheme(selectedTheme);
        }
    }

    private void showPieceThemeDialog() {
        String currentTheme = boardPanel.getThemeLoader().getPieceTheme();
        ThemeChooserDialog dialog = new ThemeChooserDialog(
                SwingUtilities.getWindowAncestor(this),
                "Choose Piece Set",
                PIECE_THEMES,
                currentTheme,
                squareSize,
                ThemeType.PIECE
        );

        String selectedTheme = dialog.showDialog();
        if (selectedTheme != null) {
            boardPanel.setPieceTheme(selectedTheme);
        }
    }

    enum ThemeType {
        BOARD,
        PIECE
    }

    class ThemeChooserDialog {
        private JDialog dialog;
        private String[] themes;
        private String currentTheme;
        private String selectedTheme;
        private int squareSize;
        private ThemeType themeType;
        private ThemeLoader themeLoader;

        private static final int COLUMNS = 3;

        public ThemeChooserDialog(Window parent, String title, String[] themes,
                                  String currentTheme, int squareSize, ThemeType themeType) {
            this.themes = themes;
            this.currentTheme = currentTheme;
            this.squareSize = squareSize;
            this.themeType = themeType;
            this.themeLoader = boardPanel.getThemeLoader();

            dialog = new JDialog(parent, title, Dialog.ModalityType.APPLICATION_MODAL);
            dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
            dialog.setLayout(new BorderLayout());
            dialog.setResizable(false);

            JPanel gridPanel = createThumbnailGrid();
            JScrollPane scrollPane = new JScrollPane(gridPanel);
            scrollPane.getVerticalScrollBar().setUnitIncrement(20);
            scrollPane.getVerticalScrollBar().setUI(CustomGUIComponents.customScrollBarUI());
            scrollPane.getVerticalScrollBar().setBorder(new MatteBorder(0, 1, 0, 0, new Color(120, 120, 120)));
            scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

            dialog.getContentPane().setBackground(menuBackGroundColor); // dialog background
            scrollPane.getViewport().setBackground(menuBackGroundColor); // scroll background
            gridPanel.setBackground(menuBackGroundColor); // panel behind thumbnails

            dialog.add(scrollPane, BorderLayout.CENTER);

            // Remove the cancel button panel entirely

            int width = COLUMNS * squareSize + squareSize;
            int height = Math.min(400, (int) Math.ceil(themes.length / (double) COLUMNS) * (squareSize + 20));
            dialog.setSize(new Dimension(width, height));
            dialog.setLocationRelativeTo(parent);
        }

        private JPanel createThumbnailGrid() {
            JPanel gridPanel = new JPanel();
            gridPanel.setLayout(new GridLayout(0, COLUMNS, squareSize / 8, squareSize / 8));
            gridPanel.setBorder(new EmptyBorder(squareSize / 6, squareSize / 6, squareSize / 6, squareSize / 6));
            for (String theme : themes) {
                JPanel thumbnailPanel = createThumbnailPanel(theme);
                gridPanel.add(thumbnailPanel);
            }

            return gridPanel;
        }

        private JPanel createThumbnailPanel(String theme) {
            JPanel panel = new JPanel(new BorderLayout());
            panel.setOpaque(true);

            // Set different highlighting styles based on theme type
            if (themeType == ThemeType.BOARD) {
                // For BOARD: use border for highlighting - ensure full border visible
                panel.setBackground(menuBackGroundColor);
                // Use EmptyBorder inside LineBorder to ensure no gap
                if (theme.equals(currentTheme)) {
                    panel.setBorder(BorderFactory.createCompoundBorder(
                            new LineBorder(selectedBorderColor, 5),
                            new EmptyBorder(0, 0, 0, 0)));
                    selectedTheme = theme; // Track selected theme from beginning
                } else {
                    panel.setBorder(BorderFactory.createCompoundBorder(
                            new LineBorder(menuBackGroundColor, 5),
                            new EmptyBorder(0, 0, 0, 0)));
                }
            } else {
                // For PIECE: use background color for highlighting
                panel.setBackground(theme.equals(currentTheme) ? selectedBackgroundColor : menuBackGroundColor);
                if (theme.equals(currentTheme)) {
                    selectedTheme = theme; // Track selected theme from beginning
                }
                panel.setBorder(null);
            }

            // Adjust size for board thumbnails
            int panelWidth = squareSize;
            int panelHeight = squareSize;

            // For board thumbnails, make them square instead of 1x2
            panel.setPreferredSize(new Dimension(panelWidth, panelHeight));

            JLabel label = new JLabel();
            label.setHorizontalAlignment(SwingConstants.CENTER);
            label.setVerticalAlignment(SwingConstants.CENTER);
            label.setPreferredSize(new Dimension(panelWidth, panelHeight));

            try {
                Image image = null;

                if (themeType == ThemeType.BOARD) {
                    image = themeLoader.getBoardThumbnail(theme);
                } else {
                    image = themeLoader.getPieceImage(theme, "wN");
                }

                if (image != null) {
                    // For board thumbnails, crop or resize to make them square
                    if (themeType == ThemeType.BOARD) {
                        BufferedImage thumb = (BufferedImage) themeLoader.getBoardThumbnail(theme);
                        int w = thumb.getWidth() / 2;
                        int h = thumb.getHeight();

                        // Extract light and dark halves
                        BufferedImage light = thumb.getSubimage(0, 0, w, h);
                        BufferedImage dark = thumb.getSubimage(w, 0, w, h);

                        // Create 2x2 composite
                        BufferedImage composite = new BufferedImage(w * 2, h * 2, BufferedImage.TYPE_INT_ARGB);
                        Graphics2D g = composite.createGraphics();

                        g.drawImage(light, 0, 0, null);       // top-left
                        g.drawImage(dark, w, 0, null);        // top-right
                        g.drawImage(dark, 0, h, null);        // bottom-left
                        g.drawImage(light, w, h, null);       // bottom-right

                        g.dispose();

                        Image scaled = composite.getScaledInstance(panelWidth, panelHeight, Image.SCALE_SMOOTH);
                        label.setIcon(new ImageIcon(scaled));
                    } else {
                        // For piece thumbnails, maintain aspect ratio
                        int imgW = image.getWidth(null);
                        int imgH = image.getHeight(null);
                        double aspect = (double) imgW / imgH;

                        int targetW = panelWidth;
                        int targetH = (int) (panelWidth / aspect);
                        if (targetH > panelHeight) {
                            targetH = panelHeight;
                            targetW = (int) (panelHeight * aspect);
                        }

                        Image scaledImage = image.getScaledInstance(targetW, targetH, Image.SCALE_SMOOTH);
                        label.setIcon(new ImageIcon(scaledImage));
                    }
                    label.setText("");
                } else {
                    label.setText(theme);
                }
            } catch (Exception e) {
                label.setText(theme);
            }

            label.setToolTipText(theme);

            label.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    selectedTheme = theme;
                    if (themeType == ThemeType.BOARD) {
                        boardPanel.setBoardTheme(selectedTheme);
                    } else {
                        boardPanel.setPieceTheme(selectedTheme);
                    }

                    // Re-highlight all panels
                    Component[] components = ((JPanel) label.getParent().getParent()).getComponents();
                    for (Component comp : components) {
                        if (comp instanceof JPanel) {
                            JPanel pnl = (JPanel) comp;
                            if (themeType == ThemeType.BOARD) {
                                // For board themes: use border with compound border to prevent gaps
                                pnl.setBorder(BorderFactory.createCompoundBorder(
                                        new LineBorder(menuBackGroundColor, 5),
                                        new EmptyBorder(0, 0, 0, 0)));
                            } else {
                                // For piece themes: use background
                                pnl.setBackground(menuBackGroundColor);
                            }
                        }
                    }

                    // Apply correct highlighting for selected theme
                    if (themeType == ThemeType.BOARD) {
                        panel.setBorder(BorderFactory.createCompoundBorder(
                                new LineBorder(selectedBorderColor, 3),
                                new EmptyBorder(0, 0, 0, 0)));
                    } else {
                        panel.setBackground(selectedBackgroundColor);
                    }
                }

                @Override
                public void mouseEntered(MouseEvent e) {
                    if (themeType == ThemeType.BOARD) {
                        // Only change border if not already selected
                        if (selectedTheme == null || !selectedTheme.equals(theme)) {
                            panel.setBorder(BorderFactory.createCompoundBorder(
                                    new LineBorder(hoverBorderColor, 3),
                                    new EmptyBorder(0, 0, 0, 0)));
                        }
                        // Keep the selected border if this is the selected theme
                        else if (selectedTheme != null && selectedTheme.equals(theme)) {
                            panel.setBorder(BorderFactory.createCompoundBorder(
                                    new LineBorder(selectedBorderColor, 3),
                                    new EmptyBorder(0, 0, 0, 0)));
                        }
                    } else {
                        // For piece themes: maintain selection highlight
                        if (selectedTheme != null && selectedTheme.equals(theme)) {
                            panel.setBackground(selectedBackgroundColor);
                        } else {
                            panel.setBackground(new Color(120, 120, 120));
                        }
                    }
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    if (themeType == ThemeType.BOARD) {
                        // Return to appropriate border
                        if (selectedTheme != null && selectedTheme.equals(theme)) {
                            panel.setBorder(BorderFactory.createCompoundBorder(
                                    new LineBorder(selectedBorderColor, 3),
                                    new EmptyBorder(0, 0, 0, 0)));
                        } else {
                            panel.setBorder(BorderFactory.createCompoundBorder(
                                    new LineBorder(menuBackGroundColor, 3),
                                    new EmptyBorder(0, 0, 0, 0)));
                        }
                    } else {
                        // Return to appropriate background
                        if (selectedTheme != null && selectedTheme.equals(theme)) {
                            panel.setBackground(selectedBackgroundColor);
                        } else {
                            panel.setBackground(menuBackGroundColor);
                        }
                    }
                }
            });

            panel.add(label, BorderLayout.CENTER);

            // Remove the name label

            return panel;
        }

        public String showDialog() {
            dialog.setVisible(true);
            return selectedTheme;
        }
    }
}