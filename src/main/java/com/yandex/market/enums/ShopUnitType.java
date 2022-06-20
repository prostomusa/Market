package com.yandex.market.enums;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum ShopUnitType {
    OFFER("OFFER"),
    CATEGORY("CATEGORY");

    private final String value;

    @JsonValue
    public String getValue() {
        return this.value;
    }

}
