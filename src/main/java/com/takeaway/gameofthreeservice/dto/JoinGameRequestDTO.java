package com.takeaway.gameofthreeservice.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class JoinGameRequestDTO {

    @JsonIgnore
    private Integer gameRoomId;

    @NotBlank
    @NotNull
    private String playerName;
}