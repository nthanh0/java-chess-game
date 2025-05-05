package ddt.chess.util;

import ddt.chess.core.Piece;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

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

    // Cache for theme preview images
    private final Map<String, Map<String, Image>> pieceThemeCache = new HashMap<>();
    private final Map<String, Image> boardThemeCache = new HashMap<>();

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

    public Image getPieceImage(String themeName, String pieceCode) {
        // Check cache first
        if (pieceThemeCache.containsKey(themeName) &&
                pieceThemeCache.get(themeName).containsKey(pieceCode)) {
            return pieceThemeCache.get(themeName).get(pieceCode);
        }

        // Load the image
        try {
            // Use a smaller preview size for thumbnails in the dialog
            int previewSize = Math.min(squareSize, 64);
            String imagePath = String.format("resources/piece/%s/%dx%d/%s.png",
                    themeName, previewSize, previewSize, pieceCode);

            Image image = ImageIO.read(new File(imagePath));

            // Cache the image
            pieceThemeCache.computeIfAbsent(themeName, k -> new HashMap<>())
                    .put(pieceCode, image);

            return image;
        } catch (IOException e) {
            // Return null if image couldn't be loaded
            return null;
        }
    }


    public Image getBoardThumbnail(String themeName) {
        // Check cache
        if (boardThemeCache.containsKey(themeName)) {
            return boardThemeCache.get(themeName);
        }

        // 1. Try to load the existing thumbnail image first
        try {
            String thumbnailPath = String.format("resources/board/%s/%s.thumbnail.png", themeName, themeName);
            Image thumbnailImage = ImageIO.read(new File(thumbnailPath));
            boardThemeCache.put(themeName, thumbnailImage);
            return thumbnailImage;
        } catch (IOException ignored) {
            // Continue to fallback
        }

        // 2. Fallback: try loading the full board and cropping it
        try {
            String imagePath = String.format("resources/board/%s/%s.png", themeName, themeName);
            BufferedImage originalImage = ImageIO.read(new File(imagePath));
            int thumbnailSize = Math.min(originalImage.getWidth(), originalImage.getHeight()) / 4;
            BufferedImage thumbnail = originalImage.getSubimage(0, 0, thumbnailSize, thumbnailSize);
            boardThemeCache.put(themeName, thumbnail);
            return thumbnail;
        } catch (IOException ignored) {
            // Continue to final fallback
        }

        // 3. Final fallback: placeholder
        int hash = themeName.hashCode();
        Color color = new Color(
                Math.abs(hash) % 200 + 55,
                Math.abs(hash >> 8) % 200 + 55,
                Math.abs(hash >> 16) % 200 + 55
        );
        int tileSize = 8, gridSize = 4;
        BufferedImage placeholder = new BufferedImage(tileSize * gridSize, tileSize * gridSize, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = placeholder.createGraphics();
        Color dark = color.darker(), light = color.brighter();
        for (int i = 0; i < gridSize; i++) {
            for (int j = 0; j < gridSize; j++) {
                g.setColor((i + j) % 2 == 0 ? light : dark);
                g.fillRect(i * tileSize, j * tileSize, tileSize, tileSize);
            }
        }
        g.dispose();
        boardThemeCache.put(themeName, placeholder);
        return placeholder;
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
        if (piece != null && piece.isWhite()) {
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

    public String getBoardTheme() {
        return boardTheme;
    }

    public String getPieceTheme() {
        return pieceTheme;
    }
}