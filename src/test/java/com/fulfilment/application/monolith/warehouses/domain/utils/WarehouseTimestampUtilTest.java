package com.fulfilment.application.monolith.warehouses.domain.utils;

import org.junit.jupiter.api.Test;

import java.time.ZonedDateTime;

import static org.junit.jupiter.api.Assertions.*;

public class WarehouseTimestampUtilTest {

  @Test
  public void testGetCurrentTimestampReturnsNonNull() {
    ZonedDateTime timestamp = WarehouseTimestampUtil.getCurrentTimestamp();
    assertNotNull(timestamp);
  }

  @Test
  public void testGetCurrentTimestampIsCloseToNow() {
    ZonedDateTime before = ZonedDateTime.now();
    ZonedDateTime timestamp = WarehouseTimestampUtil.getCurrentTimestamp();
    ZonedDateTime after = ZonedDateTime.now();

    assertFalse(timestamp.isBefore(before));
    assertFalse(timestamp.isAfter(after));
  }
}