package com.yandex.market.model;

import com.yandex.market.enums.ShopUnitType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Validated
@Getter
@Setter
@AllArgsConstructor
public class ShopUnitImport {

    @NotNull
    @Schema(type = "string",
            description = "Уникальный идентфикатор",
            example = "3fa85f64-5717-4562-b3fc-2c963f66a333")
    private String id;

    @NotNull
    @Schema(type = "string",
            description = "Имя категории")
    private String name;

    @Schema(type = "string",
            nullable = true,
            description = "UUID родительской категории",
            example = "3fa85f64-5717-4562-b3fc-2c963f66a333")
    private String parentId;

    @NotNull
    @Schema(type = "string",
            description = "Тип элемента - категория или товар")
    private ShopUnitType type;

    @Min(0)
    @Schema(type = "integer",
            nullable = true,
            description = "Целое число, для категорий поле должно содержать null.")
    private Integer price;

}
