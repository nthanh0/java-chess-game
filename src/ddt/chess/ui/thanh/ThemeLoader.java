package ddt.chess.ui.thanh;

import ddt.chess.core.Piece;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class ThemeLoader {
    String boardTheme, pieceTheme;
    int squareSize;
    
    BufferedImage boardImage;
    
    BufferedImage whitePawnImage;
    BufferedImage whiteKnightImage;
    BufferedImage whiteBishopImage;
    BufferedImage whiteRookImage;
    BufferedImage whiteQueenImage;
    BufferedImage whiteKingImage;
    
    BufferedImage blackPawnImage;
    BufferedImage blackKnightImage;
    BufferedImage blackBishopImage;
    BufferedImage blackRookImage;
    BufferedImage blackQueenImage;
    BufferedImage blackKingImage;
    
    public ThemeLoader(String boardTheme, String pieceTheme, int squareSize) {
        this.boardTheme = boardTheme;
        this.pieceTheme = pieceTheme;
        this.squareSize = squareSize;
        loadImages();
    }

    public void loadImages() {
        String boardImagePath = String.format("resources/board/%s/%s.png", boardTheme, boardTheme);
        Image scaled = null;
        try {
            scaled = ImageIO.read(new File(boardImagePath)).getScaledInstance(squareSize * 8, squareSize * 8, Image.SCALE_SMOOTH);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        boardImage = new BufferedImage(squareSize * 8, squareSize * 8, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = boardImage.createGraphics();
        g2d.drawImage(scaled, 0, 0, null);
        g2d.dispose();

        String pieceImageFolderPath = String.format("resources/piece/%s/%dx%d/", pieceTheme, squareSize, squareSize);
        try {
            whitePawnImage = ImageIO.read(new File(pieceImageFolderPath + "wP.png"));
            whiteKnightImage = ImageIO.read(new File(pieceImageFolderPath + "wN.png"));
            whiteBishopImage = ImageIO.read(new File(pieceImageFolderPath + "wB.png"));
            whiteRookImage = ImageIO.read(new File(pieceImageFolderPath + "wR.png"));
            whiteQueenImage = ImageIO.read(new File(pieceImageFolderPath + "wQ.png"));
            whiteKingImage = ImageIO.read(new File(pieceImageFolderPath + "wK.png"));

            blackPawnImage = ImageIO.read(new File(pieceImageFolderPath + "bP.png"));
            blackKnightImage = ImageIO.read(new File(pieceImageFolderPath + "bN.png"));
            blackBishopImage = ImageIO.read(new File(pieceImageFolderPath + "bB.png"));
            blackRookImage = ImageIO.read(new File(pieceImageFolderPath + "bR.png"));
            blackQueenImage = ImageIO.read(new File(pieceImageFolderPath + "bQ.png"));
            blackKingImage = ImageIO.read(new File(pieceImageFolderPath + "bK.png"));
        }
        catch (IOException e) {
            throw new RuntimeException();
        }
    }

    public void setSquareSize(int squareSize) {
        this.squareSize = squareSize;
        loadImages();
    }

    public void setBoardTheme(String boardTheme) {
        this.boardTheme = boardTheme;
        loadImages();
    }

    public void setPieceTheme(String pieceTheme) {
        this.pieceTheme = pieceTheme;
        loadImages();
    }

    public BufferedImage getBoardImage() {
        return boardImage;
    }

    public BufferedImage getWhitePawnImage() {
        return whitePawnImage;
    }

    public BufferedImage getWhiteKnightImage() {
        return whiteKnightImage;
    }

    public BufferedImage getWhiteBishopImage() {
        return whiteBishopImage;
    }

    public BufferedImage getWhiteRookImage() {
        return whiteRookImage;
    }

    public BufferedImage getWhiteQueenImage() {
        return whiteQueenImage;
    }

    public BufferedImage getWhiteKingImage() {
        return whiteKingImage;
    }

    public BufferedImage getBlackPawnImage() {
        return blackPawnImage;
    }

    public BufferedImage getBlackKnightImage() {
        return blackKnightImage;
    }

    public BufferedImage getBlackBishopImage() {
        return blackBishopImage;
    }

    public BufferedImage getBlackRookImage() {
        return blackRookImage;
    }

    public BufferedImage getBlackQueenImage() {
        return blackQueenImage;
    }

    public BufferedImage getBlackKingImage() {
        return blackKingImage;
    }
    
    public BufferedImage getImageOfPiece(Piece piece) {
        if (piece.isWhite()) {
            switch (piece.getType()) {
                case KNIGHT -> {
                    return whiteKnightImage;
                }
                case BISHOP -> {
                    return whiteBishopImage;
                }
                case ROOK -> {
                    return whiteRookImage;
                }
                case QUEEN -> {
                    return whiteQueenImage;
                }
                case KING -> {
                    return whiteKingImage;
                }
                case PAWN -> {
                    return whitePawnImage;
                }
            }
        } else {
            switch (piece.getType()) {
                case KNIGHT -> {
                    return blackKnightImage;
                }
                case BISHOP -> {
                    return blackBishopImage;
                }
                case ROOK -> {
                    return blackRookImage;
                }
                case QUEEN -> {
                    return blackQueenImage;
                }
                case KING -> {
                    return blackKingImage;
                }
                case PAWN -> {
                    return blackPawnImage;
                }
            }
        }
        return null;
    }
    
}
