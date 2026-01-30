package com.fulfilment.application.monolith.stores;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class StoreTest {

  @Test
  public void testDefaultConstructor() {
    Store store = new Store();
    assertNull(store.name);
    assertEquals(0, store.quantityProductsInStock);
  }

  @Test
  public void testNameConstructor() {
    Store store = new Store("AMSTERDAM");
    assertEquals("AMSTERDAM", store.name);
    assertEquals(0, store.quantityProductsInStock);
  }

  @Test
  public void testFieldAssignment() {
    Store store = new Store();
    store.name = "HAARLEM";
    store.quantityProductsInStock = 42;

    assertEquals("HAARLEM", store.name);
    assertEquals(42, store.quantityProductsInStock);
  }
}