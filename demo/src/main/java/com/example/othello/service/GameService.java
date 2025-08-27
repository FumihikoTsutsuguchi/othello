package com.example.othello.service;

import com.example.othello.dto.GameDto;
import com.example.othello.model.Game;
import com.example.othello.rules.OthelloRules;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class GameService {
    private final Map<String, Game> games = new ConcurrentHashMap<>();

    public GameDto create() {
        String id = UUID.randomUUID().toString();
        Game g = new Game(id);
        games.put(id, g);
        return toDto(g);
    }

    public GameDto get(String id) { return toDto(require(id)); }

    public List<OthelloRules.Move> legalMoves(String id, Integer playerOpt) {
        Game g = require(id);
        int player = (playerOpt != null) ? playerOpt : g.getCurrentPlayer();
        return OthelloRules.legalMoves(g.getBoard(), player);
    }

    public GameDto move(String id, int x, int y) {
        Game g = require(id);
        if (g.isFinished()) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "game finished");
        if (g.getCurrentPlayer() != g.getHuman()) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "not human turn");
        if (!OthelloRules.isLegal(g.getBoard(), g.getHuman(), x, y)) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "illegal move");

        // 人間の着手
        OthelloRules.applyMove(g.getBoard(), g.getHuman(), x, y);
        g.setCurrentPlayer(g.getCpu());

        // CPU処理やパス判定を行う
        resolveTurnsAndMaybeCpu(g);
        return toDto(g);
    }

    private void resolveTurnsAndMaybeCpu(Game g) {
        while (true) {
            boolean humanHas = OthelloRules.hasAnyMove(g.getBoard(), g.getHuman());
            boolean cpuHas = OthelloRules.hasAnyMove(g.getBoard(), g.getCpu());

            if (!humanHas && !cpuHas) {
                g.setFinished(true);
                g.setWinner(OthelloRules.winner(g.getBoard()));
                return;
            }

            if (g.getCurrentPlayer() == g.getCpu()) {
                if (cpuHas) {
                    var m = OthelloRules.bestCpuMove(g.getBoard(), g.getCpu());
                    OthelloRules.applyMove(g.getBoard(), g.getCpu(), m.x(), m.y());
                    g.setCurrentPlayer(g.getHuman());
                    if (!OthelloRules.hasAnyMove(g.getBoard(), g.getHuman())) {
                        g.setCurrentPlayer(g.getCpu());
                        continue;
                    }
                    return;
                } else {
                    g.setCurrentPlayer(g.getHuman());
                    if (!humanHas) continue;
                    return;
                }
            } else { // 人間の番
                if (!humanHas) {
                    g.setCurrentPlayer(g.getCpu());
                    continue;
                }
                return;
            }
        }
    }

    private Game require(String id) {
        Game g = games.get(id);
        if (g == null) throw new ResponseStatusException(HttpStatus.NOT_FOUND, "game not found");
        return g;
    }

    private GameDto toDto(Game g) {
        return new GameDto(g.getGameId(), g.getBoard(), g.getCurrentPlayer(), g.isFinished(), g.getWinner());
    }
}