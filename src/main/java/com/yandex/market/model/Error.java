package com.yandex.market.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;

@Getter
@Setter
@AllArgsConstructor
public class Error {

    @NotNull
    @Schema(type = "integer")
    private Integer code;

    @NotNull
    @Schema(type = "string")
    private String message;

}
