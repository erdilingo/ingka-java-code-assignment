package com.fulfilment.application.monolith.fulfillment.domain.validators;

import com.fulfilment.application.monolith.fulfillment.domain.exceptions.FulfillmentValidationException;
import com.fulfilment.application.monolith.fulfillment.domain.ports.FulfillmentStore;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class ProductPerWarehouseValidator {

  private static final int MAX_PRODUCTS_PER_WAREHOUSE = 5;

  private final FulfillmentStore fulfillmentStore;

  public ProductPerWarehouseValidator(FulfillmentStore fulfillmentStore) {
    this.fulfillmentStore = fulfillmentStore;
  }

  public void validate(Long warehouseId) {
    long count = fulfillmentStore.countDistinctProductsByWarehouse(warehouseId);
    if (count >= MAX_PRODUCTS_PER_WAREHOUSE) {
      throw new FulfillmentValidationException(
          "Warehouse already has the maximum of "
              + MAX_PRODUCTS_PER_WAREHOUSE
              + " product types.");
    }
  }
}
