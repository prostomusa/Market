package com.yandex.market.model;

import com.yandex.market.enums.ShopUnitType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;

@Validated
@Getter
@Setter
@AllArgsConstructor
public class ShopUnit {

    @NotNull
    @Schema(type = "string",
            description = "Уникальный идентфикатор",
            example = "3fa85f64-5717-4562-b3fc-2c963f66a333")
    private String id;

    @NotNull
    @Schema(type = "string",
            description = "Имя категории")
    private String name;

    @NotNull
    @Schema(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'",
            type = "string",
            description = "Время последнего обновления элемента",
            example = "2022-05-28T21:12:01.000Z")
    private LocalDateTime date;

    @Schema(type = "string",
            nullable = true,
            description = "UUID родительской категории",
            example = "3fa85f64-5717-4562-b3fc-2c963f66a333")
    private String parentId;

    @NotNull
    @Schema(type = "string",
            example = "OFFER",
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

    @Schema(type = "array",
            nullable = true,
            description = "Список всех дочерних товаров/категорий. Для товаров поле равно null.")
    private List<ShopUnit> children;

}
