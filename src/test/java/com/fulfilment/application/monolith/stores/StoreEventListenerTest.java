package com.fulfilment.application.monolith.stores;

import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.InjectMock;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.*;

@QuarkusTest
public class StoreEventListenerTest {

  @InjectMock
  LegacyStoreManagerGateway legacyStoreManagerGateway;

  @Inject
  StoreEventListener storeEventListener;

  @Test
  public void testOnStoreEventWithCreateActionShouldCallLegacyGateway() {
    // given
    Store store = new Store();
    store.name = "Test Store";
    store.quantityProductsInStock = 100;
    StoreEvent event = new StoreEvent(store, StoreEvent.Action.CREATE);

    // when
    storeEventListener.onStoreEvent(event);

    // then
    verify(legacyStoreManagerGateway, times(1)).createStoreOnLegacySystem(store);
    verify(legacyStoreManagerGateway, never()).updateStoreOnLegacySystem(any());
  }

  @Test
  public void testOnStoreEventWithUpdateActionShouldCallLegacyGateway() {
    // given
    Store store = new Store();
    store.id = 1L;
    store.name = "Updated Store";
    store.quantityProductsInStock = 200;
    StoreEvent event = new StoreEvent(store, StoreEvent.Action.UPDATE);

    // when
    storeEventListener.onStoreEvent(event);

    // then
    verify(legacyStoreManagerGateway, times(1)).updateStoreOnLegacySystem(store);
    verify(legacyStoreManagerGateway, never()).createStoreOnLegacySystem(any());
  }
}
