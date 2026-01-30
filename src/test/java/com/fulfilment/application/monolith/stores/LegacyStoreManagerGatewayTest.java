package com.fulfilment.application.monolith.stores;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

public class LegacyStoreManagerGatewayTest {

  private LegacyStoreManagerGateway gateway;

  @BeforeEach
  public void setUp() {
    gateway = new LegacyStoreManagerGateway();
  }

  @Test
  public void testCreateStoreOnLegacySystem() {
    Store store = new Store("TestStore");
    store.quantityProductsInStock = 10;

    assertDoesNotThrow(() -> gateway.createStoreOnLegacySystem(store));
  }

  @Test
  public void testUpdateStoreOnLegacySystem() {
    Store store = new Store("TestStore");
    store.quantityProductsInStock = 20;

    assertDoesNotThrow(() -> gateway.updateStoreOnLegacySystem(store));
  }
}