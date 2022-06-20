package com.yandex.market.controller;

import com.yandex.market.exception.NotFoundException;
import com.yandex.market.model.Error;
import com.yandex.market.model.ShopUnit;
import com.yandex.market.model.ShopUnitImportRequest;
import com.yandex.market.model.ShopUnitStatisticResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("")
@Tag(name = "Вступительное задание в Летнюю Школу Бэкенд Разработки Яндекса 2022")
public class MarketController {

    private static final String IMPORT_DESCRIPTION = "Импортирует новые товары и/или категории. Товары/категории импортированные повторно обновляют текущие. Изменение типа элемента с товара на категорию или с категории на товар не допускается. Порядок элементов в запросе является произвольным.\n" +
            "\n" +
            "- uuid товара или категории является уникальным среди товаров и категорий\n" +
            "- родителем товара или категории может быть только категория\n" +
            "- принадлежность к категории определяется полем parentId\n" +
            "- товар или категория могут не иметь родителя (при обновлении parentId на null, элемент остается без родителя)\n" +
            "- название элемента не может быть null\n" +
            "- у категорий поле price должно содержать null\n" +
            "- цена товара не может быть null и должна быть больше либо равна нулю.\n" +
            "- при обновлении товара/категории обновленными считаются все их параметры\n" +
            "- при обновлении параметров элемента обязательно обновляется поле date в соответствии с временем обновления\n" +
            "- в одном запросе не может быть двух элементов с одинаковым id\n" +
            "- дата должна обрабатываться согласно ISO 8601 (такой придерживается OpenAPI). Если дата не удовлетворяет данному формату, необходимо отвечать 400.\n" +
            "\n Гарантируется, что во входных данных нет циклических зависимостей и поле updateDate монотонно возрастает. Гарантируется, что при проверке передаваемое время кратно секундам.";

    @Operation(
            description = IMPORT_DESCRIPTION
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Вставка или обновление прошли успешно."),
            @ApiResponse(responseCode = "400", description = "Невалидная схема документа или входные данные не верны.",
                    content = @Content(schema = @Schema(implementation = Error.class), examples = @ExampleObject(
                            value = "{\"code\": 400, \"message\": \"Validation Failed\"}")
                    )
            )
    })
    @PostMapping("/imports")
    public ResponseEntity<Object> imports(@RequestBody ShopUnitImportRequest shopUnitImportRequest) {
        return ResponseEntity.ok().build();
    }

    @Operation(
            description = "Удалить элемент по идентификатору. При удалении категории удаляются все дочерние элементы. Доступ к статистике (истории обновлений) удаленного элемента невозможен.\n" +
                    "\n" +
                    "Так как время удаления не передается, при удалении элемента время обновления родителя изменять не нужно.\n" +
                    "\n" +
                    "Обратите, пожалуйста, внимание на этот обработчик. При его некорректной работе тестирование может быть невозможно."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Удаление прошло успешно."),
            @ApiResponse(responseCode = "400", description = "Невалидная схема документа или входные данные не верны.",
                    content = @Content(schema = @Schema(implementation = Error.class), examples = @ExampleObject(
                            value = "{\"code\": 400, \"message\": \"Validation Failed\"}")
                    )
            ),
            @ApiResponse(responseCode = "404", description = "Категория/товар не найден.",
                    content = @Content(schema = @Schema(implementation = Error.class), examples = @ExampleObject(
                            value = "{\"code\": 404, \"message\": \"Item not found\"}")
                    )
            )
    })
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Object> delete(@PathVariable UUID id) {
        if ("ui".equals(id)) {
            throw new NotFoundException();
        }
        return ResponseEntity.ok().build();
    }

    @Operation(
            description = "Получить информацию об элементе по идентификатору. При получении информации о категории также предоставляется " +
                    "информация о её дочерних элементах.\n" +
                    "\n" +
                    "- для пустой категории поле children равно пустому массиву, а для товара равно null\n" +
                    "- цена категории - это средняя цена всех её товаров, включая товары дочерних категорий. " +
                    "Если категория не содержит товаров цена равна null. При обновлении цены товара, средняя цена категории, " +
                    "которая содержит этот товар, тоже обновляется."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Информация об элементе.", content = @Content(schema = @Schema(implementation = ShopUnit.class))),
            @ApiResponse(responseCode = "400", description = "Невалидная схема документа или входные данные не верны.",
                    content = @Content(schema = @Schema(implementation = Error.class), examples = @ExampleObject(
                            value = "{\"code\": 400, \"message\": \"Validation Failed\"}")
                    )
            ),
            @ApiResponse(responseCode = "404", description = "Категория/товар не найден.",
                    content = @Content(schema = @Schema(implementation = Error.class), examples = @ExampleObject(
                            value = "{\"code\": 404, \"message\": \"Item not found\"}")
                    )
            )
    })
    @GetMapping("/nodes/{id}")
    public ResponseEntity<Object> getNodes(@PathVariable UUID id) {
        return ResponseEntity.ok().build();
    }

    @Operation(
            description = "Получение списка товаров, цена которых была обновлена за последние 24 часа включительно [now() - 24h, now()] " +
                    "от времени переданном в запросе. Обновление цены не означает её изменение. Обновления цен удаленных товаров недоступны. " +
                    "При обновлении цены товара, средняя цена категории, которая содержит этот товар, тоже обновляется."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Список товаров, цена которых была обновлена.", content = @Content(schema = @Schema(implementation = ShopUnitStatisticResponse.class))),
            @ApiResponse(responseCode = "400", description = "Невалидная схема документа или входные данные не верны.",
                    content = @Content(schema = @Schema(implementation = Error.class), examples = @ExampleObject(
                            value = "{\"code\": 400, \"message\": \"Validation Failed\"}")
                    )
            )
    })
    @GetMapping("/sales")
    public ResponseEntity<Object> sales(@RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'") LocalDateTime date) {
        return ResponseEntity.ok().build();
    }

    @Operation(
            description = "Получение статистики (истории обновлений) по товару/категории за заданный полуинтервал [from, to). " +
                    "Статистика по удаленным элементам недоступна.\n" +
                    "\n" +
                    "- цена категории - это средняя цена всех её товаров, включая товары дочерних категорий." +
                    "Если категория не содержит товаров цена равна null. " +
                    "При обновлении цены товара, средняя цена категории, которая содержит этот товар, тоже обновляется.\n" +
                    "- можно получить статистику за всё время."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Статистика по элементу.", content = @Content(schema = @Schema(implementation = ShopUnitStatisticResponse.class))),
            @ApiResponse(responseCode = "400", description = "Невалидная схема документа или входные данные не верны.",
                    content = @Content(schema = @Schema(implementation = Error.class), examples = @ExampleObject(
                            value = "{\"code\": 400, \"message\": \"Validation Failed\"}")
                    )
            ),
            @ApiResponse(responseCode = "404", description = "Категория/товар не найден.",
                    content = @Content(schema = @Schema(implementation = Error.class), examples = @ExampleObject(
                            value = "{\"code\": 404, \"message\": \"Item not found\"}")
                    )
            )
    })
    @GetMapping("/node/{id}/statistic")
    public ResponseEntity<Object> getStatistic(@PathVariable UUID id,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'") LocalDateTime dateStart,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'") LocalDateTime dateEnd) {
        return ResponseEntity.ok().build();
    }
}
