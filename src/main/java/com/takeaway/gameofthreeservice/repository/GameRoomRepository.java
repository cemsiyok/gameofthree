package com.takeaway.gameofthreeservice.repository;

import com.takeaway.gameofthreeservice.domain.GameRoom;
import com.takeaway.gameofthreeservice.domain.GameState;
import com.takeaway.gameofthreeservice.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Service
public class GameRoomRepository implements BaseRepository<Integer, GameRoom> {

    private Map<String, GameRoom> gameRoomCache = new ConcurrentHashMap<>();
    private final AtomicInteger atomicInteger = new AtomicInteger();


    public List<GameRoom> getAllByState(GameState gameState) {
        return gameRoomCache.values().stream()
                .filter(gameRoom -> gameRoom.getState() == gameState)
                .sorted(Comparator.comparing(GameRoom::getCreatedDate))
                .collect(Collectors.toList());
    }

    @Override
    public GameRoom findById(Integer id) {
        return Optional.ofNullable(gameRoomCache.get(String.valueOf(id))).orElseThrow(() -> new ResourceNotFoundException("Player", id));
    }

    @Override
    public GameRoom save(GameRoom gameRoom) {
        if (!gameRoom.isPersisted()) {
            gameRoom.setId(atomicInteger.incrementAndGet());
        }
        gameRoomCache.put(String.valueOf(gameRoom.getId()), gameRoom);
        return gameRoom;
    }
}