package com.takeaway.gameofthreeservice.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.OffsetDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@Data
public class Move {

    private OffsetDateTime createdDate;
    private Integer playerId;
    private Integer addedValue;
    private Integer numberBeforeMove;
}