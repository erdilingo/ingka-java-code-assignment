package com.fulfilment.application.monolith.warehouses.domain.models;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class LocationDTOTest {

  @Test
  public void testConstructorAndFields() {
    LocationDTO dto = new LocationDTO("ZWOLLE-001", 3, 100);

    assertEquals("ZWOLLE-001", dto.identification);
    assertEquals(3, dto.maxNumberOfWarehouses);
    assertEquals(100, dto.maxCapacity);
  }
}