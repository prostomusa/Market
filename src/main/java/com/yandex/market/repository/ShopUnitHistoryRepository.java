package com.yandex.market.repository;

import com.yandex.market.entity.ShopUnitHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import java.util.UUID;

public interface ShopUnitHistoryRepository extends JpaRepository<ShopUnitHistory, UUID>, JpaSpecificationExecutor<ShopUnitHistory> {

}