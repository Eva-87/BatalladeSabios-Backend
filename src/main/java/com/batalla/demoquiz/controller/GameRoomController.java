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

    // ---------------------------------------------------------
    // CREATE ROOM
    // ---------------------------------------------------------
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

    // ---------------------------------------------------------
    // JOIN ROOM
    // ---------------------------------------------------------
    @PostMapping("/{code}/join")
    public RoomPlayer joinRoom(@PathVariable String code,
                               @RequestBody JoinRoomRequest request) {
        return gameRoomService.joinRoom(code, request.getUserId());
    }

    // ---------------------------------------------------------
    // GET PLAYERS
    // ---------------------------------------------------------
    @GetMapping("/{code}/players")
    public List<RoomPlayer> getPlayers(@PathVariable String code) {
        return gameRoomService.getPlayers(code);
    }

    // ---------------------------------------------------------
    // START GAME
    // ---------------------------------------------------------
    @PostMapping("/{code}/start")
    public QuestionMessage start(@PathVariable String code) {
        return gameEngineService.startGame(code);
    }

    // ---------------------------------------------------------
    // SUBMIT ANSWER
    // ---------------------------------------------------------
    @PostMapping("/{code}/answer")
    public RoundResultMessage submitAnswer(@PathVariable String code,
                                           @RequestBody AnswerMessage answer) {
        return gameEngineService.submitAnswer(code, answer);
    }

    // ---------------------------------------------------------
    // NEXT QUESTION
    // ---------------------------------------------------------
    @PostMapping("/{code}/next")
    public QuestionMessage nextQuestion(@PathVariable String code) {
        return gameEngineService.nextQuestion(code);
    }

    // ---------------------------------------------------------
    // FINISH GAME
    // ---------------------------------------------------------
    @PostMapping("/{code}/finish")
    public GameOverMessage finishGame(@PathVariable String code) {
        return gameEngineService.finishGame(code);
    }

    // ---------------------------------------------------------
    // GET STATUS
    // ---------------------------------------------------------
    @GetMapping("/{code}/status")
    public GameStatus getStatus(@PathVariable String code) {
        return roomService.getStatus(code);
    }

    // ---------------------------------------------------------
    // GET ROOM INFO
    // ---------------------------------------------------------
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

    // ---------------------------------------------------------
    // ⭐ NUEVO: GET CURRENT QUESTION (para TODOS los jugadores)
    // ---------------------------------------------------------
    @GetMapping("/{code}/question")
    public QuestionMessage getCurrentQuestion(@PathVariable String code) {
        return gameEngineService.getCurrentQuestion(code);
    }
}
