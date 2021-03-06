package com.takeaway.gameofthreeservice.validation;

import com.takeaway.gameofthreeservice.domain.GameRoom;
import com.takeaway.gameofthreeservice.dto.MoveRequestDTO;
import com.takeaway.gameofthreeservice.exception.UnsupportedMoveException;
import com.takeaway.gameofthreeservice.repository.GameRoomRepository;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class MoveValidationService {

    private final static int DIVIDER = 3;
    private final GameRoomRepository gameRoomRepository;

    public MoveValidationService(GameRoomRepository gameRoomRepository) {
        this.gameRoomRepository = gameRoomRepository;
    }

    public void validate(MoveRequestDTO moveRequestDTO) {
        GameRoom gameRoom = gameRoomRepository.findById(moveRequestDTO.getGameRoomId());
        if (!gameRoom.getState().isMoveAcceptable()) {
            throw new UnsupportedMoveException("Game can not accept a move");
        }
        if (!gameRoom.getFirstPlayerId().equals(moveRequestDTO.getPlayerId()) && !gameRoom.getSecondPlayerId().equals(moveRequestDTO.getPlayerId())) {
            throw new UnsupportedMoveException("Player doesn't belong to current game room");
        }
        if (gameRoom.getMoves().getLast().getPlayerId().equals(moveRequestDTO.getPlayerId())) {
            throw new UnsupportedMoveException("Same player can't make successive moves");
        }
        boolean isAddedValueInvalid = !moveRequestDTO.isPlayAutomatically()
                && (Objects.isNull(moveRequestDTO.getAddedValue()) || (gameRoom.getCurrentNumber() + moveRequestDTO.getAddedValue()) % DIVIDER != 0);
        if (isAddedValueInvalid) {
            throw new UnsupportedMoveException("Added value has to make new number divisible by 3");
        }
    }
}