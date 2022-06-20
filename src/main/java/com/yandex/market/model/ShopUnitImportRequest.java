package com.yandex.market.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;

@Validated
@Getter
@Setter
@NoArgsConstructor
public class ShopUnitImportRequest {

    @NotNull
    @Schema(type = "array",
            description = "Импортируемые элементы")
    private List<ShopUnitImport> items;

    @NotNull
    @Schema(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'",
            type = "string",
            description = "Время обновления добавляемых товаров/категорий.")
    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
    private LocalDateTime updateDate;

}
