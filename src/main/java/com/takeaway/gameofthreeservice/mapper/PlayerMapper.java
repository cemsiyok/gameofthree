package com.takeaway.gameofthreeservice.mapper;

import com.takeaway.gameofthreeservice.domain.Player;
import com.takeaway.gameofthreeservice.dto.PlayerDTO;
import com.takeaway.gameofthreeservice.repository.PlayerRepository;
import org.springframework.stereotype.Component;

@Component
public class PlayerMapper {

    private final PlayerRepository playerRepository;

    public PlayerMapper(PlayerRepository playerRepository) {
        this.playerRepository = playerRepository;
    }

    public PlayerDTO makePlayerDTO(Integer playerId) {
        if (playerId == null) {
            return null;
        }
        Player player = playerRepository.findById(playerId);
        return new PlayerDTO(player.getId(), player.getName());
    }
}
