package com.fulfilment.application.monolith.warehouses.domain.usecases;

import com.fulfilment.application.monolith.warehouses.domain.exceptions.WarehouseValidationException;
import com.fulfilment.application.monolith.warehouses.domain.models.LocationDTO;
import com.fulfilment.application.monolith.warehouses.domain.models.WarehouseDTO;
import com.fulfilment.application.monolith.warehouses.domain.ports.WarehouseStore;
import com.fulfilment.application.monolith.warehouses.domain.validators.CapacityValidator;
import com.fulfilment.application.monolith.warehouses.domain.validators.LocationValidator;
import com.fulfilment.application.monolith.warehouses.domain.validators.WarehouseExistenceValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

public class CreateWarehouseUseCaseTest {

  @Mock
  private WarehouseStore warehouseStore;

  @Mock
  private WarehouseExistenceValidator warehouseExistenceValidator;

  @Mock
  private LocationValidator locationValidator;

  @Mock
  private CapacityValidator capacityValidator;

  private CreateWarehouseUseCase createWarehouseUseCase;

  @BeforeEach
  public void setUp() {
    MockitoAnnotations.openMocks(this);
    createWarehouseUseCase = new CreateWarehouseUseCase(
        warehouseStore, warehouseExistenceValidator, locationValidator, capacityValidator
    );
  }

  @Test
  public void testCreateWarehouseSuccess() {
    // given
    WarehouseDTO warehouseDTO = new WarehouseDTO();
    warehouseDTO.businessUnitCode = "WH001";
    warehouseDTO.location = "ZWOLLE-001";
    warehouseDTO.capacity = 30;
    warehouseDTO.stock = 10;

    LocationDTO locationDTO = new LocationDTO("ZWOLLE-001", 1, 40);

    when(warehouseStore.findByBusinessUnitCode("WH001")).thenReturn(null);
    when(locationValidator.validateAndResolveLocation("ZWOLLE-001")).thenReturn(locationDTO);
    when(warehouseStore.findActiveByLocation("ZWOLLE-001")).thenReturn(Collections.emptyList());
    when(warehouseStore.getTotalCapacityByLocation("ZWOLLE-001")).thenReturn(0);

    // when
    createWarehouseUseCase.create(warehouseDTO);

    // then
    verify(warehouseExistenceValidator).validateWarehouseDoesNotExist(null, "WH001");
    verify(locationValidator).validateAndResolveLocation("ZWOLLE-001");
    verify(capacityValidator).validateMaxWarehousesAtLocation(0, locationDTO, "ZWOLLE-001");
    verify(capacityValidator).validateLocationCapacity(0, 30, locationDTO, "ZWOLLE-001");
    verify(capacityValidator).validateStockDoesNotExceedCapacity(10, 30);
    verify(warehouseStore).create(warehouseDTO);
    assertNotNull(warehouseDTO.creationAt);
  }

  @Test
  public void testCreateWarehouseFailsWhenAlreadyExists() {
    // given
    WarehouseDTO warehouseDTO = new WarehouseDTO();
    warehouseDTO.businessUnitCode = "WH001";
    warehouseDTO.location = "ZWOLLE-001";

    WarehouseDTO existingWarehouse = new WarehouseDTO();
    existingWarehouse.businessUnitCode = "WH001";

    when(warehouseStore.findByBusinessUnitCode("WH001")).thenReturn(existingWarehouse);
    doThrow(new WarehouseValidationException("already exists"))
        .when(warehouseExistenceValidator).validateWarehouseDoesNotExist(existingWarehouse, "WH001");

    // when & then
    WarehouseValidationException exception = assertThrows(WarehouseValidationException.class, () -> {
      createWarehouseUseCase.create(warehouseDTO);
    });
    assertTrue(exception.getMessage().contains("already exists"));
    verify(warehouseStore, never()).create(any());
  }

  @Test
  public void testCreateWarehouseFailsWhenLocationInvalid() {
    // given
    WarehouseDTO warehouseDTO = new WarehouseDTO();
    warehouseDTO.businessUnitCode = "WH001";
    warehouseDTO.location = "INVALID-LOC";

    when(warehouseStore.findByBusinessUnitCode("WH001")).thenReturn(null);
    when(locationValidator.validateAndResolveLocation("INVALID-LOC"))
        .thenThrow(new WarehouseValidationException("Location INVALID-LOC does not exist"));

    // when & then
    WarehouseValidationException exception = assertThrows(WarehouseValidationException.class, () -> {
      createWarehouseUseCase.create(warehouseDTO);
    });
    assertTrue(exception.getMessage().contains("does not exist"));
    verify(warehouseStore, never()).create(any());
  }

