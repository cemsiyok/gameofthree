package com.takeaway.gameofthreeservice.service;

import com.takeaway.gameofthreeservice.domain.GameRoom;
import com.takeaway.gameofthreeservice.dto.InitiateGameRequestDTO;
import com.takeaway.gameofthreeservice.dto.JoinGameRequestDTO;
import com.takeaway.gameofthreeservice.dto.MoveRequestDTO;

import java.util.List;

public interface GameService {

    List<GameRoom> getPendingGameRooms();

    GameRoom initiateGame(InitiateGameRequestDTO initiateGameRequestDTO);

    GameRoom joinGame(JoinGameRequestDTO joinGameRequestDTO);

    GameRoom makeMove(MoveRequestDTO moveRequestDTO);

    GameRoom findGameRoomById(Integer gameRoomId);

}
