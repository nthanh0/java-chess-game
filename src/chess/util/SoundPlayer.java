package chess.util;

import chess.core.*;

import javax.sound.sampled.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

public class SoundPlayer {
    private String folderPath = "resources/sound/";
    private String checkSound = folderPath + "move-check.wav";
    private String promoteSound = folderPath + "promote.wav";
    private String captureSound = folderPath + "capture.wav";
    private String normalSound = folderPath + "move.wav";
    private String castleSound = folderPath + "castle.wav";
    private String gameEndSound = folderPath + "game-end.wav";

    public void playMoveSound(Game game) {
        Board board = game.getBoard();
        Move move = game.getHistory().getLastMove();
        String soundEffect = null;
        PieceColor opponentColor = (move.getMovingPiece().isWhite()) ? PieceColor.BLACK : PieceColor.WHITE;
        if (board.isCheck(opponentColor)) {
            soundEffect = checkSound;
        } else if (move.getMoveType() == MoveType.PROMOTION) {
            soundEffect = promoteSound;
        } else if (move.getMoveType() == MoveType.CASTLING) {
            soundEffect = castleSound;
        } else {
            if (move.isCapture()) {
                soundEffect = captureSound;
            } else {
                soundEffect = normalSound;
            }
        }
        play(soundEffect);
    }

    public void playGameEndSound() {
        play(gameEndSound);
    }

    public void play(String filePath) {
        File file = new File(filePath);
        try (AudioInputStream audioStream = AudioSystem.getAudioInputStream(file)) {
            Clip clip = AudioSystem.getClip();
            clip.open(audioStream);
            clip.start();
        }
        catch (FileNotFoundException e) {
            System.out.println("File not found");
        }
        catch (UnsupportedAudioFileException e) {
            System.out.println("Unsupported audio file");
        }
        catch (IOException e) {
            System.out.println("Something went wrong");
        }
        catch (LineUnavailableException e) {
            System.out.println("Unable to access audio resource");
        }
    }
}