  @Test
  public void testCreateWarehouseFailsWhenMaxWarehousesReached() {
    // given
    WarehouseDTO warehouseDTO = new WarehouseDTO();
    warehouseDTO.businessUnitCode = "WH002";
    warehouseDTO.location = "ZWOLLE-001";
    warehouseDTO.capacity = 10;
    warehouseDTO.stock = 5;

    LocationDTO locationDTO = new LocationDTO("ZWOLLE-001", 1, 40);

    when(warehouseStore.findByBusinessUnitCode("WH002")).thenReturn(null);
    when(locationValidator.validateAndResolveLocation("ZWOLLE-001")).thenReturn(locationDTO);
    when(warehouseStore.findActiveByLocation("ZWOLLE-001")).thenReturn(List.of(new WarehouseDTO()));
    doThrow(new WarehouseValidationException("Maximum number of warehouses reached"))
        .when(capacityValidator).validateMaxWarehousesAtLocation(1, locationDTO, "ZWOLLE-001");

    // when & then
    WarehouseValidationException exception = assertThrows(WarehouseValidationException.class, () -> {
      createWarehouseUseCase.create(warehouseDTO);
    });
    assertTrue(exception.getMessage().contains("Maximum number of warehouses reached"));
    verify(warehouseStore, never()).create(any());
  }

  @Test
  public void testCreateWarehouseFailsWhenCapacityExceedsLocationMax() {
    // given
    WarehouseDTO warehouseDTO = new WarehouseDTO();
    warehouseDTO.businessUnitCode = "WH002";
    warehouseDTO.location = "ZWOLLE-001";
    warehouseDTO.capacity = 50;
    warehouseDTO.stock = 5;

    LocationDTO locationDTO = new LocationDTO("ZWOLLE-001", 2, 40);

    when(warehouseStore.findByBusinessUnitCode("WH002")).thenReturn(null);
    when(locationValidator.validateAndResolveLocation("ZWOLLE-001")).thenReturn(locationDTO);
    when(warehouseStore.findActiveByLocation("ZWOLLE-001")).thenReturn(Collections.emptyList());
    when(warehouseStore.getTotalCapacityByLocation("ZWOLLE-001")).thenReturn(0);
    doThrow(new WarehouseValidationException("Warehouse capacity exceeds maximum capacity"))
        .when(capacityValidator).validateLocationCapacity(0, 50, locationDTO, "ZWOLLE-001");

    // when & then
    WarehouseValidationException exception = assertThrows(WarehouseValidationException.class, () -> {
      createWarehouseUseCase.create(warehouseDTO);
    });
    assertTrue(exception.getMessage().contains("capacity exceeds"));
    verify(warehouseStore, never()).create(any());
  }

  @Test
  public void testCreateWarehouseFailsWhenStockExceedsCapacity() {
    // given
    WarehouseDTO warehouseDTO = new WarehouseDTO();
    warehouseDTO.businessUnitCode = "WH002";
    warehouseDTO.location = "ZWOLLE-001";
    warehouseDTO.capacity = 10;
    warehouseDTO.stock = 20;

    LocationDTO locationDTO = new LocationDTO("ZWOLLE-001", 2, 40);

    when(warehouseStore.findByBusinessUnitCode("WH002")).thenReturn(null);
    when(locationValidator.validateAndResolveLocation("ZWOLLE-001")).thenReturn(locationDTO);
    when(warehouseStore.findActiveByLocation("ZWOLLE-001")).thenReturn(Collections.emptyList());
    when(warehouseStore.getTotalCapacityByLocation("ZWOLLE-001")).thenReturn(0);
    doThrow(new WarehouseValidationException("Warehouse stock cannot exceed warehouse capacity"))
        .when(capacityValidator).validateStockDoesNotExceedCapacity(20, 10);

    // when & then
    WarehouseValidationException exception = assertThrows(WarehouseValidationException.class, () -> {
      createWarehouseUseCase.create(warehouseDTO);
    });
    assertTrue(exception.getMessage().contains("stock cannot exceed"));
    verify(warehouseStore, never()).create(any());
  }
}