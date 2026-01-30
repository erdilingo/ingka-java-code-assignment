package com.fulfilment.application.monolith.warehouses.domain.validators;

import com.fulfilment.application.monolith.warehouses.domain.exceptions.WarehouseNotFoundException;
import com.fulfilment.application.monolith.warehouses.domain.exceptions.WarehouseValidationException;
import com.fulfilment.application.monolith.warehouses.domain.models.WarehouseDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.ZonedDateTime;

import static org.junit.jupiter.api.Assertions.*;

public class WarehouseExistenceValidatorTest {

  private WarehouseExistenceValidator validator;

  @BeforeEach
  public void setUp() {
    validator = new WarehouseExistenceValidator();
  }

  // validateWarehouseExists

  @Test
  public void testValidateWarehouseExists_exists() {
    WarehouseDTO warehouse = new WarehouseDTO();
    assertDoesNotThrow(() -> validator.validateWarehouseExists(warehouse, "WH001"));
  }

  @Test
  public void testValidateWarehouseExists_null_throws() {
    WarehouseNotFoundException ex = assertThrows(WarehouseNotFoundException.class, () ->
        validator.validateWarehouseExists(null, "WH001")
    );
    assertTrue(ex.getMessage().contains("WH001"));
    assertTrue(ex.getMessage().contains("does not exist"));
  }

  // validateWarehouseDoesNotExist

  @Test
  public void testValidateWarehouseDoesNotExist_null_ok() {
    assertDoesNotThrow(() -> validator.validateWarehouseDoesNotExist(null, "WH001"));
  }

  @Test
  public void testValidateWarehouseDoesNotExist_exists_throws() {
    WarehouseDTO warehouse = new WarehouseDTO();
    WarehouseValidationException ex = assertThrows(WarehouseValidationException.class, () ->
        validator.validateWarehouseDoesNotExist(warehouse, "WH001")
    );
    assertTrue(ex.getMessage().contains("WH001"));
    assertTrue(ex.getMessage().contains("already exists"));
  }

  // validateWarehouseNotArchived

  @Test
  public void testValidateWarehouseNotArchived_notArchived() {
    WarehouseDTO warehouse = new WarehouseDTO();
    warehouse.businessUnitCode = "WH001";
    warehouse.archivedAt = null;
    assertDoesNotThrow(() -> validator.validateWarehouseNotArchived(warehouse));
  }

  @Test
  public void testValidateWarehouseNotArchived_archived_throws() {
    WarehouseDTO warehouse = new WarehouseDTO();
    warehouse.businessUnitCode = "WH001";
    warehouse.archivedAt = ZonedDateTime.now();
    WarehouseValidationException ex = assertThrows(WarehouseValidationException.class, () ->
        validator.validateWarehouseNotArchived(warehouse)
    );
    assertTrue(ex.getMessage().contains("already archived"));
  }

  // validateCannotReplaceArchivedWarehouse

  @Test
  public void testValidateCannotReplaceArchived_notArchived() {
    WarehouseDTO warehouse = new WarehouseDTO();
    warehouse.archivedAt = null;
    assertDoesNotThrow(() -> validator.validateCannotReplaceArchivedWarehouse(warehouse));
  }

  @Test
  public void testValidateCannotReplaceArchived_archived_throws() {
    WarehouseDTO warehouse = new WarehouseDTO();
    warehouse.archivedAt = ZonedDateTime.now();
    WarehouseValidationException ex = assertThrows(WarehouseValidationException.class, () ->
        validator.validateCannotReplaceArchivedWarehouse(warehouse)
    );
    assertTrue(ex.getMessage().contains("Cannot replace"));
  }
}