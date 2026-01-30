package com.fulfilment.application.monolith.warehouses.domain.usecases;

import com.fulfilment.application.monolith.warehouses.domain.models.LocationDTO;
import com.fulfilment.application.monolith.warehouses.domain.models.WarehouseDTO;
import com.fulfilment.application.monolith.warehouses.domain.ports.CreateWarehouseOperation;
import com.fulfilment.application.monolith.warehouses.domain.ports.WarehouseStore;
import com.fulfilment.application.monolith.warehouses.domain.utils.WarehouseTimestampUtil;
import com.fulfilment.application.monolith.warehouses.domain.validators.CapacityValidator;
import com.fulfilment.application.monolith.warehouses.domain.validators.LocationValidator;
import com.fulfilment.application.monolith.warehouses.domain.validators.WarehouseExistenceValidator;
import jakarta.enterprise.context.ApplicationScoped;
import org.jboss.logging.Logger;

@ApplicationScoped
public class CreateWarehouseUseCase implements CreateWarehouseOperation {

  private static final Logger LOG = Logger.getLogger(CreateWarehouseUseCase.class);

  private final WarehouseStore warehouseStore;
  private final WarehouseExistenceValidator warehouseExistenceValidator;
  private final LocationValidator locationValidator;
  private final CapacityValidator capacityValidator;

  public CreateWarehouseUseCase(
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
  public void create(WarehouseDTO warehouseDTO) {
    LOG.infof("Creating warehouse with business unit code: %s at location: %s", warehouseDTO.businessUnitCode, warehouseDTO.location);
    validateWarehouseCreation(warehouseDTO);
    persistNewWarehouse(warehouseDTO);
    LOG.infof("Warehouse %s created successfully", warehouseDTO.businessUnitCode);
  }

  private void validateWarehouseCreation(WarehouseDTO warehouseDTO) {
    LOG.debugf("Validating warehouse creation for %s", warehouseDTO.businessUnitCode);

    WarehouseDTO existingWarehouse = warehouseStore.findByBusinessUnitCode(warehouseDTO.businessUnitCode);
    warehouseExistenceValidator.validateWarehouseDoesNotExist(existingWarehouse, warehouseDTO.businessUnitCode);

    LocationDTO location = locationValidator.validateAndResolveLocation(warehouseDTO.location);

    int activeWarehousesCount = warehouseStore.findActiveByLocation(warehouseDTO.location).size();
    capacityValidator.validateMaxWarehousesAtLocation(activeWarehousesCount, location, warehouseDTO.location);

    int currentTotalCapacity = warehouseStore.getTotalCapacityByLocation(warehouseDTO.location);
    capacityValidator.validateLocationCapacity(currentTotalCapacity, warehouseDTO.capacity, location, warehouseDTO.location);

    capacityValidator.validateStockDoesNotExceedCapacity(warehouseDTO.stock, warehouseDTO.capacity);
  }

  private void persistNewWarehouse(WarehouseDTO warehouseDTO) {
    warehouseDTO.creationAt = WarehouseTimestampUtil.getCurrentTimestamp();
    warehouseStore.create(warehouseDTO);
  }
}
