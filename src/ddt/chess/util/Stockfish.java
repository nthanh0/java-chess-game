package ddt.chess.util;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class Stockfish {
    private Process engineProcess;
    private BufferedReader processReader;
    private BufferedWriter processWriter;
    private boolean isReady = false;
    private Map<String, String> positionCache = new HashMap<>();
    private boolean useHash = true;
    private int hashSizeMB = 256; //
    private int elo = 1320;
    private int threads = Runtime.getRuntime().availableProcessors() - 1;

    public boolean startEngine(String path) {
        try {
            engineProcess = new ProcessBuilder(path).redirectErrorStream(true).start();
            processReader = new BufferedReader(new InputStreamReader(engineProcess.getInputStream()));
            processWriter = new BufferedWriter(new OutputStreamWriter(engineProcess.getOutputStream()));

            isReady = sendCommand("uci") && waitFor("uciok");

            if (isReady) {
                optimizeEngine();
            }

            return isReady;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    private void optimizeEngine() {
        // Set thread count to use all available cores
        sendCommand("setoption name Threads value " + threads);

        // Set hash table size (in MB)
        if (useHash) {
            sendCommand("setoption name Hash value " + hashSizeMB);
        }

        // turn off ponder (thinking for the opponent's move)
        sendCommand("setoption name Ponder value false");

        // Initialize the engine
        sendCommand("isready");
        waitFor("readyok");
    }

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

    public String getOutput(long waitTime) {
        StringBuilder sb = new StringBuilder();
        try {
            // wait for initial time
            Thread.sleep(Math.min(waitTime, 100));

            // start time measurement
            long startTime = System.currentTimeMillis();
            long endTime = startTime + waitTime;

            // keep reading until timeout or no more data
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
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return sb.toString();
    }

    // wait for specific keyword in stockfish's output
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

    public String getBestMove(String fen, int waitTime) {
        // check cache
        if (useHash && positionCache.containsKey(fen + waitTime)) {
            return positionCache.get(fen + waitTime);
        }

        // pass in position and calculate
        sendCommand("position fen " + fen);
        sendCommand("go movetime " + waitTime);

        // get output and parse for best move
        String output = getOutput(waitTime + 100);
        for (String line : output.split("\n")) {
            if (line.startsWith("bestmove")) {
                String bestMove = line.split(" ")[1];

                // cache the result
                if (useHash) {
                    positionCache.put(fen + waitTime, bestMove);
                }

                return bestMove;
            }
        }
        return null;
    }

    public boolean setEloLevel(int elo) {
        if (elo < 1320 || elo > 3190) {
            elo = 1320;
            this.elo = elo;
        }
        this.elo = elo;

        // set UCI_LimitStrength and UCI_Elo
        boolean success = sendCommand("setoption name UCI_LimitStrength value true")
                && sendCommand("setoption name UCI_Elo value " + elo);

        return success;
    }

    public String getBestMoveWithTimeManagement(String fen, long whiteTimeMs, long blackTimeMs) {
        // pass in fen
        sendCommand("position fen " + fen);

        // pass in time and calculate
        sendCommand("go wtime " + whiteTimeMs + " btime " + blackTimeMs);

        // maximum wait time
        boolean isWhiteTurn = fen.contains(" w ");
        long availableTime = isWhiteTurn ? whiteTimeMs : blackTimeMs;
        long maxWaitTime = Math.min(availableTime / 10, 15000); // max 30 seconds

        // Get output with timeout
        String output = getOutput(maxWaitTime);
        for (String line : output.split("\n")) {
            if (line.startsWith("bestmove")) {
                return line.split(" ")[1];
            }
        }

        // stop if time exceeds maxWaitTime
        sendCommand("stop");
        output = getOutput(100);
        for (String line : output.split("\n")) {
            if (line.startsWith("bestmove")) {
                return line.split(" ")[1];
            }
        }

        return null;
    }

    public String getBestMoveWithSimulatedTime(String fen, long simulatedTimeMs) {
        return getBestMoveWithTimeManagement(fen, simulatedTimeMs, simulatedTimeMs);
    }

    public void setHashSize(int sizeInMB) {
        this.hashSizeMB = sizeInMB;
        if (isReady) {
            sendCommand("setoption name Hash value " + hashSizeMB);
        }
    }

    public void setThreads(int numThreads) {
        if (numThreads > 0) {
            this.threads = numThreads;
            if (isReady) {
                sendCommand("setoption name Threads value " + threads);
                // Wait for ready confirmation
                sendCommand("isready");
                waitFor("readyok");
            }
        }
    }

    public void clearHash() {
        if (isReady) {
            sendCommand("setoption name Clear Hash value true");
            // Wait for ready confirmation
            sendCommand("isready");
            waitFor("readyok");
        }
    }

    public void setUseCache(boolean useCache) {
        this.useHash = useCache;
        if (!useCache) {
            positionCache.clear();
        }
    }

    public int getElo() {
        return elo;
    }

    public void stopCalculation() {
        sendCommand("stop");
    }
}