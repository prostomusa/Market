package com.yandex.market.entity;

import com.yandex.market.enums.ShopUnitType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

import static javax.persistence.GenerationType.IDENTITY;

@Entity
@Table(name = "SHOP_UNIT_HISTORY")
@Getter
@NoArgsConstructor
public class ShopUnitHistory implements Serializable {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "id", updatable = false, nullable = false)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "SHOP_UNIT_ENTITY_ID")
    @Setter
    private ShopUnitEntity shopUnitEntity;

    @Column
    @Setter
    private UUID parentShopUnitEntityId;

    @Column
    @Setter
    private Integer price;

    @Column
    @Setter
    private String name;

    @Column
    @Enumerated(value = EnumType.STRING)
    @Setter
    private ShopUnitType type;

    @Column
    @Setter
    private LocalDateTime updatedDate;

}
