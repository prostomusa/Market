package com.yandex.market.entity;

import com.yandex.market.enums.ShopUnitType;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Formula;
import org.hibernate.annotations.JoinFormula;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "SHOP_UNIT_ENTITY")
@Getter
@Setter
@EqualsAndHashCode(of = {"id"})
@NoArgsConstructor
public class ShopUnitEntity implements Serializable {

    private static final String PARENT_QUERY = "(SELECT sue.id FROM SHOP_UNIT_ENTITY sue WHERE sue.id = parent_id)";

    @Id
    @Column(name = "id", nullable = false)
    private UUID id;

    @Column
    private String name;

    @Column
    private UUID parentId;

    @Column
    private Integer price;

    @Column
    @Enumerated(value = EnumType.STRING)
    private ShopUnitType type;

    @Column
    private LocalDateTime createdDate;

    @Column
    private LocalDateTime updatedDate;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    @JoinFormula(value = PARENT_QUERY)
    private ShopUnitEntity parentEntity;

    @OneToMany(mappedBy = "shopUnitEntity", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<ShopUnitHistory> history;

}
