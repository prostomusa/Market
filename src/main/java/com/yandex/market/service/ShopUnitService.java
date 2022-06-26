package com.yandex.market.service;

import com.yandex.market.entity.ShopUnitEntity;
import com.yandex.market.entity.ShopUnitHistory;
import com.yandex.market.enums.ShopUnitType;
import com.yandex.market.exception.IncorrectDataException;
import com.yandex.market.exception.NotFoundException;
import com.yandex.market.model.*;
import com.yandex.market.repository.ShopUnitEntityRepository;
import com.yandex.market.repository.ShopUnitHistoryRepository;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import javax.annotation.Nullable;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
public class ShopUnitService {

    private final ShopUnitEntityRepository shopUnitEntityRepository;

    private final ShopUnitHistoryRepository shopUnitHistoryRepository;

    @Transactional
    @Async
    public CompletableFuture<ResponseEntity<ShopUnitStatisticResponse>> getStatistic(
            @NonNull UUID id,
            @Nullable LocalDateTime startDate,
            @Nullable LocalDateTime endDate) {
        List<ShopUnitHistory> shopUnitHistoryList;
        ShopUnitEntity shopUnitEntity = shopUnitEntityRepository.findById(id).orElse(null);
        if (shopUnitEntity == null) {
            throw new NotFoundException();
        }
        if (startDate == null && endDate == null) {
            shopUnitHistoryList = shopUnitHistoryRepository.getAllByShopUnitEntityId(id);
        } else {
            if (startDate == null) {
                shopUnitHistoryList = shopUnitHistoryRepository.getAllByShopUnitEntityIdAndUpdatedDateBefore(id, endDate);
            } else if (endDate == null) {
                shopUnitHistoryList = shopUnitHistoryRepository.getAllByShopUnitEntityIdAndUpdatedDateAfter(id, startDate.minusSeconds(1));
            } else {
                shopUnitHistoryList = shopUnitHistoryRepository
                        .getAllByShopUnitEntityIdAndUpdatedDateAfterAndUpdatedDateBefore(id, startDate.minusSeconds(1), endDate);
            }
        }
        boolean isCategory = ShopUnitType.CATEGORY.equals(shopUnitEntity.getType());
        Integer price = null;
        if (isCategory) {
            price = avg(getPriceForShopEntity(shopUnitEntity));
        }
        List<ShopUnitStaticUnit> shopUnitStaticUnitList = new ArrayList<>();
        for (ShopUnitHistory shopUnitHistory : shopUnitHistoryList) {
            ShopUnitStaticUnit shopUnitStaticUnit = new ShopUnitStaticUnit();
            shopUnitStaticUnit.setId(shopUnitHistory.getShopUnitEntity().getId());
            shopUnitStaticUnit.setName(shopUnitHistory.getName());
            shopUnitStaticUnit.setUpdateDate(shopUnitHistory.getUpdatedDate());
            shopUnitStaticUnit.setType(shopUnitHistory.getType());
            shopUnitStaticUnit.setPrice(isCategory ? price : shopUnitHistory.getPrice());
            shopUnitStaticUnit.setParentId(shopUnitHistory.getParentShopUnitEntityId());
            shopUnitStaticUnitList.add(shopUnitStaticUnit);
        }
        ShopUnitStatisticResponse shopUnitStatisticResponse = new ShopUnitStatisticResponse();
        shopUnitStatisticResponse.setItems(shopUnitStaticUnitList);
        return CompletableFuture.completedFuture(ResponseEntity.ok(shopUnitStatisticResponse));
    }

