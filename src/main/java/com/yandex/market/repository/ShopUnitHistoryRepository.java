package com.yandex.market.repository;

import com.yandex.market.entity.ShopUnitHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface ShopUnitHistoryRepository extends JpaRepository<ShopUnitHistory, UUID>, JpaSpecificationExecutor<ShopUnitHistory> {

    List<ShopUnitHistory> getAllByShopUnitEntityId(UUID id);

    List<ShopUnitHistory> getAllByShopUnitEntityIdAndUpdatedDateAfter(UUID id, LocalDateTime localDateTime);

    List<ShopUnitHistory> getAllByShopUnitEntityIdAndUpdatedDateBefore(UUID id, LocalDateTime localDateTime);

    List<ShopUnitHistory> getAllByShopUnitEntityIdAndUpdatedDateAfterAndUpdatedDateBefore(UUID id, LocalDateTime startDate, LocalDateTime endDate);

}