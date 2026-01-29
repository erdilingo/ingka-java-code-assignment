package com.fulfilment.application.monolith.stores;

public record StoreEvent(Store store, Action action) {
  
  public enum Action {
    CREATE, UPDATE
  }
}