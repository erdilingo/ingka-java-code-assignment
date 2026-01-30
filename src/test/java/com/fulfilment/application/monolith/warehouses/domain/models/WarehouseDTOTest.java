package com.fulfilment.application.monolith.warehouses.domain.models;

import org.junit.jupiter.api.Test;

import java.time.ZonedDateTime;

import static org.junit.jupiter.api.Assertions.*;

public class WarehouseDTOTest {

  @Test
  public void testFieldsAssignment() {
    WarehouseDTO dto = new WarehouseDTO();
    ZonedDateTime now = ZonedDateTime.now();

    dto.businessUnitCode = "WH001";
    dto.location = "ZWOLLE-001";
    dto.capacity = 50;
    dto.stock = 10;
    dto.creationAt = now;
    dto.archivedAt = null;

    assertEquals("WH001", dto.businessUnitCode);
    assertEquals("ZWOLLE-001", dto.location);
    assertEquals(50, dto.capacity);
    assertEquals(10, dto.stock);
    assertEquals(now, dto.creationAt);
    assertNull(dto.archivedAt);
  }
}