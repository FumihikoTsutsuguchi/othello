package com.example.othello.dto;

public record GameDto(
        String gameId,
        int[][] board,
        int currentPlayer,  // 1=黒(人間), 2=白(CPU)
        boolean finished,
        Integer winner      // null=引き分け, 1=黒, 2=白
) {}