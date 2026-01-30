package com.fulfilment.application.monolith.warehouses.adapters.database;

import com.fulfilment.application.monolith.warehouses.domain.models.WarehouseDTO;
import com.fulfilment.application.monolith.warehouses.domain.ports.WarehouseStore;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import org.jboss.logging.Logger;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.stream.Collectors;

@ApplicationScoped
public class WarehouseRepository implements WarehouseStore, PanacheRepository<DbWarehouse> {

  private static final Logger LOG = Logger.getLogger(WarehouseRepository.class);

  @Override
  public void create(WarehouseDTO warehouseDTO) {
    LOG.debugf("Persisting new warehouse: %s", warehouseDTO.businessUnitCode);
    DbWarehouse dbWarehouse = new DbWarehouse();
    dbWarehouse.businessUnitCode = warehouseDTO.businessUnitCode;
    dbWarehouse.location = warehouseDTO.location;
    dbWarehouse.capacity = warehouseDTO.capacity;
    dbWarehouse.stock = warehouseDTO.stock;
    dbWarehouse.createdAt = toLocalDateTime(warehouseDTO.creationAt);
    dbWarehouse.archivedAt = toLocalDateTime(warehouseDTO.archivedAt);
    persist(dbWarehouse);
  }

  @Override
  public void update(WarehouseDTO warehouseDTO) {
    LOG.debugf("Updating warehouse: %s", warehouseDTO.businessUnitCode);
    // Find the active (non-archived) warehouse with this business unit code
    DbWarehouse dbWarehouse = find("businessUnitCode = ?1 and archivedAt is null", warehouseDTO.businessUnitCode).firstResult();

    if (dbWarehouse != null) {
      dbWarehouse.location = warehouseDTO.location;
      dbWarehouse.capacity = warehouseDTO.capacity;
      dbWarehouse.stock = warehouseDTO.stock;
      dbWarehouse.archivedAt = toLocalDateTime(warehouseDTO.archivedAt);
      persist(dbWarehouse);
    }
  }

  @Override
  public void remove(WarehouseDTO warehouseDTO) {
    LOG.debugf("Removing warehouse: %s", warehouseDTO.businessUnitCode);
    delete("businessUnitCode = ?1 and archivedAt is null", warehouseDTO.businessUnitCode);
  }

  @Override
  public WarehouseDTO findByBusinessUnitCode(String buCode) {
    // Find only the active (non-archived) warehouse
    DbWarehouse dbWarehouse = find("businessUnitCode = ?1 and archivedAt is null", buCode).firstResult();
    return dbWarehouse != null ? toDomain(dbWarehouse) : null;
  }

  @Override
  public List<WarehouseDTO> findActiveByLocation(String location) {
    return find("location = ?1 and archivedAt is null", location)
            .stream()
            .map(this::toDomain)
            .collect(Collectors.toList());
  }

  public List<WarehouseDTO> findAllActiveWarehouses() {
    return find("archivedAt is null")
            .stream()
            .map(this::toDomain)
            .collect(Collectors.toList());
  }

  @Override
  public int getTotalCapacityByLocation(String location) {
    return find("location = ?1 and archivedAt is null", location)
            .stream()
            .mapToInt(w -> w.capacity != null ? w.capacity : 0)
            .sum();
  }

  private WarehouseDTO toDomain(DbWarehouse dbWarehouse) {
    WarehouseDTO warehouseDTO = new WarehouseDTO();
    warehouseDTO.businessUnitCode = dbWarehouse.businessUnitCode;
    warehouseDTO.location = dbWarehouse.location;
    warehouseDTO.capacity = dbWarehouse.capacity;
    warehouseDTO.stock = dbWarehouse.stock;
    warehouseDTO.creationAt = toZonedDateTime(dbWarehouse.createdAt);
    warehouseDTO.archivedAt = toZonedDateTime(dbWarehouse.archivedAt);
    return warehouseDTO;
  }

  private LocalDateTime toLocalDateTime(ZonedDateTime zonedDateTime) {
    return zonedDateTime != null ? zonedDateTime.toLocalDateTime() : null;
  }

  private ZonedDateTime toZonedDateTime(LocalDateTime localDateTime) {
    return localDateTime != null ? localDateTime.atZone(ZoneId.systemDefault()) : null;
  }
}
