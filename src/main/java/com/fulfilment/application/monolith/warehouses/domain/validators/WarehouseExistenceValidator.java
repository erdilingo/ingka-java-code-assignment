package com.fulfilment.application.monolith.warehouses.domain.validators;

import com.fulfilment.application.monolith.warehouses.domain.exceptions.WarehouseNotFoundException;
import com.fulfilment.application.monolith.warehouses.domain.exceptions.WarehouseValidationException;
import com.fulfilment.application.monolith.warehouses.domain.models.WarehouseDTO;
import jakarta.enterprise.context.ApplicationScoped;
import org.jboss.logging.Logger;

@ApplicationScoped
public class WarehouseExistenceValidator {

  private static final Logger LOG = Logger.getLogger(WarehouseExistenceValidator.class);

  public void validateWarehouseExists(WarehouseDTO warehouse, String businessUnitCode) {
    if (warehouse == null) {
      LOG.errorf("Warehouse %s does not exist", businessUnitCode);
      throw new WarehouseNotFoundException("Warehouse with business unit code " + businessUnitCode + " does not exist");
    }
  }

  public void validateWarehouseDoesNotExist(WarehouseDTO warehouse, String businessUnitCode) {
    if (warehouse != null) {
      LOG.errorf("Warehouse %s already exists", businessUnitCode);
      throw new WarehouseValidationException("Warehouse with business unit code " + businessUnitCode + " already exists");
    }
  }

  public void validateWarehouseNotArchived(WarehouseDTO warehouse) {
    if (warehouse.archivedAt != null) {
      LOG.errorf("Warehouse %s is already archived", warehouse.businessUnitCode);
      throw new WarehouseValidationException("Warehouse with business unit code " + warehouse.businessUnitCode + " is already archived");
    }
  }

  public void validateCannotReplaceArchivedWarehouse(WarehouseDTO warehouse) {
    if (warehouse.archivedAt != null) {
      LOG.errorf("Cannot replace archived warehouse %s", warehouse.businessUnitCode);
      throw new WarehouseValidationException("Cannot replace an already archived warehouse");
    }
  }
}
