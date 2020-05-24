package com.takeaway.gameofthreeservice.service;

import com.takeaway.gameofthreeservice.domain.GameRoom;
import com.takeaway.gameofthreeservice.domain.GameState;
import com.takeaway.gameofthreeservice.domain.Move;
import com.takeaway.gameofthreeservice.domain.Player;
import com.takeaway.gameofthreeservice.dto.InitiateGameRequestDTO;
import com.takeaway.gameofthreeservice.dto.JoinGameRequestDTO;
import com.takeaway.gameofthreeservice.dto.MoveRequestDTO;
import com.takeaway.gameofthreeservice.exception.PlayerCannotJoinGameCreatedByItself;
import com.takeaway.gameofthreeservice.mapper.GameRoomMapper;
import com.takeaway.gameofthreeservice.repository.GameRoomRepository;
import com.takeaway.gameofthreeservice.repository.PlayerRepository;
import com.takeaway.gameofthreeservice.validation.MoveValidationService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class DefaultGameServiceTest {

    @InjectMocks
    private DefaultGameService gameService;

    @Mock
    private GameRoomRepository gameRoomRepository;

    @Mock
    private PlayerRepository playerRepository;

    @Mock
    private MoveValidationService moveValidationService;

    @Mock
    private GameRoomMapper gameRoomMapper;

    @Test
    public void it_should_initiate_game_room() {
        // Given
        String playerName = "cem";
        int playerId = 1;
        InitiateGameRequestDTO initiateGameRequestDTO = new InitiateGameRequestDTO(playerName, 99);
        GameRoom gameRoom = new GameRoom();
        GameRoom expected = new GameRoom();

        when(gameRoomMapper.makeGameRoom(any())).thenReturn(gameRoom);
        when(playerRepository.findByName(any())).thenReturn(Optional.of(new Player(playerId, playerName)));
        when(gameRoomRepository.save(any())).thenReturn(expected);

        // When
        GameRoom result = gameService.initiateGame(initiateGameRequestDTO);

        // Then
        verify(gameRoomRepository).save(gameRoom);
        assertEquals(playerId, gameRoom.getFirstPlayerId());
        assertEquals(expected, result);
    }

    @Test
    public void it_should_retrieve_pending_games() {
        // Given
        GameRoom expected = new GameRoom();

        when(gameRoomRepository.getAllByState(any())).thenReturn(Collections.singletonList(expected));

        // When
        List<GameRoom> result = gameService.getPendingGameRooms();

        // Then
        assertEquals(1, result.size());
        assertEquals(expected, result.get(0));
    }

    @Test
    public void it_should_join_game_room() {
        // Given
        int playerId = 2;
        String playerName = "john";
        int gameRoomId = 1;
        GameRoom expected = GameRoom.builder()
                .firstPlayerId(99)
                .moves(new LinkedList<>())
                .state(GameState.PENDING_SECOND_PLAYER_TO_JOIN)
                .build();
        JoinGameRequestDTO joinGameRequestDTO = new JoinGameRequestDTO(gameRoomId, playerName);

        when(gameRoomRepository.findById(any())).thenReturn(expected);
        when(playerRepository.findByName(any())).thenReturn(Optional.of(new Player(playerId, playerName)));
        when(gameRoomRepository.save(any())).thenReturn(expected);

        // When
        GameRoom result = gameService.joinGame(joinGameRequestDTO);

        // Then
        verify(gameRoomRepository).save(expected);
        assertEquals(playerId, expected.getSecondPlayerId());
        assertEquals(expected, result);
        assertEquals(GameState.BEING_PLAYED, expected.getState());

        Move lastMove = expected.getMoves().getLast();
        assertEquals(expected.getFirstPlayerId(), lastMove.getPlayerId());
        assertNull(lastMove.getAddedValue());
        assertEquals(expected.getCurrentNumber(), lastMove.getNumberBeforeMove());
    }

    @Test
    public void it_should_throw_exception_when_player_try_to_join_game_created_by_itself() {
        // Given
        int playerId = 2;
        String playerName = "john";
        int gameRoomId = 1;
        GameRoom expected = GameRoom.builder()
                .firstPlayerId(playerId)
                .moves(new LinkedList<>())
                .state(GameState.PENDING_SECOND_PLAYER_TO_JOIN)
                .build();
        JoinGameRequestDTO joinGameRequestDTO = new JoinGameRequestDTO(gameRoomId, playerName);

        when(gameRoomRepository.findById(any())).thenReturn(expected);
        when(playerRepository.findByName(any())).thenReturn(Optional.of(new Player(playerId, playerName)));

        // When
        assertThatExceptionOfType(PlayerCannotJoinGameCreatedByItself.class)
                .isThrownBy(() -> gameService.joinGame(joinGameRequestDTO));

        // Then
        verify(gameRoomRepository, never()).save(expected);
    }

    @Test
    public void it_should_make_move_when_player_plays_manually() {
        // Given
        int playerId = 2;
        String playerName = "john";
        int gameRoomId = 1;
        int currentNumber = 12;
        GameRoom gameRoom = GameRoom.builder()
                .firstPlayerId(99)
                .currentNumber(currentNumber)
                .moves(new LinkedList<>())
                .state(GameState.BEING_PLAYED)
                .build();
        int addedValue = 0;
        MoveRequestDTO moveRequestDTO = MoveRequestDTO.builder().gameRoomId(gameRoomId).playerId(playerId).addedValue(addedValue).playAutomatically(false).build();

        when(gameRoomRepository.findById(any())).thenReturn(gameRoom);
        when(playerRepository.findById(any())).thenReturn(new Player(playerId, playerName));
        GameRoom expected = new GameRoom();
        when(gameRoomRepository.save(any())).thenReturn(expected);

        // When
        GameRoom result = gameService.makeMove(moveRequestDTO);

        // Then
        verify(moveValidationService).validate(moveRequestDTO);

        Move lastMove = gameRoom.getMoves().getLast();
        assertEquals(playerId, lastMove.getPlayerId());
        assertEquals(addedValue, lastMove.getAddedValue());
        assertEquals(currentNumber, lastMove.getNumberBeforeMove());
        assertEquals(GameState.BEING_PLAYED, gameRoom.getState());
        assertEquals(4, gameRoom.getCurrentNumber());

        verify(gameRoomRepository).save(gameRoom);
        assertEquals(expected, result);
    }

    @Test
    public void it_should_make_move_when_player_plays_automatically_and_wins() {
        // Given
        int playerId = 2;
        String playerName = "john";
        int gameRoomId = 1;
        int currentNumber = 4;
        GameRoom gameRoom = GameRoom.builder()
                .firstPlayerId(99)
                .currentNumber(currentNumber)
                .moves(new LinkedList<>())
                .state(GameState.BEING_PLAYED)
                .build();
        MoveRequestDTO moveRequestDTO = MoveRequestDTO.builder().gameRoomId(gameRoomId).playerId(playerId).playAutomatically(true).build();

        when(gameRoomRepository.findById(any())).thenReturn(gameRoom);
        when(playerRepository.findById(any())).thenReturn(new Player(playerId, playerName));
        GameRoom expected = new GameRoom();
        when(gameRoomRepository.save(any())).thenReturn(expected);

        // When
        GameRoom result = gameService.makeMove(moveRequestDTO);

        // Then
        verify(moveValidationService).validate(moveRequestDTO);

        Move lastMove = gameRoom.getMoves().getLast();
        assertEquals(playerId, lastMove.getPlayerId());
        assertEquals(-1, lastMove.getAddedValue());
        assertEquals(currentNumber, lastMove.getNumberBeforeMove());
        assertEquals(GameState.FINISHED, gameRoom.getState());
        assertEquals(1, gameRoom.getCurrentNumber());

        verify(gameRoomRepository).save(gameRoom);
        assertEquals(expected, result);
    }
}