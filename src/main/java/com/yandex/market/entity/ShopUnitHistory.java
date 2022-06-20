package com.yandex.market.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "SHOP_UNIT_HISTORY")
@Getter
@Setter
@NoArgsConstructor
public class ShopUnitHistory extends AbstractEntity {

    @ManyToOne
    @JoinColumn(name = "SHOP_UNIT_ENTITY_ID")
    private ShopUnitEntity shopUnitEntity;

    @Column
    private LocalDateTime updatedDate;

}
