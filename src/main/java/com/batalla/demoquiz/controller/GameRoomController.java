package com.batalla.demoquiz.controller;

import com.batalla.demoquiz.dto.*;
import com.batalla.demoquiz.entity.GameRoom;
import com.batalla.demoquiz.entity.RoomPlayer;
import com.batalla.demoquiz.enums.GameStatus;
import com.batalla.demoquiz.service.GameEngineService;
import com.batalla.demoquiz.service.GameRoomService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/rooms")
@CrossOrigin
public class GameRoomController {

    private final GameRoomService gameRoomService;
    private final GameEngineService gameEngineService;

    public GameRoomController(
            GameRoomService gameRoomService,
            GameEngineService gameEngineService
    ) {
        this.gameRoomService = gameRoomService;
        this.gameEngineService = gameEngineService;
    }

    @PostMapping("/create")
    public RoomDTO createRoom(@RequestBody CreateRoomRequest request) {

        GameRoom room = gameRoomService.createRoom(
                request.getUserId(),
                request.getQuizId(),
                request.getMaxPlayers()
        );

        return new RoomDTO(
                room.getCode(),
                room.getQuiz().getTitle(),
                room.getCreator().getId(),
                room.getMaxPlayers(),
                room.getStatus().name()
        );
    }

    @PostMapping("/{code}/join")
    public RoomPlayer joinRoom(@PathVariable String code,
                               @RequestBody JoinRoomRequest request) {
        return gameRoomService.joinRoom(code, request.getUserId());
    }

    @GetMapping("/{code}/players")
    public List<RoomPlayer> getPlayers(@PathVariable String code) {
        return gameRoomService.getPlayers(code);
    }

    @PostMapping("/{code}/start")
    public QuestionMessage start(@PathVariable String code) {
        return gameEngineService.startGame(code);
    }

    @PostMapping("/{code}/answer")
    public RoundResultMessage submitAnswer(@PathVariable String code,
                                           @RequestBody AnswerMessage answer) {
        return gameEngineService.submitAnswer(code, answer);
    }

    @PostMapping("/{code}/next")
public QuestionMessage nextQuestion(@PathVariable String code) {
    QuestionMessage q = gameEngineService.nextQuestion(code);

    if (q == null) {
        // Nunca devolver null
        return new QuestionMessage(
                null, "", List.of(), -1, -1, ""
        );
    }

    return q;
}

    @PostMapping("/{code}/finish")
    public GameOverMessage finishGame(@PathVariable String code) {
        return gameEngineService.finishGame(code);
    }

    @GetMapping("/{code}/status")
public ResponseEntity<?> getStatus(@PathVariable String code) {
    GameStatus status = gameRoomService.getStatus(code);

    if (status == null) {
        return ResponseEntity.ok(new StatusDTO("UNKNOWN"));
    }

    return ResponseEntity.ok(new StatusDTO(status.name()));
}

    @GetMapping("/{code}")
public ResponseEntity<?> getRoom(@PathVariable String code) {
    GameRoom room = gameRoomService.getRoom(code);

    if (room == null) {
        // JSON válido
        return ResponseEntity.ok(new RoomDTO(
                "", "", null, 0, "UNKNOWN"
        ));
    }

    return ResponseEntity.ok(new RoomDTO(
            room.getCode(),
            room.getQuiz().getTitle(),
            room.getCreator().getId(),
            room.getMaxPlayers(),
            room.getStatus().name()
    ));
}


@GetMapping("/{code}/question")
public ResponseEntity<?> getCurrentQuestion(@PathVariable String code) {
    QuestionMessage q = gameEngineService.getCurrentQuestion(code);

    if (q == null) {
        // Siempre devolver JSON válido
        return ResponseEntity.ok(new QuestionMessage(
                null, "", List.of(), -1, -1, ""
        ));
    }

    return ResponseEntity.ok(q);
}

@GetMapping("/{code}/round-result")
public ResponseEntity<?> getRoundResult(@PathVariable String code) {

    GameRoom room = gameRoomService.getRoom(code);

    if (room == null) {
        return ResponseEntity.notFound().build();
    }

    if (room.getLastRoundResult() == null) {
        return ResponseEntity.noContent().build();
    }

    return ResponseEntity.ok(room.getLastRoundResult());
}


}