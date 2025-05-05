package chess.ui;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class NewGameDialog extends JDialog {
    int squareSize;

    private boolean isComputerGame;
    private boolean isTimedGame;
    private boolean allowUndo;
    private int computerElo;
    private double timeMinutes;

    JCheckBox isTimedGameCheckBox;
    JCheckBox isComputerGameCheckBox;
    JCheckBox allowUndoCheckBox;
    JSlider timeSlider;
    JComboBox<String> difficultyComboBox;
    JLabel timeValueLabel;
    JButton okButton;
    JButton cancelButton;

    double[] timeValues = {
            0.25, 0.5, 0.75, 1, 1.5, 2, 3, 4, 5, 6,
            7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17,
            18, 19, 20, 25, 30, 35, 40, 45, 60, 75, 90,
            105, 120, 135, 150, 165, 180
    };

    String[] difficulties = {"easy", "medium", "hard", "grandmaster", "super grandmaster"};
    int[] correspondingElo = {1320, 1600, 2000, 2500, 2800};

    private boolean confirmed = false;

    Font font;

    public NewGameDialog() {
        initComponents();
        setupLayout();
        setupListeners();
        setDefaultValues();
    }

    public NewGameDialog(int squareSize) {
        this();
        this.squareSize = squareSize;

        // Calculate a reasonable dialog size based on the square size
        int width = squareSize * 5;
        int height = squareSize * 5;
        setSize(width, height);
        setLocationRelativeTo(null);

        // Create a larger font but don't go too extreme
        font = new Font("Arial", Font.PLAIN, squareSize / 6);

        // Apply font to all components
        setUIFont(font);
    }

    // This helper method sets the default font for all Swing components
    private void setUIFont(Font font) {
        // Set default font
        UIManager.put("Button.font", font);
        UIManager.put("ToggleButton.font", font);
        UIManager.put("RadioButton.font", font);
        UIManager.put("CheckBox.font", font);
        UIManager.put("ComboBox.font", font);
        UIManager.put("Label.font", font);
        UIManager.put("List.font", font);
        UIManager.put("TextField.font", font);
        UIManager.put("Panel.font", font);
        UIManager.put("ScrollPane.font", font);
        UIManager.put("TitledBorder.font", font);
        UIManager.put("ComboBox.font", font);

        // Manually apply font to existing components
        isTimedGameCheckBox.setFont(font);
        isComputerGameCheckBox.setFont(font);
        allowUndoCheckBox.setFont(font);
        difficultyComboBox.setFont(font);
        timeValueLabel.setFont(font);
        okButton.setFont(font);
        cancelButton.setFont(font);

        // Update UI to reflect changes
        SwingUtilities.updateComponentTreeUI(this);
    }

    private void initComponents() {
        setTitle("New Game");
        setModal(true);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        isTimedGameCheckBox = new JCheckBox("Timed Game");
        isTimedGameCheckBox.setFocusPainted(false);

        isComputerGameCheckBox = new JCheckBox("Play Against Computer");
        isComputerGameCheckBox.setFocusPainted(false);

        allowUndoCheckBox = new JCheckBox("Allow Undo");
        allowUndoCheckBox.setFocusPainted(false);

        timeSlider = new JSlider(0, timeValues.length - 1);
        timeSlider.setPaintTicks(true);
        timeSlider.setMajorTickSpacing(5);
        timeSlider.setMinorTickSpacing(1);

        timeValueLabel = new JLabel();

        difficultyComboBox = new JComboBox<>(difficulties);

        okButton = new JButton("OK");
        okButton.setFocusPainted(false);
        okButton.setPreferredSize(new Dimension(100, 40));

        cancelButton = new JButton("Cancel");
        cancelButton.setFocusPainted(false);
        cancelButton.setPreferredSize(new Dimension(100, 40));
    }

    private void setupLayout() {
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JPanel computerPanel = new JPanel(new BorderLayout(10, 10));
        computerPanel.add(isComputerGameCheckBox, BorderLayout.NORTH);

        JPanel difficultyPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
            JLabel difficultyLabel = new JLabel("Computer Difficulty:");
        difficultyPanel.add(difficultyLabel);
        difficultyPanel.add(difficultyComboBox);
        computerPanel.add(difficultyPanel, BorderLayout.CENTER);

        JPanel timedGamePanel = new JPanel();
        timedGamePanel.setLayout(new BorderLayout(10, 10));
        timedGamePanel.add(isTimedGameCheckBox, BorderLayout.NORTH);

        JPanel timeSliderPanel = new JPanel(new BorderLayout(5, 5));
        JLabel timeLabel = new JLabel("Time (minutes):");
        timeSliderPanel.add(timeLabel, BorderLayout.NORTH);
        timeSliderPanel.add(timeSlider, BorderLayout.CENTER);
        timeSliderPanel.add(timeValueLabel, BorderLayout.SOUTH);

        timedGamePanel.add(timeSliderPanel, BorderLayout.CENTER);

        JPanel undoPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        undoPanel.add(allowUndoCheckBox);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        buttonPanel.add(cancelButton);
        buttonPanel.add(okButton);

        mainPanel.add(computerPanel);
        mainPanel.add(Box.createVerticalStrut(20));
        mainPanel.add(timedGamePanel);
        mainPanel.add(Box.createVerticalStrut(20));
        mainPanel.add(undoPanel);
        mainPanel.add(Box.createVerticalStrut(20));
        mainPanel.add(buttonPanel);

        setContentPane(mainPanel);
    }

    private void setupListeners() {
        isComputerGameCheckBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateComputerSettings();
            }
        });

        isTimedGameCheckBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateTimeSettings();
            }
        });

        timeSlider.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                updateTimeValueLabel();
            }
        });

        difficultyComboBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateComputerElo();
            }
        });

        okButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                saveSettings();
                confirmed = true;
                dispose();
            }
        });

        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                confirmed = false;
                dispose();
            }
        });
    }

    private void setDefaultValues() {
        isComputerGame = false;
        isTimedGame = false;
        allowUndo = true;
        computerElo = correspondingElo[0];
        timeMinutes = timeValues[13];

        isComputerGameCheckBox.setSelected(isComputerGame);
        isTimedGameCheckBox.setSelected(isTimedGame);
        allowUndoCheckBox.setSelected(allowUndo);
        timeSlider.setValue(13);
        difficultyComboBox.setSelectedIndex(0);

        updateComputerSettings();
        updateTimeSettings();
        updateTimeValueLabel();
    }

    private void updateComputerSettings() {
        isComputerGame = isComputerGameCheckBox.isSelected();
        difficultyComboBox.setEnabled(isComputerGame);
        updateComputerElo();
    }

    private void updateTimeSettings() {
        isTimedGame = isTimedGameCheckBox.isSelected();
        timeSlider.setEnabled(isTimedGame);
        timeValueLabel.setEnabled(isTimedGame);
    }

    private void updateTimeValueLabel() {
        int index = timeSlider.getValue();
        timeMinutes = timeValues[index];
        timeValueLabel.setText(timeMinutes + " min");
    }

    private void updateComputerElo() {
        int index = difficultyComboBox.getSelectedIndex();
        computerElo = correspondingElo[index];
    }

    private void saveSettings() {
        isComputerGame = isComputerGameCheckBox.isSelected();
        isTimedGame = isTimedGameCheckBox.isSelected();
        allowUndo = allowUndoCheckBox.isSelected();
        updateComputerElo();
        updateTimeValueLabel();
    }

    public boolean isConfirmed() {
        return confirmed;
    }

    public boolean isComputerGame() {
        return isComputerGame;
    }

    public boolean isTimedGame() {
        return isTimedGame;
    }

    public boolean isAllowUndo() {
        return allowUndo;
    }

    public int getComputerElo() {
        return computerElo;
    }

    public double getTimeMinutes() {
        return timeMinutes;
    }

    public static NewGameDialog showDialog(int squareSize) {
        NewGameDialog dialog = new NewGameDialog(squareSize);
        dialog.setLocationRelativeTo(null);
        dialog.setVisible(true);
        return dialog;
    }
}