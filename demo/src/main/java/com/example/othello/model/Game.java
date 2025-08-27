package com.example.othello.model;

import java.util.Arrays;

public class Game {
    private final String gameId;
    private final int[][] board = new int[8][8];
    private int currentPlayer = 1; // 1=黒(人間)先手
    private boolean finished = false;
    private Integer winner = null; // null=draw
    private final int human = 1;
    private final int cpu = 2;

    public Game(String gameId) {
        this.gameId = gameId;
        for (int y = 0; y < 8; y++) Arrays.fill(board[y], 0);
        // 初期配置
        board[3][3] = 2; board[4][4] = 2; // 白
        board[3][4] = 1; board[4][3] = 1; // 黒
    }

    public String getGameId() { return gameId; }
    public int[][] getBoard() { return board; }
    public int getCurrentPlayer() { return currentPlayer; }
    public void setCurrentPlayer(int p) { this.currentPlayer = p; }
    public boolean isFinished() { return finished; }
    public void setFinished(boolean f) { this.finished = f; }
    public Integer getWinner() { return winner; }
    public void setWinner(Integer w) { this.winner = w; }
    public int getHuman() { return human; }
    public int getCpu() { return cpu; }
}