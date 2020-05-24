package com.takeaway.gameofthreeservice.repository;

import com.takeaway.gameofthreeservice.domain.Player;
import com.takeaway.gameofthreeservice.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class PlayerRepository implements BaseRepository<Integer, Player> {

    private Map<String, Player> players = new ConcurrentHashMap<>();
    private Map<String, Integer> playerNameByIdIndex = new ConcurrentHashMap<>();
    private final AtomicInteger atomicInteger = new AtomicInteger();

    @Override
    public Player findById(Integer id) {
        return Optional.ofNullable(players.get(String.valueOf(id))).orElseThrow(() -> new ResourceNotFoundException("Player", id));
    }

    public Optional<Player> findByName(String name) {
        Integer playerId = playerNameByIdIndex.get(name);
        return playerId != null ? Optional.of(findById(playerId)) : Optional.empty();
    }

    @Override
    public Player save(Player player) {
        if (!player.isPersisted()) {
            player.setId(atomicInteger.incrementAndGet());
        }
        players.put(String.valueOf(player.getId()), player);
        playerNameByIdIndex.put(player.getName(), player.getId());
        return player;
    }
}