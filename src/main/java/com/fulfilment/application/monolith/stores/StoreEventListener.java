package com.fulfilment.application.monolith.stores;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.enterprise.event.TransactionPhase;
import jakarta.inject.Inject;

@ApplicationScoped
public class StoreEventListener {
  
  @Inject
  LegacyStoreManagerGateway legacyStoreManagerGateway;
  
  public void onStoreEvent(@Observes(during = TransactionPhase.AFTER_SUCCESS) StoreEvent event) {
    Store store = event.store();
    
    switch (event.action()) {
      case CREATE -> legacyStoreManagerGateway.createStoreOnLegacySystem(store);
      case UPDATE -> legacyStoreManagerGateway.updateStoreOnLegacySystem(store);
    }
  }
}
