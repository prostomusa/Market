package com.yandex.market.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.yandex.market.enums.ShopUnitType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.UUID;

@Validated
@Getter
@Setter
@NoArgsConstructor
public class ShopUnitStaticUnit {

    @NotNull
    @Schema(type = "string",
            format = "uuid",
            description = "Уникальный идентфикатор",
            example = "3fa85f64-5717-4562-b3fc-2c963f66a333")
    private UUID id;

    @NotNull
    @Schema(type = "string",
            description = "Имя категории")
    private String name;

    @Schema(type = "string",
            nullable = true,
            format = "uuid",
            description = "UUID родительской категории",
            example = "3fa85f64-5717-4562-b3fc-2c963f66a333")
    private UUID parentId;

    @NotNull
    @Schema(type = "string",
            description = "Тип элемента - категория или товар")
    private ShopUnitType type;

    @Min(0)
    @Schema(type = "integer",
            nullable = true,
            description = "Целое число, для категории - это средняя цена всех дочерних " +
                    "товаров(включая товары подкатегорий). Если цена является не целым числом, " +
                    "округляется в меньшую сторону до целого числа. Если категория не содержит " +
                    "товаров цена равна null.")
    private Integer price;

    @NotNull
    @Schema(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'",
            type = "string",
            description = "Время обновления добавляемых товаров/категорий.")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
    private LocalDateTime updateDate;

}
