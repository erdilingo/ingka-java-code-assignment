package com.fulfilment.application.monolith.stores;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.enterprise.event.TransactionPhase;
import jakarta.inject.Inject;
import org.jboss.logging.Logger;

@ApplicationScoped
public class StoreEventListener {

  private static final Logger LOG = Logger.getLogger(StoreEventListener.class);

  @Inject
  LegacyStoreManagerGateway legacyStoreManagerGateway;

  public void onStoreEvent(@Observes(during = TransactionPhase.AFTER_SUCCESS) StoreEvent event) {
    Store store = event.store();
    LOG.infof("Received store event: %s for store: %s", event.action(), store.name);

    switch (event.action()) {
      case CREATE -> legacyStoreManagerGateway.createStoreOnLegacySystem(store);
      case UPDATE -> legacyStoreManagerGateway.updateStoreOnLegacySystem(store);
    }
  }
}
