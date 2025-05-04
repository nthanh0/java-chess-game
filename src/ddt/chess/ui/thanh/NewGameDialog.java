package ddt.chess.ui.thanh;

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

    String[] difficulties = {"easy", "medium", "hard", "grandmaster"};
    int[] correspondingElo = {1320, 1600, 2000, 2500};

    private boolean confirmed = false;

    public NewGameDialog() {
        initComponents();
        setupLayout();
        setupListeners();
        setDefaultValues();
    }

    public NewGameDialog(int squareSize) {
        this();
        this.squareSize = squareSize;
        int width = squareSize * 4;
        int height = squareSize * 6;
        setSize(width, height);
        setLocationRelativeTo(null);
    }

    private void initComponents() {
        setTitle("New Game");
        setModal(true);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        getContentPane().setBackground(new Color(70, 70, 70));
        getContentPane().setForeground(Color.WHITE);

        isTimedGameCheckBox = new JCheckBox("Timed Game");
        isComputerGameCheckBox = new JCheckBox("Play Against Computer");
        allowUndoCheckBox = new JCheckBox("Allow Undo");

        timeSlider = new JSlider(0, timeValues.length - 1);
        timeSlider.setPaintTicks(true);
        timeSlider.setMajorTickSpacing(5);
        timeSlider.setMinorTickSpacing(1);

        timeValueLabel = new JLabel();

        difficultyComboBox = new JComboBox<>(difficulties);

        okButton = new JButton("OK");
        cancelButton = new JButton("Cancel");

    }

    private void setupLayout() {
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel settingsPanel = new JPanel();
        settingsPanel.setLayout(new BoxLayout(settingsPanel, BoxLayout.Y_AXIS));

        JPanel gameTypePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        gameTypePanel.add(isComputerGameCheckBox);

        JPanel difficultyPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        difficultyPanel.add(new JLabel("Computer Difficulty:"));
        difficultyPanel.add(difficultyComboBox);

        JPanel timePanel = new JPanel();
        timePanel.setLayout(new GridLayout(4, 1));

        timePanel.add(isTimedGameCheckBox);

        timePanel.add(new JLabel("Time (minutes): "));
        timePanel.add(timeSlider);
        timePanel.add(timeValueLabel);

        JPanel undoPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        undoPanel.add(allowUndoCheckBox);

        settingsPanel.add(gameTypePanel);
        settingsPanel.add(difficultyPanel);
        settingsPanel.add(Box.createVerticalStrut(10));
        settingsPanel.add(timePanel);
        settingsPanel.add(Box.createVerticalStrut(10));
        settingsPanel.add(undoPanel);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.add(okButton);
        buttonPanel.add(cancelButton);

        mainPanel.add(settingsPanel, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        setContentPane(mainPanel);
        pack();
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

    public void setupCheckBox(JCheckBox checkBox) {
        checkBox.setPreferredSize(new Dimension(squareSize / 10, squareSize / 10));
    }

    private void setFont(Component comp, Font font) {
        comp.setFont(font);
        if (comp instanceof Container) {
            for (Component child : ((Container) comp).getComponents()) {
                setFont(child, font);
            }
        }
    }

}