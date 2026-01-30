package com.fulfilment.application.monolith.warehouses.domain.utils;

import java.time.ZonedDateTime;

public class WarehouseTimestampUtil {

  private WarehouseTimestampUtil() {
    // Utility class
  }

  public static ZonedDateTime getCurrentTimestamp() {
    return ZonedDateTime.now();
  }
}
