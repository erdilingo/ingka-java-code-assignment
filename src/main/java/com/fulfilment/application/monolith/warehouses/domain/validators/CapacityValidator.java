package com.fulfilment.application.monolith.warehouses.domain.validators;

import com.fulfilment.application.monolith.warehouses.domain.exceptions.WarehouseValidationException;
import com.fulfilment.application.monolith.warehouses.domain.models.LocationDTO;
import com.fulfilment.application.monolith.warehouses.domain.models.WarehouseDTO;
import jakarta.enterprise.context.ApplicationScoped;
import org.jboss.logging.Logger;

@ApplicationScoped
public class CapacityValidator {

  private static final Logger LOG = Logger.getLogger(CapacityValidator.class);

  public void validateStockDoesNotExceedCapacity(Integer stock, Integer capacity) {
    if (stock > capacity) {
      LOG.errorf("Validation failed: stock %d exceeds capacity %d", stock, capacity);
      throw new WarehouseValidationException("Warehouse stock cannot exceed warehouse capacity");
    }
  }

  public void validateLocationCapacity(int currentTotalCapacity, Integer newCapacity, LocationDTO locationDTO, String location) {
    if (currentTotalCapacity + newCapacity > locationDTO.maxCapacity) {
      LOG.errorf("Validation failed: total capacity %d + %d exceeds max %d for location %s",
          currentTotalCapacity, newCapacity, locationDTO.maxCapacity, location);
      throw new WarehouseValidationException("Warehouse capacity exceeds maximum capacity for location " + location);
    }
  }

  public void validateLocationCapacityForReplacement(
      int currentTotalCapacity,
      WarehouseDTO newWarehouse,
      WarehouseDTO existingWarehouse,
      LocationDTO newLocation) {

    if (newWarehouse.location.equals(existingWarehouse.location)) {
      int capacityAfterReplacement = currentTotalCapacity - existingWarehouse.capacity + newWarehouse.capacity;
      if (capacityAfterReplacement > newLocation.maxCapacity) {
        LOG.errorf("Validation failed: replacement capacity %d exceeds max %d for location %s",
            capacityAfterReplacement, newLocation.maxCapacity, newWarehouse.location);
        throw new WarehouseValidationException("Warehouse capacity exceeds maximum capacity for location " + newWarehouse.location);
      }
    } else {
      if (currentTotalCapacity + newWarehouse.capacity > newLocation.maxCapacity) {
        LOG.errorf("Validation failed: total capacity %d + %d exceeds max %d for new location %s",
            currentTotalCapacity, newWarehouse.capacity, newLocation.maxCapacity, newWarehouse.location);
        throw new WarehouseValidationException("Warehouse capacity exceeds maximum capacity for location " + newWarehouse.location);
      }
    }
  }

  public void validateMaxWarehousesAtLocation(int activeWarehousesCount, LocationDTO locationDTO, String location) {
    if (activeWarehousesCount >= locationDTO.maxNumberOfWarehouses) {
      LOG.errorf("Validation failed: %d active warehouses reached max %d for location %s",
          activeWarehousesCount, locationDTO.maxNumberOfWarehouses, location);
      throw new WarehouseValidationException("Maximum number of warehouses reached for location " + location);
    }
  }

  public void validateStockMatches(Integer newStock, Integer existingStock) {
    if (!newStock.equals(existingStock)) {
      LOG.errorf("Validation failed: new stock %d does not match existing stock %d", newStock, existingStock);
      throw new WarehouseValidationException("New warehouse stock must match the existing warehouse stock");
    }
  }
}
