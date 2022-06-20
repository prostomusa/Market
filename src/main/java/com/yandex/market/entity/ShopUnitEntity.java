package com.yandex.market.entity;

import com.yandex.market.enums.ShopUnitType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "SHOP_UNIT_ENTITY")
@Getter
@Setter
@NoArgsConstructor
public class ShopUnitEntity extends AbstractEntity {

    @Column
    private String name;

    @ManyToOne
    @JoinColumn(name = "PARENT_ID")
    private ShopUnitEntity parentShopUnitEntity;

    @Column
    private Integer price;

    @Column
    @Enumerated(value = EnumType.STRING)
    private ShopUnitType type;

    @Column
    private LocalDateTime createdDate;

    @Column
    private LocalDateTime updatedDate;

    @OneToMany(cascade = CascadeType.ALL , fetch = FetchType.LAZY, mappedBy = "shopUnitEntity")
    private List<ShopUnitHistory> historyList;

}
