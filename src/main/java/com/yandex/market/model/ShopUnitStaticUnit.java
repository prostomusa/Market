package com.yandex.market.model;

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

@Validated
@Getter
@Setter
@NoArgsConstructor
public class ShopUnitStaticUnit {

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
            description = "Целое число, для категории - это средняя цена всех дочерних " +
                    "товаров(включая товары подкатегорий). Если цена является не целым числом, " +
                    "округляется в меньшую сторону до целого числа. Если категория не содержит " +
                    "товаров цена равна null.")
    private Integer price;

    @NotNull
    @Schema(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'",
            type = "string",
            description = "Время обновления добавляемых товаров/категорий.")
    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
    private LocalDateTime updateDate;

}
