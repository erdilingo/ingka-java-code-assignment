package com.fulfilment.application.monolith.warehouses.domain.usecases;

import com.fulfilment.application.monolith.warehouses.domain.models.WarehouseDTO;
import com.fulfilment.application.monolith.warehouses.domain.ports.ArchiveWarehouseOperation;
import com.fulfilment.application.monolith.warehouses.domain.ports.WarehouseStore;
import com.fulfilment.application.monolith.warehouses.domain.utils.WarehouseTimestampUtil;
import com.fulfilment.application.monolith.warehouses.domain.validators.WarehouseExistenceValidator;
import jakarta.enterprise.context.ApplicationScoped;
import org.jboss.logging.Logger;

@ApplicationScoped
public class ArchiveWarehouseUseCase implements ArchiveWarehouseOperation {

  private static final Logger LOG = Logger.getLogger(ArchiveWarehouseUseCase.class);

  private final WarehouseStore warehouseStore;
  private final WarehouseExistenceValidator warehouseExistenceValidator;

  public ArchiveWarehouseUseCase(
      WarehouseStore warehouseStore,
      WarehouseExistenceValidator warehouseExistenceValidator) {
    this.warehouseStore = warehouseStore;
    this.warehouseExistenceValidator = warehouseExistenceValidator;
  }

  @Override
  public void archive(WarehouseDTO warehouseDTO) {
    LOG.infof("Archiving warehouse %s", warehouseDTO.businessUnitCode);

    WarehouseDTO existingWarehouse = warehouseStore.findByBusinessUnitCode(warehouseDTO.businessUnitCode);
    warehouseExistenceValidator.validateWarehouseExists(existingWarehouse, warehouseDTO.businessUnitCode);
    warehouseExistenceValidator.validateWarehouseNotArchived(existingWarehouse);

    warehouseDTO.archivedAt = WarehouseTimestampUtil.getCurrentTimestamp();
    warehouseStore.update(warehouseDTO);

    LOG.infof("Warehouse %s archived successfully", warehouseDTO.businessUnitCode);
  }
}
