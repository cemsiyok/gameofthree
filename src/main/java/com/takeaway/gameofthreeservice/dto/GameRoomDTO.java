package com.takeaway.gameofthreeservice.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.takeaway.gameofthreeservice.domain.GameState;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class GameRoomDTO {

    private Integer id;
    private PlayerDTO firstPlayer;
    private PlayerDTO secondPlayer;
    private PlayerDTO waitingActionFrom;
    private PlayerDTO winner;
    private Integer lastAddedNumber;
    private Integer number;
    private GameState state;
}