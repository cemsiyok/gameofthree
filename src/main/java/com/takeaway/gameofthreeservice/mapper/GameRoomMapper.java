package com.takeaway.gameofthreeservice.mapper;

import com.takeaway.gameofthreeservice.domain.GameRoom;
import com.takeaway.gameofthreeservice.domain.GameState;
import com.takeaway.gameofthreeservice.domain.Move;
import com.takeaway.gameofthreeservice.dto.GameRoomDTO;
import com.takeaway.gameofthreeservice.dto.InitiateGameRequestDTO;
import com.takeaway.gameofthreeservice.dto.PlayerDTO;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class GameRoomMapper {

    private final static int MIN_NUMBER = 3;
    private final static int MAX_NUMBER = Integer.MAX_VALUE;

    private final PlayerMapper playerMapper;

    public GameRoomMapper(PlayerMapper playerMapper) {
        this.playerMapper = playerMapper;
    }

    public GameRoom makeGameRoom(InitiateGameRequestDTO initiateGameRequestDTO) {
        return GameRoom.builder()
                .createdDate(OffsetDateTime.now())
                .moves(new LinkedList<>())
                .state(GameState.PENDING_SECOND_PLAYER_TO_JOIN)
                .currentNumber(Optional.ofNullable(initiateGameRequestDTO.getInitialNumber())
                        .orElse((int) (Math.random() * ((MAX_NUMBER - MIN_NUMBER) + 1)) + MIN_NUMBER))
                .build();
    }

    public GameRoomDTO makeGameRoomDTO(GameRoom gameRoom) {
        Move lastMove = !gameRoom.getMoves().isEmpty() ? gameRoom.getMoves().getLast() : null;
        Integer waitingActionFromPlayerId = lastMove != null
                ? (gameRoom.getFirstPlayerId().equals(lastMove.getPlayerId()) ? gameRoom.getSecondPlayerId() : gameRoom.getFirstPlayerId())
                : null;
        PlayerDTO firstPlayer = playerMapper.makePlayerDTO(gameRoom.getFirstPlayerId());
        PlayerDTO secondPlayer = playerMapper.makePlayerDTO(gameRoom.getSecondPlayerId());
        return GameRoomDTO.builder()
                .id(gameRoom.getId())
                .firstPlayer(firstPlayer)
                .secondPlayer(secondPlayer)
                .waitingActionFrom(gameRoom.getState() == GameState.BEING_PLAYED ? waitingActionFromPlayerId.equals(firstPlayer.getId()) ? firstPlayer : secondPlayer : null)
                .winner(gameRoom.getState() == GameState.FINISHED ? firstPlayer.getId().equals(lastMove.getPlayerId()) ? firstPlayer : secondPlayer : null)
                .state(gameRoom.getState())
                .lastAddedNumber(lastMove != null ? lastMove.getAddedValue() : null)
                .number(gameRoom.getCurrentNumber()).build();
    }

    public List<GameRoomDTO> makeGameRoomDTO(List<GameRoom> gameRooms) {
        return gameRooms.stream().map(this::makeGameRoomDTO).collect(Collectors.toList());
    }

}
