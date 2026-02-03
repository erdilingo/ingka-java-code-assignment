package com.fulfilment.application.monolith.fulfillment.adapters.database;

import com.fulfilment.application.monolith.fulfillment.domain.models.FulfillmentDTO;
import com.fulfilment.application.monolith.fulfillment.domain.ports.FulfillmentStore;
import com.fulfilment.application.monolith.products.Product;
import com.fulfilment.application.monolith.stores.Store;
import com.fulfilment.application.monolith.warehouses.adapters.database.DbWarehouse;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import java.util.List;

@ApplicationScoped
public class FulfillmentRepositoryAdapter
    implements FulfillmentStore, PanacheRepository<DbFulfillment> {

  private final EntityManager entityManager;

  public FulfillmentRepositoryAdapter(EntityManager entityManager) {
    this.entityManager = entityManager;
  }

  @Override
  public List<FulfillmentDTO> findAllFulfillments() {
    return listAll().stream().map(this::toDomain).toList();
  }

  @Override
  public List<FulfillmentDTO> findByStoreId(Long storeId) {
    return list("store.id", storeId).stream().map(this::toDomain).toList();
  }

  @Override
  public List<FulfillmentDTO> findByWarehouseId(Long warehouseId) {
    return list("warehouse.id", warehouseId).stream().map(this::toDomain).toList();
  }

  @Override
  public FulfillmentDTO findFulfillmentById(Long id) {
    DbFulfillment entity = find("id", id).firstResult();
    return entity != null ? toDomain(entity) : null;
  }

  @Override
  public void persistFulfillment(FulfillmentDTO dto) {
    DbFulfillment entity = new DbFulfillment();
    entity.warehouse = entityManager.find(DbWarehouse.class, dto.warehouseId);
    entity.product = entityManager.find(Product.class, dto.productId);
    entity.store = entityManager.find(Store.class, dto.storeId);
    persist(entity);
    dto.id = entity.id;
  }

  @Override
  public void deleteFulfillment(Long id) {
    delete("id", id);
  }

  @Override
  public long countDistinctWarehousesByProductAndStore(Long productId, Long storeId) {
    return getEntityManager()
        .createQuery(
            "select count(distinct f.warehouse.id) from DbFulfillment f "
                + "where f.product.id = :productId and f.store.id = :storeId",
            Long.class)
        .setParameter("productId", productId)
        .setParameter("storeId", storeId)
        .getSingleResult();
  }

  @Override
  public long countDistinctWarehousesByStore(Long storeId) {
    return getEntityManager()
        .createQuery(
            "select count(distinct f.warehouse.id) from DbFulfillment f "
                + "where f.store.id = :storeId",
            Long.class)
        .setParameter("storeId", storeId)
        .getSingleResult();
  }

  @Override
  public long countDistinctProductsByWarehouse(Long warehouseId) {
    return getEntityManager()
        .createQuery(
            "select count(distinct f.product.id) from DbFulfillment f "
                + "where f.warehouse.id = :warehouseId",
            Long.class)
        .setParameter("warehouseId", warehouseId)
        .getSingleResult();
  }

  @Override
  public boolean warehouseExists(Long warehouseId) {
    return entityManager.find(DbWarehouse.class, warehouseId) != null;
  }

  @Override
  public boolean productExists(Long productId) {
    return entityManager.find(Product.class, productId) != null;
  }

  @Override
  public boolean storeExists(Long storeId) {
    return entityManager.find(Store.class, storeId) != null;
  }

  private FulfillmentDTO toDomain(DbFulfillment entity) {
    FulfillmentDTO dto = new FulfillmentDTO();
    dto.id = entity.id;
    dto.warehouseId = entity.warehouse.id;
    dto.productId = entity.product.id;
    dto.storeId = entity.store.id;
    return dto;
  }
}
