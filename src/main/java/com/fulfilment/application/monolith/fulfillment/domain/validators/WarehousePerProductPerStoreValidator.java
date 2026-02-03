package com.fulfilment.application.monolith.fulfillment.domain.validators;

import com.fulfilment.application.monolith.fulfillment.domain.exceptions.FulfillmentValidationException;
import com.fulfilment.application.monolith.fulfillment.domain.ports.FulfillmentStore;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class WarehousePerProductPerStoreValidator {

  private static final int MAX_WAREHOUSES_PER_PRODUCT_PER_STORE = 2;

  private final FulfillmentStore fulfillmentStore;

  public WarehousePerProductPerStoreValidator(FulfillmentStore fulfillmentStore) {
    this.fulfillmentStore = fulfillmentStore;
  }

  public void validate(Long productId, Long storeId) {
    long count = fulfillmentStore.countDistinctWarehousesByProductAndStore(productId, storeId);
    if (count >= MAX_WAREHOUSES_PER_PRODUCT_PER_STORE) {
      throw new FulfillmentValidationException(
          "Product already has the maximum of "
              + MAX_WAREHOUSES_PER_PRODUCT_PER_STORE
              + " warehouses for this store.");
    }
  }
}
