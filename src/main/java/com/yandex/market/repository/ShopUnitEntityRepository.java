package com.yandex.market.repository;

import com.yandex.market.entity.ShopUnitEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.UUID;

public interface ShopUnitEntityRepository extends JpaRepository<ShopUnitEntity, UUID>, JpaSpecificationExecutor<ShopUnitEntity> {

}
