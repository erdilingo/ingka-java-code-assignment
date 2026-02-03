package com.fulfilment.application.monolith.fulfillment.domain.ports;

import com.fulfilment.application.monolith.fulfillment.domain.models.FulfillmentDTO;
import java.util.List;

public interface FulfillmentStore {

  List<FulfillmentDTO> findAllFulfillments();

  List<FulfillmentDTO> findByStoreId(Long storeId);

  List<FulfillmentDTO> findByWarehouseId(Long warehouseId);

  FulfillmentDTO findFulfillmentById(Long id);

  void persistFulfillment(FulfillmentDTO dto);

  void deleteFulfillment(Long id);

  long countDistinctWarehousesByProductAndStore(Long productId, Long storeId);

  long countDistinctWarehousesByStore(Long storeId);

  long countDistinctProductsByWarehouse(Long warehouseId);

  boolean warehouseExists(Long warehouseId);

  boolean productExists(Long productId);

  boolean storeExists(Long storeId);
}