    @Transactional
    @Async
    public CompletableFuture<ResponseEntity<ShopUnitStatisticResponse>> getSales(@NonNull LocalDateTime localDateTime) {
        LocalDateTime startDate = localDateTime.minusHours(24).minusSeconds(1);
        LocalDateTime endDate = localDateTime.plusSeconds(1);
        List<ShopUnitEntity> shopUnitEntityList = shopUnitEntityRepository
                .getAllByUpdatedDateAfterAndUpdatedDateBeforeAndTypeIs(startDate, endDate, ShopUnitType.OFFER);
        List<ShopUnitStaticUnit> shopUnitStaticUnitList = new ArrayList<>();
        for (ShopUnitEntity shopUnitEntity : shopUnitEntityList) {
            ShopUnitStaticUnit shopUnitStaticUnit = new ShopUnitStaticUnit();
            shopUnitStaticUnit.setId(shopUnitEntity.getId());
            shopUnitStaticUnit.setName(shopUnitEntity.getName());
            shopUnitStaticUnit.setUpdateDate(shopUnitEntity.getUpdatedDate());
            shopUnitStaticUnit.setPrice(shopUnitEntity.getPrice());
            shopUnitStaticUnit.setType(shopUnitEntity.getType());
            shopUnitStaticUnit.setParentId(shopUnitEntity.getParentId());
            shopUnitStaticUnitList.add(shopUnitStaticUnit);
        }
        ShopUnitStatisticResponse shopUnitStatisticResponse = new ShopUnitStatisticResponse();
        shopUnitStatisticResponse.setItems(shopUnitStaticUnitList);
        return CompletableFuture.completedFuture(ResponseEntity.ok(shopUnitStatisticResponse));
    }

    @Transactional
    @Async
    public CompletableFuture<ResponseEntity<ShopUnit>> getNodes(@NonNull UUID uuid) {
        ShopUnitEntity shopUnitEntity = shopUnitEntityRepository.findById(uuid).orElse(null);;
        if (shopUnitEntity == null) {
            throw new NotFoundException();
        }
        ShopUnit shopUnit = buildShopUnit(shopUnitEntity);
        ArrayList<Integer> mainSum = setPriceForShopUnit(shopUnit);
        shopUnit.setPrice(avg(mainSum));
        return CompletableFuture.completedFuture(ResponseEntity.ok(shopUnit));
    }

    @Transactional
    @Async
    public CompletableFuture<ResponseEntity> deleteShopUnit(@NonNull UUID uuid) {
        List<ShopUnitEntity> shopUnits = new ArrayList<>();
        ShopUnitEntity shopUnitEntity = shopUnitEntityRepository.findById(uuid).orElse(null);
        if (shopUnitEntity == null) {
            throw new NotFoundException();
        }
        shopUnits.add(shopUnitEntity);
        addShopUnitForDelete(shopUnitEntity.getId(), shopUnits);
        shopUnitEntityRepository.deleteAll(shopUnits);
        return CompletableFuture.completedFuture(ResponseEntity.ok("Удаление прошло успешно."));
    }

