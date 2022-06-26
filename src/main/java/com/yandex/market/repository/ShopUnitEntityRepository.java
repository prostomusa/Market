package com.yandex.market.repository;

import com.yandex.market.entity.ShopUnitEntity;
import com.yandex.market.enums.ShopUnitType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Repository
public interface ShopUnitEntityRepository extends JpaRepository<ShopUnitEntity, UUID>, JpaSpecificationExecutor<ShopUnitEntity> {

    List<ShopUnitEntity> getAllByIdIn(Set<UUID> id);

    List<ShopUnitEntity> getAllByParentId(UUID uuid);

    List<ShopUnitEntity> getAllByUpdatedDateAfterAndUpdatedDateBeforeAndTypeIs(LocalDateTime startDate, LocalDateTime endDate, ShopUnitType type);

}
