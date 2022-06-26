package com.yandex.market.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.yandex.market.enums.ShopUnitType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Validated
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ShopUnit {

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

    @NotNull
    @Schema(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'",
            type = "string",
            description = "Время последнего обновления элемента",
            example = "2022-05-28T21:12:01.000Z")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
    private LocalDateTime date;

    @Schema(type = "string",
            nullable = true,
            format = "uuid",
            description = "UUID родительской категории",
            example = "3fa85f64-5717-4562-b3fc-2c963f66a333")
    private UUID parentId;

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
    //@JsonInclude(JsonInclude.Include.NON_NULL)
    private List<ShopUnit> children;

}
