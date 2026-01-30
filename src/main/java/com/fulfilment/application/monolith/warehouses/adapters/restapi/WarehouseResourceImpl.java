package com.fulfilment.application.monolith.warehouses.adapters.restapi;

import com.fulfilment.application.monolith.warehouses.domain.exceptions.WarehouseNotFoundException;
import com.fulfilment.application.monolith.warehouses.domain.models.WarehouseDTO;
import com.fulfilment.application.monolith.warehouses.domain.ports.ArchiveWarehouseOperation;
import com.fulfilment.application.monolith.warehouses.domain.ports.CreateWarehouseOperation;
import com.fulfilment.application.monolith.warehouses.domain.ports.ReplaceWarehouseOperation;
import com.fulfilment.application.monolith.warehouses.domain.ports.WarehouseStore;
import com.warehouse.api.WarehouseResource;
import com.warehouse.api.beans.Warehouse;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import jakarta.validation.constraints.NotNull;
import org.jboss.logging.Logger;

import java.util.List;

@ApplicationScoped
public class WarehouseResourceImpl implements WarehouseResource {

  private static final Logger LOG = Logger.getLogger(WarehouseResourceImpl.class);

  private final CreateWarehouseOperation createWarehouseOperation;
  private final ReplaceWarehouseOperation replaceWarehouseOperation;
  private final ArchiveWarehouseOperation archiveWarehouseOperation;
  private final WarehouseStore warehouseStore;

  public WarehouseResourceImpl(
      CreateWarehouseOperation createWarehouseOperation,
      ReplaceWarehouseOperation replaceWarehouseOperation,
      ArchiveWarehouseOperation archiveWarehouseOperation,
      WarehouseStore warehouseStore) {
    this.createWarehouseOperation = createWarehouseOperation;
    this.replaceWarehouseOperation = replaceWarehouseOperation;
    this.archiveWarehouseOperation = archiveWarehouseOperation;
    this.warehouseStore = warehouseStore;
  }

  @Override
  public List<Warehouse> listAllWarehousesUnits() {
    LOG.debug("Listing all active warehouse units");
    return warehouseStore.findAllActiveWarehouses()
            .stream()
            .map(this::toResponse)
            .toList();
  }

  @Override
  @Transactional
  public Warehouse createANewWarehouseUnit(@NotNull Warehouse data) {
    LOG.infof("REST request to create warehouse: %s", data.getId());
    WarehouseDTO warehouseDTO = new WarehouseDTO();
    warehouseDTO.businessUnitCode = data.getId();
    warehouseDTO.location = data.getLocation();
    warehouseDTO.capacity = data.getCapacity();
    warehouseDTO.stock = data.getStock();

    createWarehouseOperation.create(warehouseDTO);

    return toResponse(warehouseDTO);
  }

  @Override
  public Warehouse getAWarehouseUnitByID(String id) {
    LOG.debugf("REST request to get warehouse: %s", id);
    WarehouseDTO domainWarehouseDTO = warehouseStore.findByBusinessUnitCode(id);
    if (domainWarehouseDTO == null) {
      throw new WarehouseNotFoundException("Warehouse with id " + id + " not found");
    }
    return toResponse(domainWarehouseDTO);
  }

  @Override
  @Transactional
  public Warehouse replaceAWarehouseUnitByID(String businessUnitCode, @NotNull Warehouse data) {
    LOG.infof("REST request to replace warehouse: %s", businessUnitCode);
    WarehouseDTO newWarehouseDTO = new WarehouseDTO();
    newWarehouseDTO.businessUnitCode = businessUnitCode;
    newWarehouseDTO.location = data.getLocation();
    newWarehouseDTO.capacity = data.getCapacity();
    newWarehouseDTO.stock = data.getStock();

    replaceWarehouseOperation.replace(newWarehouseDTO);

    return toResponse(newWarehouseDTO);
  }

  @Override
  @Transactional
  public void archiveAWarehouseUnitByID(String id) {
    LOG.infof("REST request to archive warehouse: %s", id);
    WarehouseDTO warehouseDTO = new WarehouseDTO();
    warehouseDTO.businessUnitCode = id;
    archiveWarehouseOperation.archive(warehouseDTO);
  }

  private Warehouse toResponse(WarehouseDTO domain) {
    Warehouse warehouse = new Warehouse();
    warehouse.setId(domain.businessUnitCode);
    warehouse.setLocation(domain.location);
    warehouse.setCapacity(domain.capacity);
    warehouse.setStock(domain.stock);
    return warehouse;
  }
}
