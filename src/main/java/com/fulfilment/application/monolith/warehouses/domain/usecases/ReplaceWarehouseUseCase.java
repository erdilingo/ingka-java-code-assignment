package com.fulfilment.application.monolith.warehouses.domain.usecases;

import com.fulfilment.application.monolith.warehouses.domain.models.LocationDTO;
import com.fulfilment.application.monolith.warehouses.domain.models.WarehouseDTO;
import com.fulfilment.application.monolith.warehouses.domain.ports.ReplaceWarehouseOperation;
import com.fulfilment.application.monolith.warehouses.domain.ports.WarehouseStore;
import com.fulfilment.application.monolith.warehouses.domain.utils.WarehouseTimestampUtil;
import com.fulfilment.application.monolith.warehouses.domain.validators.CapacityValidator;
import com.fulfilment.application.monolith.warehouses.domain.validators.LocationValidator;
import com.fulfilment.application.monolith.warehouses.domain.validators.WarehouseExistenceValidator;
import jakarta.enterprise.context.ApplicationScoped;
import org.jboss.logging.Logger;

@ApplicationScoped
public class ReplaceWarehouseUseCase implements ReplaceWarehouseOperation {

  private static final Logger LOG = Logger.getLogger(ReplaceWarehouseUseCase.class);

  private final WarehouseStore warehouseStore;
  private final WarehouseExistenceValidator warehouseExistenceValidator;
  private final LocationValidator locationValidator;
  private final CapacityValidator capacityValidator;

  public ReplaceWarehouseUseCase(
      WarehouseStore warehouseStore,
      WarehouseExistenceValidator warehouseExistenceValidator,
      LocationValidator locationValidator,
      CapacityValidator capacityValidator) {
    this.warehouseStore = warehouseStore;
    this.warehouseExistenceValidator = warehouseExistenceValidator;
    this.locationValidator = locationValidator;
    this.capacityValidator = capacityValidator;
  }

  @Override
  public void replace(WarehouseDTO newWarehouseDTO) {
    LOG.infof("Replacing warehouse %s at location %s", newWarehouseDTO.businessUnitCode, newWarehouseDTO.location);

    WarehouseDTO existingWarehouse = warehouseStore.findByBusinessUnitCode(newWarehouseDTO.businessUnitCode);
    LocationDTO newLocation = locationValidator.validateAndResolveLocation(newWarehouseDTO.location);

    validateWarehouseReplacement(newWarehouseDTO, existingWarehouse, newLocation);
    archiveExistingWarehouse(existingWarehouse);
    createReplacementWarehouse(newWarehouseDTO);

    LOG.infof("Warehouse %s replaced successfully", newWarehouseDTO.businessUnitCode);
  }

  private void validateWarehouseReplacement(WarehouseDTO newWarehouse, WarehouseDTO existingWarehouse, LocationDTO newLocation) {
    LOG.debugf("Validating warehouse replacement for %s", newWarehouse.businessUnitCode);

    warehouseExistenceValidator.validateWarehouseExists(existingWarehouse, newWarehouse.businessUnitCode);
    warehouseExistenceValidator.validateCannotReplaceArchivedWarehouse(existingWarehouse);

    capacityValidator.validateStockMatches(newWarehouse.stock, existingWarehouse.stock);
    capacityValidator.validateStockDoesNotExceedCapacity(newWarehouse.stock, newWarehouse.capacity);

    int currentTotalCapacity = warehouseStore.getTotalCapacityByLocation(newWarehouse.location);
    capacityValidator.validateLocationCapacityForReplacement(currentTotalCapacity, newWarehouse, existingWarehouse, newLocation);

    if (!newWarehouse.location.equals(existingWarehouse.location)) {
      LOG.debugf("Location change detected: %s -> %s", existingWarehouse.location, newWarehouse.location);
      int activeWarehousesCount = warehouseStore.findActiveByLocation(newWarehouse.location).size();
      capacityValidator.validateMaxWarehousesAtLocation(activeWarehousesCount, newLocation, newWarehouse.location);
    }
  }

  private void archiveExistingWarehouse(WarehouseDTO existingWarehouse) {
    LOG.debugf("Archiving existing warehouse %s", existingWarehouse.businessUnitCode);
    existingWarehouse.archivedAt = WarehouseTimestampUtil.getCurrentTimestamp();
    warehouseStore.update(existingWarehouse);
  }

  private void createReplacementWarehouse(WarehouseDTO newWarehouse) {
    LOG.debugf("Creating replacement warehouse %s", newWarehouse.businessUnitCode);
    newWarehouse.creationAt = WarehouseTimestampUtil.getCurrentTimestamp();
    newWarehouse.archivedAt = null;
    warehouseStore.create(newWarehouse);
  }
}
