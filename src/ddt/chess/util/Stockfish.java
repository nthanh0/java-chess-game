package ddt.chess.util;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class Stockfish {
    private Process engineProcess;
    private BufferedReader processReader;
    private BufferedWriter processWriter;
    private boolean isUCIReady = false;
    private Map<String, String> positionCache = new HashMap<>();
    private boolean useHash = true;
    private int hashSizeMB = 1024; // Default hash size
    private int threads = Runtime.getRuntime().availableProcessors(); // Use all available cores by default

    /**
     * Starts the Stockfish engine process
     * @param path Path to the Stockfish executable
     * @return True if engine started successfully
     */
    public boolean startEngine(String path) {
        try {
            engineProcess = new ProcessBuilder(path).redirectErrorStream(true).start();
            processReader = new BufferedReader(new InputStreamReader(engineProcess.getInputStream()));
            processWriter = new BufferedWriter(new OutputStreamWriter(engineProcess.getOutputStream()));

            // Initialize engine with UCI protocol
            isUCIReady = sendCommand("uci") && waitFor("uciok");

            // Apply performance optimizations
            if (isUCIReady) {
                optimizeEngine();
            }

            return isUCIReady;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Apply performance optimizations to the engine
     */
    private void optimizeEngine() {
        // Set thread count to use all available cores
        sendCommand("setoption name Threads value " + threads);

        // Set hash table size (in MB)
        if (useHash) {
            sendCommand("setoption name Hash value " + hashSizeMB);
        }

        // Enable or disable Syzygy tablebases if available
        // sendCommand("setoption name SyzygyPath value path/to/tablebases");

        // Turn off Ponder (thinking on opponent's time)
        sendCommand("setoption name Ponder value false");

        // Initialize the engine
        sendCommand("isready");
        waitFor("readyok");
    }

    /**
     * Stops the engine process
     */
    public void stopEngine() {
        try {
            sendCommand("quit");
            processReader.close();
            processWriter.close();
            if (engineProcess.isAlive()) {
                engineProcess.waitFor(1, TimeUnit.SECONDS);
                if (engineProcess.isAlive()) {
                    engineProcess.destroyForcibly();
                }
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * Send a command to the engine
     * @param command Command string
     * @return True if command was sent successfully
     */
    public boolean sendCommand(String command) {
        try {
            processWriter.write(command + "\n");
            processWriter.flush();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Get output from the engine with timeout
     * @param waitTime Time to wait in milliseconds
     * @return Output from engine
     */
    public String getOutput(long waitTime) {
        StringBuilder sb = new StringBuilder();
        try {
            // Wait for initial time
            Thread.sleep(Math.min(waitTime, 100));

            // Start time measurement
            long startTime = System.currentTimeMillis();
            long endTime = startTime + waitTime;

            // Keep reading until timeout or no more data
            while (System.currentTimeMillis() < endTime) {
                if (processReader.ready()) {
                    String line = processReader.readLine();
                    if (line != null) {
                        sb.append(line).append("\n");

                        // If we found the bestmove, we can exit early
                        if (line.startsWith("bestmove")) {
                            break;
                        }
                    }
                } else {
                    // Small sleep to prevent CPU spinning
                    Thread.sleep(5);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return sb.toString();
    }

    /**
     * Wait for a specific keyword in the engine output
     * @param keyword Keyword to wait for
     * @return True if keyword was found
     */
    private boolean waitFor(String keyword) {
        try {
            String text;
            while ((text = processReader.readLine()) != null) {
                if (text.contains(keyword)) {
                    return true;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Get the best move for a position with fixed thinking time
     * @param fen FEN position string
     * @param waitTime Thinking time in milliseconds
     * @return Best move in UCI format
     */
    public String getBestMove(String fen, int waitTime) {
        // Check cache first
        if (useHash && positionCache.containsKey(fen + waitTime)) {
            return positionCache.get(fen + waitTime);
        }

        // Make sure engine is ready
        sendCommand("isready");
        waitFor("readyok");

        // Set position and start calculation
        sendCommand("position fen " + fen);
        sendCommand("go movetime " + waitTime);

        // Get output and parse for best move
        String output = getOutput(waitTime + 100);
        for (String line : output.split("\n")) {
            if (line.startsWith("bestmove")) {
                String bestMove = line.split(" ")[1];

                // Cache the result
                if (useHash) {
                    positionCache.put(fen + waitTime, bestMove);
                }

                return bestMove;
            }
        }
        return null;
    }

    /**
     * Get the best move with full engine info (for debugging/analysis)
     * @param fen FEN position string
     * @param waitTime Thinking time in milliseconds
     * @return Full engine output including best move
     */
    public String getBestMoveWithInfo(String fen, int waitTime) {
        // Make sure engine is ready
        sendCommand("isready");
        waitFor("readyok");

        // Set position and start calculation
        sendCommand("position fen " + fen);
        sendCommand("go movetime " + waitTime);

        // Get full output
        return getOutput(waitTime + 100);
    }

    /**
     * Set the ELO level of the engine
     * @param elo ELO rating (1350-2850)
     * @return True if successful
     */
    public boolean setEloLevel(int elo) {
        if (elo < 1350 || elo > 2850) {
            System.out.println("Elo must be between 1350 and 2850.");
            return false;
        }

        // Make sure engine is ready
        sendCommand("isready");
        waitFor("readyok");

        // Set UCI_LimitStrength and UCI_Elo
        boolean success = sendCommand("setoption name UCI_LimitStrength value true")
                && sendCommand("setoption name UCI_Elo value " + elo);

        // Wait for engine to be ready again
        sendCommand("isready");
        waitFor("readyok");

        return success;
    }

    /**
     * Get best move with time management for timed games
     * @param fen FEN position string
     * @param whiteTimeMs White's remaining time in milliseconds
     * @param blackTimeMs Black's remaining time in milliseconds
     * @return Best move in UCI format
     */
    public String getBestMoveWithTimeManagement(String fen, long whiteTimeMs, long blackTimeMs) {
        // Make sure engine is ready
        sendCommand("isready");
        waitFor("readyok");

        // Set position and start calculation with time control
        sendCommand("position fen " + fen);

        // Calculate increment and moves to go
        int incrementMs = 0; // Add increment if your game uses it
        int movesToGo = 40;  // Typical time control assumption

        // Send time control command
        sendCommand("go wtime " + whiteTimeMs + " btime " + blackTimeMs +
                " winc " + incrementMs + " binc " + incrementMs +
                " movestogo " + movesToGo);

        // Determine maximum wait time (don't wait longer than 1/10th of available time)
        boolean isWhiteTurn = fen.contains(" w ");
        long availableTime = isWhiteTurn ? whiteTimeMs : blackTimeMs;
        long maxWaitTime = Math.min(availableTime / 10, 30000); // Max 30 seconds

        // Get output with timeout
        String output = getOutput(maxWaitTime);
        for (String line : output.split("\n")) {
            if (line.startsWith("bestmove")) {
                return line.split(" ")[1];
            }
        }

        // If we didn't get a bestmove, force engine to move
        sendCommand("stop");
        output = getOutput(100);
        for (String line : output.split("\n")) {
            if (line.startsWith("bestmove")) {
                return line.split(" ")[1];
            }
        }

        return null;
    }

    /**
     * Set the hash table size in MB
     * @param sizeInMB Size in megabytes
     */
    public void setHashSize(int sizeInMB) {
        this.hashSizeMB = sizeInMB;
        if (isUCIReady) {
            sendCommand("setoption name Hash value " + hashSizeMB);
            // Wait for ready confirmation
            sendCommand("isready");
            waitFor("readyok");
        }
    }

    /**
     * Set the number of threads/cores for Stockfish to use
     * @param numThreads Number of threads
     */
    public void setThreads(int numThreads) {
        if (numThreads > 0) {
            this.threads = numThreads;
            if (isUCIReady) {
                sendCommand("setoption name Threads value " + threads);
                // Wait for ready confirmation
                sendCommand("isready");
                waitFor("readyok");
            }
        }
    }

    /**
     * Clear the transposition table
     */
    public void clearHash() {
        if (isUCIReady) {
            sendCommand("setoption name Clear Hash value true");
            // Wait for ready confirmation
            sendCommand("isready");
            waitFor("readyok");
        }
    }

    /**
     * Enable or disable position caching
     * @param useCache True to enable caching
     */
    public void setUseCache(boolean useCache) {
        this.useHash = useCache;
        if (!useCache) {
            positionCache.clear();
        }
    }
}