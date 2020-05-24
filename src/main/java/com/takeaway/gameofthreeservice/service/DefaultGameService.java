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
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.List;

@Service
public class DefaultGameService implements GameService {

    private static final int[] NUMBERS = {-1, 0, 1};
    private final static int DIVIDER = 3;

    private final GameRoomRepository gameRoomRepository;
    private final PlayerRepository playerRepository;
    private final MoveValidationService moveValidationService;
    private final GameRoomMapper gameRoomMapper;

    public DefaultGameService(GameRoomRepository gameRoomRepository,
                              PlayerRepository playerRepository,
                              MoveValidationService moveValidationService,
                              GameRoomMapper gameRoomMapper) {
        this.gameRoomRepository = gameRoomRepository;
        this.playerRepository = playerRepository;
        this.moveValidationService = moveValidationService;
        this.gameRoomMapper = gameRoomMapper;
    }

    @Override
    public List<GameRoom> getPendingGameRooms() {
        return gameRoomRepository.getAllByState(GameState.PENDING_SECOND_PLAYER_TO_JOIN);
    }

    @Override
    public GameRoom initiateGame(InitiateGameRequestDTO initiateGameRequestDTO) {
        GameRoom gameRoom = gameRoomMapper.makeGameRoom(initiateGameRequestDTO);
        Player firstPlayer = playerRepository.findByName(initiateGameRequestDTO.getPlayerName()).
                orElse(playerRepository.save(new Player(initiateGameRequestDTO.getPlayerName())));
        gameRoom.setFirstPlayerId(firstPlayer.getId());
        return gameRoomRepository.save(gameRoom);
    }

    @Override
    public GameRoom joinGame(JoinGameRequestDTO joinGameRequestDTO) {
        GameRoom gameRoom = gameRoomRepository
                .findById(joinGameRequestDTO.getGameRoomId());
        Player secondPlayer = playerRepository.findByName(joinGameRequestDTO.getPlayerName()).
                orElse(playerRepository.save(new Player(joinGameRequestDTO.getPlayerName())));

        if (gameRoom.getState() == GameState.PENDING_SECOND_PLAYER_TO_JOIN) {
            if (gameRoom.getFirstPlayerId().equals(secondPlayer.getId())) {
                throw new PlayerCannotJoinGameCreatedByItself();
            }
            Move move = Move.builder()
                    .createdDate(OffsetDateTime.now())
                    .playerId(gameRoom.getFirstPlayerId())
                    .numberBeforeMove(gameRoom.getCurrentNumber())
                    .build();

            gameRoom.getMoves().addLast(move);
            gameRoom.setSecondPlayerId(secondPlayer.getId());
            gameRoom.setState(GameState.BEING_PLAYED);
            gameRoomRepository.save(gameRoom);
        }
        return gameRoom;
    }

    @Override
    public GameRoom makeMove(MoveRequestDTO moveRequestDTO) {
        moveValidationService.validate(moveRequestDTO);

        GameRoom gameRoom = gameRoomRepository.findById(moveRequestDTO.getGameRoomId());
        Player player = playerRepository.findById(moveRequestDTO.getPlayerId());

        Integer currentNumber = gameRoom.getCurrentNumber();
        Integer addedNumber;
        if (moveRequestDTO.isPlayAutomatically()) {
            int i = 0;
            while ((currentNumber + NUMBERS[i]) % DIVIDER != 0) {
                i++;
            }
            addedNumber = NUMBERS[i];
        } else {
            addedNumber = moveRequestDTO.getAddedValue();
        }
        Move move = Move.builder()
                .createdDate(OffsetDateTime.now())
                .playerId(player.getId())
                .addedValue(addedNumber)
                .numberBeforeMove(currentNumber)
                .build();
        gameRoom.getMoves().addLast(move);

        currentNumber = (currentNumber + addedNumber) / DIVIDER;
        gameRoom.setCurrentNumber(currentNumber);
        if (currentNumber == 1) {
            gameRoom.setState(GameState.FINISHED);
        }
        return gameRoomRepository.save(gameRoom);
    }

    @Override
    public GameRoom findGameRoomById(Integer gameRoomId) {
        return gameRoomRepository.findById(gameRoomId);
    }
}
