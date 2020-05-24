package com.takeaway.gameofthreeservice.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class MoveRequestDTO {

    @JsonIgnore
    private Integer gameRoomId;

    @JsonIgnore
    private Integer playerId;

    @Min(value = -1)
    @Max(value = 1)
    private Integer addedValue;

    private boolean playAutomatically;
}