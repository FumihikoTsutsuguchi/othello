package com.example.othello.rules;

import java.util.*;

public class OthelloRules {
    public record Move(int x, int y) {}

    private static final int[] DX = {-1,-1,-1,0,0,1,1,1};
    private static final int[] DY = {-1,0,1,-1,1,-1,0,1};

    public static boolean inBoard(int x, int y) {
        return x >= 0 && x < 8 && y >= 0 && y < 8;
    }

    public static boolean isLegal(int[][] b, int player, int x, int y) {
        if (!inBoard(x, y) || b[y][x] != 0) return false;
        int opp = (player == 1) ? 2 : 1;
        for (int d = 0; d < 8; d++) {
            int cx = x + DX[d], cy = y + DY[d];
            boolean seenOpp = false;
            while (inBoard(cx, cy) && b[cy][cx] == opp) {
                seenOpp = true; cx += DX[d]; cy += DY[d];
            }
            if (seenOpp && inBoard(cx, cy) && b[cy][cx] == player) return true;
        }
        return false;
    }

    public static List<Move> legalMoves(int[][] b, int player) {
        List<Move> list = new ArrayList<>();
        for (int y = 0; y < 8; y++) {
            for (int x = 0; x < 8; x++) {
                if (isLegal(b, player, x, y)) list.add(new Move(x, y));
            }
        }
        return list;
    }

    public static boolean hasAnyMove(int[][] b, int player) {
        return !legalMoves(b, player).isEmpty();
    }

    public static int flipsIf(int[][] b, int player, int x, int y) {
        if (!isLegal(b, player, x, y)) return 0;
        int opp = (player == 1) ? 2 : 1;
        int total = 0;
        for (int d = 0; d < 8; d++) {
            int cx = x + DX[d], cy = y + DY[d];
            int count = 0;
            while (inBoard(cx, cy) && b[cy][cx] == opp) {
                count++; cx += DX[d]; cy += DY[d];
            }
            if (count > 0 && inBoard(cx, cy) && b[cy][cx] == player) total += count;
        }
        return total;
    }

    public static void applyMove(int[][] b, int player, int x, int y) {
        if (!isLegal(b, player, x, y)) throw new IllegalArgumentException("illegal move");
        int opp = (player == 1) ? 2 : 1;
        b[y][x] = player;
        for (int d = 0; d < 8; d++) {
            int cx = x + DX[d], cy = y + DY[d];
            List<int[]> toFlip = new ArrayList<>();
            while (inBoard(cx, cy) && b[cy][cx] == opp) {
                toFlip.add(new int[]{cx, cy});
                cx += DX[d]; cy += DY[d];
            }
            if (!toFlip.isEmpty() && inBoard(cx, cy) && b[cy][cx] == player) {
                for (int[] p : toFlip) b[p[1]][p[0]] = player;
            }
        }
    }

    public static Integer winner(int[][] b) {
        int black = 0, white = 0;
        for (int y = 0; y < 8; y++) {
            for (int x = 0; x < 8; x++) {
                if (b[y][x] == 1) black++; else if (b[y][x] == 2) white++;
            }
        }
        if (black == white) return null; // 引き分け
        return (black > white) ? 1 : 2;
    }

    // 簡易 CPU：角>重み>反転枚数 のスコアで選ぶ
    public static Move bestCpuMove(int[][] b, int cpu) {
        List<Move> moves = legalMoves(b, cpu);
        if (moves.isEmpty()) throw new IllegalStateException("no cpu moves");
        // 角優先
        for (Move m : moves) {
            if ((m.x() == 0 || m.x() == 7) && (m.y() == 0 || m.y() == 7)) return m;
        }
        // 重み付き（一般的な評価表）
        int[][] weight = {
                {120,-20,20,5,5,20,-20,120},
                {-20,-40,-5,-5,-5,-5,-40,-20},
                {20,-5,15,3,3,15,-5,20},
                {5,-5,3,3,3,3,-5,5},
                {5,-5,3,3,3,3,-5,5},
                {20,-5,15,3,3,15,-5,20},
                {-20,-40,-5,-5,-5,-5,-40,-20},
                {120,-20,20,5,5,20,-20,120}
        };
        Move best = moves.get(0);
        int bestScore = Integer.MIN_VALUE;
        for (Move m : moves) {
            int flips = flipsIf(b, cpu, m.x(), m.y());
            int pos = weight[m.y()][m.x()];
            int score = flips * 10 + pos;
            if (score > bestScore) { bestScore = score; best = m; }
        }
        return best;
    }
}