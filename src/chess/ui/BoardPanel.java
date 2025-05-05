package chess.ui;


import chess.core.*;
import chess.util.ThemeLoader;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class BoardPanel extends JPanel implements MouseListener {
    private final Game game;
    private final Board board;
    private int squareSize;

    private String boardTheme = "brown";
    private String pieceTheme = "staunty";
    private final ThemeLoader theme;

    private Square fromSquare;
    private final ArrayList<Square> validToSquares = new ArrayList<>();
    private final Map<Square, Color> highlightedSquares = new HashMap<>();
    private Square toSquare;
    
//    public BoardPanel(int SQUARE_SIZE, String boardTheme, String pieceTheme) {
//        this.SQUARE_SIZE = SQUARE_SIZE;
//        this.BOARD_SIZE = SQUARE_SIZE * 8
//        this.boardTheme = boardTheme;
//        this.pieceTheme = pieceTheme;
//        this.addMouseListener(this);
//        this.setPreferredSize(new Dimension(BOARD_SIZE, BOARD_SIZE));
//    }
    public BoardPanel(Game game, int squareSize) {
        theme = new ThemeLoader(boardTheme, pieceTheme, squareSize);
        this.game = game;
        board = game.getBoard();
        board.setupPieces();
        this.squareSize = squareSize;
        this.addMouseListener(this);
        this.setPreferredSize(new Dimension(squareSize * 8, squareSize * 8));
    }

    public void setPieceTheme(String pieceTheme) {
        this.pieceTheme = pieceTheme;
        theme.setPieceTheme(pieceTheme);
        repaint();
    }

        public void setBoardTheme(String boardTheme) {
        this.boardTheme = boardTheme;
        theme.setBoardTheme(boardTheme);
        repaint();
    }

    public Square getSquareFromMouseEvent(MouseEvent e) {
        return board.getSquare(e.getY() / squareSize, e.getX() / squareSize);
    }

    @Override
    public void mousePressed(MouseEvent e) {
        if (game.isOver()) {
            return;
        }

        if (game instanceof ComputerGame computerGame) {
            if (computerGame.getCurrentTurn() != computerGame.getPlayerSide()) {
                return;
            }
        }

        Square clickedSquare = getSquareFromMouseEvent(e);

        if (fromSquare == null) {
            if (clickedSquare.isEmpty()) {
                return;
            }

            if (clickedSquare.getPiece().getColor() != game.getCurrentTurn()) {
                return;
            }

            fromSquare = clickedSquare;
            highlightSquare(fromSquare);
            generateValidToSquares(fromSquare);
        } else {
            toSquare = clickedSquare;

            if (toSquare.isOccupied() &&
                    toSquare.getPiece().getColor() == fromSquare.getPiece().getColor()) {
                highlightedSquares.remove(fromSquare);
                fromSquare = toSquare;
                toSquare = null;
                validToSquares.clear();

                highlightSquare(fromSquare);
                generateValidToSquares(fromSquare);
                repaint();
                return;
            }

            if (fromSquare != null && toSquare != null) {
                Move move = new Move(fromSquare, toSquare);
                boolean isValidMove = game.makeMove(move);

                if (isValidMove) {
                    clearHighlights();
                    highlightSquare(move.getFromSquare());
                    highlightSquare(move.getToSquare());
                    if (game.isOver()) {
                        return;
                    };
                    // execute stockfish move if player has made a valid move
                    if (game instanceof ComputerGame computerGame
                            && computerGame.getCurrentTurn() == computerGame.getComputerSide()) {
                        computerGame.cancelCalculation();
                        new Thread(() -> {
                            Move computerMove = computerGame.executeComputerMove();
                        }).start();
                    }
                } else {
                    highlightedSquares.remove(fromSquare);
                }

                fromSquare = null;
                toSquare = null;
                validToSquares.clear();
            }
        }
        repaint();
    }


    private void highlightSquare(Square square, Color color) {
        highlightedSquares.put(square, color);
    }

    private void highlightSquare(Square square) {
        highlightedSquares.put(square, new Color(255, 255, 100, 100));
    }

    public void drawSquareHighlights(Graphics2D g2D) {
        for (Map.Entry<Square, Color> entry : highlightedSquares.entrySet()) {
            Square square = entry.getKey();
            Color color = entry.getValue();
            int x = square.getY() * squareSize;
            int y = square.getX() * squareSize;
            g2D.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), 128)); // 128 = semi-transparent
            g2D.fillRect(x, y, squareSize, squareSize);
        }
    }

    public void clearHighlights() {
        highlightedSquares.clear();
    }

    public void drawMoveHints(Graphics2D g2D) {
        g2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2D.setColor(new Color(0, 0, 0, 64));

        int dotSize = squareSize / 4;
        int circleSize = 15 * squareSize / 16;
        int circleThickness = squareSize / 16;

        for (Square square : validToSquares) {
            int dotX = square.getY() * squareSize + squareSize / 2 - dotSize / 2;
            int dotY = square.getX() * squareSize + squareSize / 2 - dotSize / 2;
            int circleX = square.getY() * squareSize + squareSize / 2 - circleSize / 2;
            int circleY = square.getX() * squareSize + squareSize / 2 - circleSize / 2;
            if (square.isOccupied()
                && square.getPiece().getColor() != game.getCurrentTurn()) {
                g2D.setStroke(new BasicStroke(circleThickness));
                g2D.drawOval(circleX, circleY, circleSize, circleSize);
            } else {
                g2D.fillOval(dotX, dotY, dotSize, dotSize);
            }
        }
    }

    public void drawCoordinates(Graphics2D g2D) {
        BufferedImage boardImage = theme.getBoardImage();
        int lightSquareRgb = boardImage.getRGB(0, 0);
        int darkSquareRgb = boardImage.getRGB(0, squareSize + 1);
        Color lightSquareColor = new Color(lightSquareRgb);
        Color darkSquareColor = new Color(darkSquareRgb);
        g2D.setFont(new Font("Arial", Font.PLAIN, squareSize / 5));
        g2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        FontMetrics metrics = g2D.getFontMetrics();
        int ascent = metrics.getAscent();
        int descent = metrics.getDescent();
        for (int i = 0; i < 8; i++) {
            if (i % 2 == 0) {
                g2D.setColor(darkSquareColor);
            } else {
                g2D.setColor(lightSquareColor);
            }
            g2D.drawString(String.format("%d", i + 1), squareSize / 16, i * squareSize + ascent);
        }
        for (int i = 0; i < 8; i++) {
            if (i % 2 == 1) {
                g2D.setColor(darkSquareColor);
            } else {
                g2D.setColor(lightSquareColor);
            }
            g2D.drawString(String.format("%c", i + 97), i * squareSize + 5 * squareSize / 6, squareSize * 8 - descent);
        }
    }

    public void generateValidToSquares(Square fromSquare) {
        ArrayList<Move> validMoves = board.generateAllValidMoves(game.getCurrentTurn(), game.getHistory());
        for (Move move : validMoves) {
            if (move.getFromSquare().equals(fromSquare)) {
                validToSquares.add(move.getToSquare());
            }
        }
    }

    @Override
    public void mouseReleased(MouseEvent mouseEvent) {

    }

    @Override
    public void mouseEntered(MouseEvent mouseEvent) {

    }

    @Override
    public void mouseExited(MouseEvent mouseEvent) {

    }

    @Override
    public void mouseClicked(MouseEvent e) {

    }

    public void drawBoard(Graphics2D g2D) {
        g2D.drawImage(theme.getBoardImage(), 0, 0, null);
    }

    public void drawPieces(Graphics2D g2D) {
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                Square square = board.getSquare(i, j);
                if (square != null && square.isOccupied()) {
                    g2D.drawImage(theme.getImageOfPiece(square.getPiece()), j * squareSize, i * squareSize, null);
                }
            }
        }
    }


    @Override
    public void paintComponent(Graphics g) {
        Graphics2D g2D = (Graphics2D)g;
        // draw board image
        drawBoard(g2D);
        drawCoordinates(g2D);
        // draw square highlights
        if (board.isCheck(game.getCurrentTurn())) {
            highlightedSquares.put(board.findKingSquare(game.getCurrentTurn()), new Color(255, 100, 100, 64));
        }
        drawSquareHighlights(g2D);
        // draw piece images
        drawPieces(g2D);
        drawMoveHints(g2D);
    }

    public void setSquareSize(int squareSize) {
        this.squareSize = squareSize;
        this.setPreferredSize(new Dimension(squareSize * 8, squareSize * 8));
        theme.setSquareSize(squareSize);
        repaint();
    }

    public ThemeLoader getThemeLoader() {
        return theme;
    }

    public Game getGame() {
        return game;
    }

    public int getSquareSize() {
        return squareSize;
    }

    // reset highlights to only the last move in history
    public void resetHighlights() {
        highlightedSquares.clear();
        if (!game.getHistory().isEmpty()) {
            highlightSquare(game.getHistory().getLastMove().getToSquare());
            highlightSquare(game.getHistory().getLastMove().getFromSquare());
        }
    }

    public void clearMoveHints() {
        validToSquares.clear();
    }
}