    @Transactional
    @Async
    public CompletableFuture<ResponseEntity> importShopUnits(@NonNull ShopUnitImportRequest shopUnitImportRequest) {
        Map<UUID, ShopUnitImport> mapIdShopUnitImport = new HashMap<>();
        for (ShopUnitImport shopUnitImport : shopUnitImportRequest.getItems()) {
            checkModel(shopUnitImport);
            if (mapIdShopUnitImport.containsKey(shopUnitImport.getId())) {
                throw new IncorrectDataException("duplicate ID");
            }
            mapIdShopUnitImport.put(shopUnitImport.getId(), shopUnitImport);
        }
        List<ShopUnitEntity> shopUnitEntityList = shopUnitEntityRepository.getAllByIdIn(mapIdShopUnitImport.keySet());
        Set<UUID> existShopUnitEntity = new HashSet<>();
        shopUnitEntityList.forEach(shopUnitEntity -> {
            ShopUnitImport shopUnitImport = mapIdShopUnitImport.get(shopUnitEntity.getId());

            if (!shopUnitEntity.getType().equals(shopUnitImport.getType())) {
                throw new IncorrectDataException("change type");
            }
            shopUnitEntity.setUpdatedDate(shopUnitImportRequest.getUpdateDate());
            shopUnitEntity.setParentId(shopUnitImport.getParentId());
            shopUnitEntity.setPrice(shopUnitImport.getPrice());
            shopUnitEntity.setType(shopUnitImport.getType());
            shopUnitEntity.setName(shopUnitImport.getName());
            existShopUnitEntity.add(shopUnitEntity.getId());
        });
        for (UUID uuid : mapIdShopUnitImport.keySet()) {
            if (!existShopUnitEntity.contains(uuid)) {
                ShopUnitImport shopUnitImport = mapIdShopUnitImport.get(uuid);

                ShopUnitEntity shopUnitEntity = new ShopUnitEntity();
                shopUnitEntity.setId(shopUnitImport.getId());
                shopUnitEntity.setType(shopUnitImport.getType());
                shopUnitEntity.setParentId(shopUnitImport.getParentId());
                shopUnitEntity.setPrice(shopUnitImport.getPrice());
                shopUnitEntity.setName(shopUnitImport.getName());
                shopUnitEntity.setCreatedDate(shopUnitImportRequest.getUpdateDate());
                shopUnitEntity.setUpdatedDate(shopUnitImportRequest.getUpdateDate());
                shopUnitEntityList.add(shopUnitEntity);
            }
        }
        shopUnitEntityRepository.saveAll(shopUnitEntityList);
        ArrayList<ShopUnitEntity> listOfEntities = new ArrayList<>();
        List<ShopUnitHistory> shopUnitHistoryList = new ArrayList<>();
        Set<UUID> existHistory = new HashSet<>();
        for (int i = 0; i < shopUnitEntityList.size(); i++) {
            ShopUnitEntity shopUnitEntity = shopUnitEntityList.get(i);
            if (ShopUnitType.OFFER.equals(shopUnitEntity.getType()) && shopUnitEntity.getParentId() == null) {
                ShopUnitHistory shopUnitHistory = createShopUnitHistory(shopUnitEntity, shopUnitImportRequest.getUpdateDate());
                shopUnitHistoryList.add(shopUnitHistory);
            }
            else {
                ShopUnitHistory shopUnitHistory = createShopUnitHistory(shopUnitEntity, shopUnitImportRequest.getUpdateDate());
                shopUnitHistoryList.add(shopUnitHistory);
                if (!mapIdShopUnitImport.containsKey(shopUnitEntity.getParentId())) {
                    if (shopUnitEntity.getParentId() != null && !existHistory.contains(shopUnitEntity.getParentId())) {
                        ShopUnitEntity parentShopUnit = shopUnitEntityRepository.findById(shopUnitEntity.getParentId()).orElse(null);
                        if (parentShopUnit == null) {
                            throw new IncorrectDataException("parent doesn't exist");
                        }
                        while (parentShopUnit != null) {
                            ShopUnitHistory parentShopUnitHistory = createShopUnitHistory(parentShopUnit, shopUnitImportRequest.getUpdateDate());
                            parentShopUnit.setUpdatedDate(shopUnitImportRequest.getUpdateDate());
                            shopUnitHistoryList.add(parentShopUnitHistory);
                            listOfEntities.add(parentShopUnit);
                            existHistory.add(parentShopUnit.getId());
                            if (parentShopUnit.getParentId() == null) {
                                break;
                            }
                            parentShopUnit = shopUnitEntityRepository.findById(parentShopUnit.getParentId()).orElse(null);
                        }
                    }
                }
            }
        }
        shopUnitEntityRepository.saveAll(listOfEntities);
        shopUnitHistoryRepository.saveAll(shopUnitHistoryList);
        return CompletableFuture.completedFuture(ResponseEntity.ok("Вставка или обновление прошли успешно."));
    }

    private void checkModel(ShopUnitImport shopUnitImport) {
        if (ShopUnitType.OFFER.equals(shopUnitImport.getType()) && shopUnitImport.getPrice() == null) {
            throw new IncorrectDataException("Null value");
        }
        if (ShopUnitType.CATEGORY.equals(shopUnitImport.getType()) && shopUnitImport.getPrice() != null) {
            throw new IncorrectDataException("Not null price for CATEGORY");
        }
    }

