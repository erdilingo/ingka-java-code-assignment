package com.fulfilment.application.monolith.warehouses.domain.ports;

import com.fulfilment.application.monolith.warehouses.domain.models.WarehouseDTO;

import java.util.List;

public interface WarehouseStore {
  void create(WarehouseDTO warehouseDTO);

  void update(WarehouseDTO warehouseDTO);

  void remove(WarehouseDTO warehouseDTO);

  WarehouseDTO findByBusinessUnitCode(String buCode);

  List<WarehouseDTO> findActiveByLocation(String location);

  List<WarehouseDTO> findAllActiveWarehouses();

  int getTotalCapacityByLocation(String location);
}
