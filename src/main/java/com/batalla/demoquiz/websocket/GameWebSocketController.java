package com.batalla.demoquiz.websocket;

import com.batalla.demoquiz.dto.*;
import com.batalla.demoquiz.service.GameEngineService;
import com.batalla.demoquiz.service.GameRoomService;
import org.springframework.messaging.handler.annotation.*;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
public class GameWebSocketController {

    private final SimpMessagingTemplate messagingTemplate;
    private final GameRoomService gameRoomService;
    private final GameEngineService gameEngineService;

    public GameWebSocketController(SimpMessagingTemplate messagingTemplate,
                                   GameRoomService gameRoomService,
                                   GameEngineService gameEngineService) {
        this.messagingTemplate = messagingTemplate;
        this.gameRoomService = gameRoomService;
        this.gameEngineService = gameEngineService;
    }

    // ---------------------------------------------------------
    // PLAYER JOINS ROOM
    // ---------------------------------------------------------
    @MessageMapping("/rooms/{code}/join")
    public void joinRoomWS(@DestinationVariable String code, @Payload Long userId) {

        try {
            gameRoomService.joinRoom(code, userId);

            messagingTemplate.convertAndSend(
                    "/topic/rooms/" + code,
                    new WebSocketEvent("PLAYER_JOINED", userId)
            );

        } catch (Exception e) {
            messagingTemplate.convertAndSend(
                    "/topic/rooms/" + code,
                    new WebSocketEvent("ERROR", "No se pudo unir el jugador: " + e.getMessage())
            );
        }
    }

    // ---------------------------------------------------------
    // START GAME
    // ---------------------------------------------------------
    @MessageMapping("/rooms/{code}/start")
    public void startGame(@DestinationVariable String code) {

        try {
            QuestionMessage q = gameEngineService.startGame(code);

            if (q == null) {
                messagingTemplate.convertAndSend(
                        "/topic/rooms/" + code,
                        new WebSocketEvent("ERROR", "No hay preguntas disponibles")
                );
                return;
            }

            messagingTemplate.convertAndSend("/topic/rooms/" + code, q);

        } catch (Exception e) {
            messagingTemplate.convertAndSend(
                    "/topic/rooms/" + code,
                    new WebSocketEvent("ERROR", "No se pudo iniciar el juego: " + e.getMessage())
            );
        }
    }

    // ---------------------------------------------------------
    // RECEIVE ANSWER
    // ---------------------------------------------------------
    @MessageMapping("/rooms/{code}/answer")
    public void receiveAnswer(@DestinationVariable String code,
                              @Payload AnswerMessage msg) {

        try {
            // 1. Procesar respuesta
            RoundResultMessage result = gameEngineService.submitAnswer(code, msg);

            if (result != null) {
                messagingTemplate.convertAndSend("/topic/rooms/" + code, result);
            }

            // 2. Obtener siguiente pregunta
            QuestionMessage next = gameEngineService.nextQuestion(code);

            if (next == null) {
                // 3. Si no hay más preguntas → FIN DEL JUEGO
                GameOverMessage over = gameEngineService.finishGame(code);
                messagingTemplate.<GameOverMessage>convertAndSend("/topic/rooms/" + code, over);

            } else {
                // 4. Enviar siguiente pregunta
                messagingTemplate.convertAndSend("/topic/rooms/" + code, next);
            }

        } catch (Exception e) {
            messagingTemplate.convertAndSend(
                    "/topic/rooms/" + code,
                    new WebSocketEvent("ERROR", "Error al procesar respuesta: " + e.getMessage())
            );
        }
    }
}
