package com.fulfilment.application.monolith.warehouses.domain.validators;

import com.fulfilment.application.monolith.warehouses.domain.exceptions.WarehouseValidationException;
import com.fulfilment.application.monolith.warehouses.domain.models.LocationDTO;
import com.fulfilment.application.monolith.warehouses.domain.models.WarehouseDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class CapacityValidatorTest {

  private CapacityValidator capacityValidator;

  @BeforeEach
  public void setUp() {
    capacityValidator = new CapacityValidator();
  }

  // validateStockDoesNotExceedCapacity tests

  @Test
  public void testStockDoesNotExceedCapacity_valid() {
    assertDoesNotThrow(() -> capacityValidator.validateStockDoesNotExceedCapacity(10, 20));
  }

  @Test
  public void testStockEqualsCapacity_valid() {
    assertDoesNotThrow(() -> capacityValidator.validateStockDoesNotExceedCapacity(20, 20));
  }

  @Test
  public void testStockExceedsCapacity_throws() {
    WarehouseValidationException ex = assertThrows(WarehouseValidationException.class, () ->
        capacityValidator.validateStockDoesNotExceedCapacity(30, 20)
    );
    assertTrue(ex.getMessage().contains("stock cannot exceed"));
  }

  // validateLocationCapacity tests

  @Test
  public void testLocationCapacity_valid() {
    LocationDTO location = new LocationDTO("LOC1", 5, 100);
    assertDoesNotThrow(() -> capacityValidator.validateLocationCapacity(50, 30, location, "LOC1"));
  }

  @Test
  public void testLocationCapacity_exactlyAtMax() {
    LocationDTO location = new LocationDTO("LOC1", 5, 100);
    assertDoesNotThrow(() -> capacityValidator.validateLocationCapacity(70, 30, location, "LOC1"));
  }

  @Test
  public void testLocationCapacity_exceedsMax() {
    LocationDTO location = new LocationDTO("LOC1", 5, 100);
    WarehouseValidationException ex = assertThrows(WarehouseValidationException.class, () ->
        capacityValidator.validateLocationCapacity(80, 30, location, "LOC1")
    );
    assertTrue(ex.getMessage().contains("capacity exceeds maximum"));
  }

  // validateLocationCapacityForReplacement tests

  @Test
  public void testLocationCapacityForReplacement_sameLocation_valid() {
    LocationDTO location = new LocationDTO("LOC1", 5, 100);
    WarehouseDTO existing = new WarehouseDTO();
    existing.location = "LOC1";
    existing.capacity = 30;
    WarehouseDTO newWh = new WarehouseDTO();
    newWh.location = "LOC1";
    newWh.capacity = 40;

    assertDoesNotThrow(() ->
        capacityValidator.validateLocationCapacityForReplacement(80, newWh, existing, location)
    );
  }

  @Test
  public void testLocationCapacityForReplacement_sameLocation_exceeds() {
    LocationDTO location = new LocationDTO("LOC1", 5, 100);
    WarehouseDTO existing = new WarehouseDTO();
    existing.location = "LOC1";
    existing.capacity = 10;
    WarehouseDTO newWh = new WarehouseDTO();
    newWh.location = "LOC1";
    newWh.capacity = 50;

    // currentTotal=80, after replacement = 80 - 10 + 50 = 120 > 100
    assertThrows(WarehouseValidationException.class, () ->
        capacityValidator.validateLocationCapacityForReplacement(80, newWh, existing, location)
    );
  }

  @Test
  public void testLocationCapacityForReplacement_differentLocation_valid() {
    LocationDTO newLocation = new LocationDTO("LOC2", 5, 100);
    WarehouseDTO existing = new WarehouseDTO();
    existing.location = "LOC1";
    existing.capacity = 30;
    WarehouseDTO newWh = new WarehouseDTO();
    newWh.location = "LOC2";
    newWh.capacity = 40;

    assertDoesNotThrow(() ->
        capacityValidator.validateLocationCapacityForReplacement(50, newWh, existing, newLocation)
    );
  }

  @Test
  public void testLocationCapacityForReplacement_differentLocation_exceeds() {
    LocationDTO newLocation = new LocationDTO("LOC2", 5, 100);
    WarehouseDTO existing = new WarehouseDTO();
    existing.location = "LOC1";
    existing.capacity = 30;
    WarehouseDTO newWh = new WarehouseDTO();
    newWh.location = "LOC2";
    newWh.capacity = 60;

    // currentTotal=50 + 60 = 110 > 100
    assertThrows(WarehouseValidationException.class, () ->
        capacityValidator.validateLocationCapacityForReplacement(50, newWh, existing, newLocation)
    );
  }

  // validateMaxWarehousesAtLocation tests

  @Test
  public void testMaxWarehouses_belowLimit() {
    LocationDTO location = new LocationDTO("LOC1", 3, 100);
    assertDoesNotThrow(() -> capacityValidator.validateMaxWarehousesAtLocation(1, location, "LOC1"));
  }

  @Test
  public void testMaxWarehouses_atLimit_throws() {
    LocationDTO location = new LocationDTO("LOC1", 3, 100);
    assertThrows(WarehouseValidationException.class, () ->
        capacityValidator.validateMaxWarehousesAtLocation(3, location, "LOC1")
    );
  }

  @Test
  public void testMaxWarehouses_aboveLimit_throws() {
    LocationDTO location = new LocationDTO("LOC1", 3, 100);
    assertThrows(WarehouseValidationException.class, () ->
        capacityValidator.validateMaxWarehousesAtLocation(5, location, "LOC1")
    );
  }

  // validateStockMatches tests

  @Test
  public void testStockMatches_valid() {
    assertDoesNotThrow(() -> capacityValidator.validateStockMatches(10, 10));
  }

  @Test
  public void testStockMatches_mismatch_throws() {
    WarehouseValidationException ex = assertThrows(WarehouseValidationException.class, () ->
        capacityValidator.validateStockMatches(10, 20)
    );
    assertTrue(ex.getMessage().contains("stock must match"));
  }
}