    private ArrayList<Integer> getPriceForShopEntity(ShopUnitEntity shopUnitEntity) {
        ArrayList<Integer> sums = new ArrayList<>();
        List<ShopUnitEntity> children = shopUnitEntityRepository.getAllByParentId(shopUnitEntity.getId());
        if (!children.isEmpty()) {
            for (ShopUnitEntity child : children) {
                if (!ShopUnitType.CATEGORY.equals(child.getType())) {
                    sums.add(child.getPrice());
                } else {
                    sums.addAll(getPriceForShopEntity(child));
                }
            }
        }
        return sums;
    }

    private ArrayList<Integer> setPriceForShopUnit(ShopUnit shopUnit) {
        ArrayList<Integer> sums = new ArrayList<>();
        if (!shopUnit.getChildren().isEmpty()) {
            for (ShopUnit children : shopUnit.getChildren()) {
                if (!ShopUnitType.CATEGORY.equals(children.getType())) {
                    sums.add(children.getPrice());
                } else {
                    ArrayList<Integer> localSum = setPriceForShopUnit(children);
                    children.setPrice(avg(localSum));
                    sums.addAll(localSum);
                }
            }
        }
        return sums;
    }

    private Integer avg(ArrayList<Integer> sums) {
        if (sums == null || sums.isEmpty()) {
            return null;
        }
        Integer r = 0;
        for (Integer i : sums) {
            r += i;
        }
        return r/sums.size();
    }

    private void addShopUnitForDelete(UUID uuid, List<ShopUnitEntity> list) {
        List<ShopUnitEntity> children = shopUnitEntityRepository.getAllByParentId(uuid);
        if (!children.isEmpty()) {
            list.addAll(children);
            for (ShopUnitEntity shopUnitEntity : children) {
                addShopUnitForDelete(shopUnitEntity.getId(), list);
            }
        }
    }

    private ShopUnitHistory createShopUnitHistory(ShopUnitEntity shopUnitEntity, LocalDateTime updatedDate) {
        ShopUnitHistory shopUnitHistory = new ShopUnitHistory();
        shopUnitHistory.setShopUnitEntity(shopUnitEntity);
        shopUnitHistory.setParentShopUnitEntityId(shopUnitEntity.getParentId());
        shopUnitHistory.setName(shopUnitEntity.getName());
        shopUnitHistory.setType(shopUnitEntity.getType());
        shopUnitHistory.setPrice(shopUnitEntity.getPrice());
        shopUnitHistory.setUpdatedDate(updatedDate);
        return shopUnitHistory;
    }

    private ShopUnit buildShopUnit(ShopUnitEntity shopUnitEntity) {
        ShopUnit shopUnit = mapShopUnitEntityAndShopUnit(shopUnitEntity);
        if (ShopUnitType.OFFER.equals(shopUnitEntity.getType())) {
            shopUnit.setChildren(null);
        } else {
            List<ShopUnitEntity> children = shopUnitEntityRepository.getAllByParentId(shopUnitEntity.getId());
            if (!children.isEmpty()) {
                ArrayList<ShopUnit> shopUnits = new ArrayList<>();
                for (ShopUnitEntity child : children) {
                    shopUnits.add(buildShopUnit(child));
                }
                shopUnit.setChildren(shopUnits);
            } else {
                shopUnit.setChildren(Collections.emptyList());
            }
        }
        return shopUnit;
    }

    private ShopUnit mapShopUnitEntityAndShopUnit(ShopUnitEntity shopUnitEntity) {
        ShopUnit shopUnit = new ShopUnit();
        shopUnit.setParentId(shopUnitEntity.getParentId());
        shopUnit.setId(shopUnitEntity.getId());
        shopUnit.setName(shopUnitEntity.getName());
        shopUnit.setPrice(shopUnitEntity.getPrice());
        shopUnit.setType(shopUnitEntity.getType());
        shopUnit.setDate(shopUnitEntity.getUpdatedDate());
        return shopUnit;
    }

}
