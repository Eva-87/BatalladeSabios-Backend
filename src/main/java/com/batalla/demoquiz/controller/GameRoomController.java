package com.batalla.demoquiz.controller;

import com.batalla.demoquiz.dto.*;
import com.batalla.demoquiz.entity.GameRoom;
import com.batalla.demoquiz.entity.RoomPlayer;
import com.batalla.demoquiz.enums.GameStatus;
import com.batalla.demoquiz.service.GameEngineService;
import com.batalla.demoquiz.service.GameRoomService;
import com.batalla.demoquiz.service.RoomService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/rooms")
@CrossOrigin
public class GameRoomController {

    private final GameRoomService gameRoomService;
    private final GameEngineService gameEngineService;
    private final RoomService roomService;

    public GameRoomController(
            GameRoomService gameRoomService,
            GameEngineService gameEngineService,
            RoomService roomService
    ) {
        this.gameRoomService = gameRoomService;
        this.gameEngineService = gameEngineService;
        this.roomService = roomService;
    }

    @PostMapping("/create")
    public GameRoom createRoom(@RequestBody CreateRoomRequest request) {
        return gameRoomService.createRoom(
                request.getUserId(),
                request.getQuizId(),
                request.getMaxPlayers()
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

    @GetMapping("/{code}/status")
    public GameStatus getStatus(@PathVariable String code) {
        return roomService.getStatus(code);
    }

    @GetMapping("/{code}")
    public RoomDTO getRoom(@PathVariable String code) {
        GameRoom room = gameRoomService.getRoom(code);

        return new RoomDTO(
                room.getCode(),
                room.getQuiz().getTitle(),
                room.getCreator().getId(),
                room.getMaxPlayers(),
                room.getStatus().name()
        );
    }

    @PostMapping("/{code}/answer")
    public RoundResultMessage submitAnswer(@PathVariable String code,
                                           @RequestBody AnswerMessage answer) {
        return gameEngineService.submitAnswer(code, answer);
    }

    @PostMapping("/{code}/next")
    public QuestionMessage nextQuestion(@PathVariable String code) {
        return gameEngineService.nextQuestion(code);
    }

    @PostMapping("/{code}/finish")
    public GameOverMessage finishGame(@PathVariable String code) {
        return gameEngineService.finishGame(code);
    }
}
