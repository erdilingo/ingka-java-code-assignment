package com.fulfilment.application.monolith.fulfillment.domain.validators;

import com.fulfilment.application.monolith.fulfillment.domain.exceptions.FulfillmentValidationException;
import com.fulfilment.application.monolith.fulfillment.domain.ports.FulfillmentStore;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class WarehousePerStoreValidator {

  private static final int MAX_WAREHOUSES_PER_STORE = 3;

  private final FulfillmentStore fulfillmentStore;

  public WarehousePerStoreValidator(FulfillmentStore fulfillmentStore) {
    this.fulfillmentStore = fulfillmentStore;
  }

  public void validate(Long storeId) {
    long count = fulfillmentStore.countDistinctWarehousesByStore(storeId);
    if (count >= MAX_WAREHOUSES_PER_STORE) {
      throw new FulfillmentValidationException(
          "Store already has the maximum of " + MAX_WAREHOUSES_PER_STORE + " warehouses.");
    }
  }
}
