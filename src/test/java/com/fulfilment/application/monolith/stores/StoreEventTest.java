package com.fulfilment.application.monolith.stores;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class StoreEventTest {

  @Test
  public void testStoreEventRecordWithCreateAction() {
    Store store = new Store("TestStore");
    StoreEvent event = new StoreEvent(store, StoreEvent.Action.CREATE);

    assertEquals(store, event.store());
    assertEquals(StoreEvent.Action.CREATE, event.action());
  }

  @Test
  public void testStoreEventRecordWithUpdateAction() {
    Store store = new Store("TestStore");
    StoreEvent event = new StoreEvent(store, StoreEvent.Action.UPDATE);

    assertEquals(store, event.store());
    assertEquals(StoreEvent.Action.UPDATE, event.action());
  }

  @Test
  public void testStoreEventEquality() {
    Store store = new Store("TestStore");
    StoreEvent event1 = new StoreEvent(store, StoreEvent.Action.CREATE);
    StoreEvent event2 = new StoreEvent(store, StoreEvent.Action.CREATE);

    assertEquals(event1, event2);
    assertEquals(event1.hashCode(), event2.hashCode());
  }

  @Test
  public void testStoreEventInequality() {
    Store store = new Store("TestStore");
    StoreEvent createEvent = new StoreEvent(store, StoreEvent.Action.CREATE);
    StoreEvent updateEvent = new StoreEvent(store, StoreEvent.Action.UPDATE);

    assertNotEquals(createEvent, updateEvent);
  }

  @Test
  public void testActionEnumValues() {
    StoreEvent.Action[] values = StoreEvent.Action.values();
    assertEquals(2, values.length);
    assertEquals(StoreEvent.Action.CREATE, StoreEvent.Action.valueOf("CREATE"));
    assertEquals(StoreEvent.Action.UPDATE, StoreEvent.Action.valueOf("UPDATE"));
  }
}