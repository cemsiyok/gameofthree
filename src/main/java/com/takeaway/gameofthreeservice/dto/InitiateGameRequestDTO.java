package com.takeaway.gameofthreeservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class InitiateGameRequestDTO {

    @NotNull
    @NotBlank
    private String playerName;

    @Min(value = 3)
    private Integer initialNumber;
}