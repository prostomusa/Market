package com.yandex.market.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotNull;
import java.util.List;

@Validated
@Getter
@Setter
public class ShopUnitStatisticResponse {

    @NotNull
    @Schema(type = "array",
            description = "История в произвольном порядке.")
    private List<ShopUnitStaticUnit> items;

}
