package com.takeaway.gameofthreeservice.controller;


import com.takeaway.gameofthreeservice.domain.GameRoom;
import com.takeaway.gameofthreeservice.dto.GameRoomDTO;
import com.takeaway.gameofthreeservice.dto.InitiateGameRequestDTO;
import com.takeaway.gameofthreeservice.dto.JoinGameRequestDTO;
import com.takeaway.gameofthreeservice.dto.MoveRequestDTO;
import com.takeaway.gameofthreeservice.exception.PlayerCannotJoinGameCreatedByItself;
import com.takeaway.gameofthreeservice.exception.ResourceNotFoundException;
import com.takeaway.gameofthreeservice.exception.UnsupportedMoveException;
import com.takeaway.gameofthreeservice.mapper.GameRoomMapper;
import com.takeaway.gameofthreeservice.service.GameService;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/v1/game-rooms")
public class GameRoomController {

    private final GameService gameService;
    private final GameRoomMapper gameRoomMapper;

    public GameRoomController(GameService gameService,
                              GameRoomMapper gameRoomMapper) {
        this.gameService = gameService;
        this.gameRoomMapper = gameRoomMapper;
    }

    @GetMapping
    public List<GameRoomDTO> getActiveGameRooms() {
        return gameRoomMapper.makeGameRoomDTO(gameService.getPendingGameRooms());
    }

    @GetMapping("/{id}")
    public GameRoomDTO getGameRoomById(@PathVariable Integer id) {
        try {
            GameRoom gameRoom = gameService.findGameRoomById(id);
            return gameRoomMapper.makeGameRoomDTO(gameRoom);
        } catch (ResourceNotFoundException ex) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, ex.getMessage(), ex);
        }
    }

    @PostMapping
    public GameRoomDTO create(@Validated @RequestBody InitiateGameRequestDTO request) {
        return gameRoomMapper.makeGameRoomDTO(gameService.initiateGame(request));
    }

    @PatchMapping("/{id}/join")
    public GameRoomDTO joinGameRoom(@PathVariable Integer id, @Validated @RequestBody JoinGameRequestDTO request) {
        try {
            request.setGameRoomId(id);
            return gameRoomMapper.makeGameRoomDTO(gameService.joinGame(request));
        } catch (ResourceNotFoundException ex) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, ex.getMessage(), ex);
        } catch (PlayerCannotJoinGameCreatedByItself ex) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, ex.getMessage(), ex);
        }
    }

    @PatchMapping("/{id}/move/{playerid}")
    public GameRoomDTO makeMove(@PathVariable Integer id,
                                @PathVariable(value = "playerid") Integer playerId,
                                @RequestBody MoveRequestDTO request) {
        try {
            request.setGameRoomId(id);
            request.setPlayerId(playerId);
            return gameRoomMapper.makeGameRoomDTO(gameService.makeMove(request));
        } catch (ResourceNotFoundException ex) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, ex.getMessage(), ex);
        } catch (UnsupportedMoveException ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, ex.getMessage(), ex);
        }
    }
}