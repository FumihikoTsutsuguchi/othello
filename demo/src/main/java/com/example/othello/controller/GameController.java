package com.example.othello.controller;

import com.example.othello.dto.GameDto;
import com.example.othello.rules.OthelloRules;
import com.example.othello.service.GameService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class GameController {
    private final GameService gameService;
    public GameController(GameService gameService) { this.gameService = gameService; }

    @PostMapping("/games")
    public GameDto create() { return gameService.create(); }

    @GetMapping("/games/{id}")
    public GameDto get(@PathVariable String id) { return gameService.get(id); }

    @GetMapping("/games/{id}/legal-moves")
    public List<OthelloRules.Move> legal(
            @PathVariable String id,
            @RequestParam(required = false) Integer player
    ) { return gameService.legalMoves(id, player); }

    public record MoveReq(@Min(0) @Max(7) int x, @Min(0) @Max(7) int y) {}

    @PostMapping("/games/{id}/moves")
    public GameDto move(@PathVariable String id, @RequestBody @Valid MoveReq req) {
        return gameService.move(id, req.x(), req.y());
    }
}