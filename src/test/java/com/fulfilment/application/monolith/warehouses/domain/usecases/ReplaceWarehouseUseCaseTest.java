package com.fulfilment.application.monolith.warehouses.domain.usecases;

import com.fulfilment.application.monolith.warehouses.domain.exceptions.WarehouseNotFoundException;
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

import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

public class ReplaceWarehouseUseCaseTest {

  @Mock
  private WarehouseStore warehouseStore;

  @Mock
  private WarehouseExistenceValidator warehouseExistenceValidator;

  @Mock
  private LocationValidator locationValidator;

  @Mock
  private CapacityValidator capacityValidator;

  private ReplaceWarehouseUseCase replaceWarehouseUseCase;

  @BeforeEach
  public void setUp() {
    MockitoAnnotations.openMocks(this);
    replaceWarehouseUseCase = new ReplaceWarehouseUseCase(
        warehouseStore, warehouseExistenceValidator, locationValidator, capacityValidator
    );
  }

  @Test
  public void testReplaceWarehouseSameLocationSuccess() {
    // given
    WarehouseDTO existingWarehouse = new WarehouseDTO();
    existingWarehouse.businessUnitCode = "WH001";
    existingWarehouse.location = "ZWOLLE-001";
    existingWarehouse.capacity = 30;
    existingWarehouse.stock = 10;
    existingWarehouse.archivedAt = null;

    WarehouseDTO newWarehouse = new WarehouseDTO();
    newWarehouse.businessUnitCode = "WH001";
    newWarehouse.location = "ZWOLLE-001";
    newWarehouse.capacity = 35;
    newWarehouse.stock = 10;

    LocationDTO locationDTO = new LocationDTO("ZWOLLE-001", 1, 40);

    when(warehouseStore.findByBusinessUnitCode("WH001")).thenReturn(existingWarehouse);
    when(locationValidator.validateAndResolveLocation("ZWOLLE-001")).thenReturn(locationDTO);
    when(warehouseStore.getTotalCapacityByLocation("ZWOLLE-001")).thenReturn(30);

    // when
    replaceWarehouseUseCase.replace(newWarehouse);

    // then
    verify(warehouseExistenceValidator).validateWarehouseExists(existingWarehouse, "WH001");
    verify(warehouseExistenceValidator).validateCannotReplaceArchivedWarehouse(existingWarehouse);
    verify(capacityValidator).validateStockMatches(10, 10);
    verify(capacityValidator).validateStockDoesNotExceedCapacity(10, 35);
    verify(capacityValidator).validateLocationCapacityForReplacement(30, newWarehouse, existingWarehouse, locationDTO);
    // Same location - should NOT validate max warehouses
    verify(capacityValidator, never()).validateMaxWarehousesAtLocation(anyInt(), any(), anyString());
    verify(warehouseStore).update(existingWarehouse);
    assertNotNull(existingWarehouse.archivedAt);
    verify(warehouseStore).create(newWarehouse);
    assertNotNull(newWarehouse.creationAt);
    assertNull(newWarehouse.archivedAt);
  }

  @Test
  public void testReplaceWarehouseDifferentLocationSuccess() {
    // given
    WarehouseDTO existingWarehouse = new WarehouseDTO();
    existingWarehouse.businessUnitCode = "WH001";
    existingWarehouse.location = "ZWOLLE-001";
    existingWarehouse.capacity = 30;
    existingWarehouse.stock = 10;
    existingWarehouse.archivedAt = null;

    WarehouseDTO newWarehouse = new WarehouseDTO();
    newWarehouse.businessUnitCode = "WH001";
    newWarehouse.location = "AMSTERDAM-001";
    newWarehouse.capacity = 40;
    newWarehouse.stock = 10;

    LocationDTO newLocation = new LocationDTO("AMSTERDAM-001", 5, 100);

    when(warehouseStore.findByBusinessUnitCode("WH001")).thenReturn(existingWarehouse);
    when(locationValidator.validateAndResolveLocation("AMSTERDAM-001")).thenReturn(newLocation);
    when(warehouseStore.getTotalCapacityByLocation("AMSTERDAM-001")).thenReturn(0);
    when(warehouseStore.findActiveByLocation("AMSTERDAM-001")).thenReturn(Collections.emptyList());

    // when
    replaceWarehouseUseCase.replace(newWarehouse);

    // then
    verify(capacityValidator).validateMaxWarehousesAtLocation(0, newLocation, "AMSTERDAM-001");
    verify(warehouseStore).update(existingWarehouse);
    verify(warehouseStore).create(newWarehouse);
  }

  @Test
  public void testReplaceWarehouseFailsWhenNotFound() {
    // given
    WarehouseDTO newWarehouse = new WarehouseDTO();
    newWarehouse.businessUnitCode = "WH999";
    newWarehouse.location = "ZWOLLE-001";

    LocationDTO locationDTO = new LocationDTO("ZWOLLE-001", 1, 40);

    when(warehouseStore.findByBusinessUnitCode("WH999")).thenReturn(null);
    when(locationValidator.validateAndResolveLocation("ZWOLLE-001")).thenReturn(locationDTO);
    doThrow(new WarehouseNotFoundException("does not exist"))
        .when(warehouseExistenceValidator).validateWarehouseExists(null, "WH999");

    // when & then
    assertThrows(WarehouseNotFoundException.class, () -> {
      replaceWarehouseUseCase.replace(newWarehouse);
    });
    verify(warehouseStore, never()).update(any());
    verify(warehouseStore, never()).create(any());
  }

  @Test
  public void testReplaceWarehouseFailsWhenArchived() {
    // given
    WarehouseDTO existingWarehouse = new WarehouseDTO();
    existingWarehouse.businessUnitCode = "WH001";
    existingWarehouse.location = "ZWOLLE-001";
    existingWarehouse.archivedAt = ZonedDateTime.now();

    WarehouseDTO newWarehouse = new WarehouseDTO();
    newWarehouse.businessUnitCode = "WH001";
    newWarehouse.location = "ZWOLLE-001";

    LocationDTO locationDTO = new LocationDTO("ZWOLLE-001", 1, 40);

    when(warehouseStore.findByBusinessUnitCode("WH001")).thenReturn(existingWarehouse);
    when(locationValidator.validateAndResolveLocation("ZWOLLE-001")).thenReturn(locationDTO);
    doThrow(new WarehouseValidationException("Cannot replace an already archived warehouse"))
        .when(warehouseExistenceValidator).validateCannotReplaceArchivedWarehouse(existingWarehouse);

    // when & then
    assertThrows(WarehouseValidationException.class, () -> {
      replaceWarehouseUseCase.replace(newWarehouse);
    });
    verify(warehouseStore, never()).update(any());
    verify(warehouseStore, never()).create(any());
  }

  @Test
  public void testReplaceWarehouseFailsWhenStockMismatch() {
    // given
    WarehouseDTO existingWarehouse = new WarehouseDTO();
    existingWarehouse.businessUnitCode = "WH001";
    existingWarehouse.location = "ZWOLLE-001";
    existingWarehouse.capacity = 30;
    existingWarehouse.stock = 10;
    existingWarehouse.archivedAt = null;

    WarehouseDTO newWarehouse = new WarehouseDTO();
    newWarehouse.businessUnitCode = "WH001";
    newWarehouse.location = "ZWOLLE-001";
    newWarehouse.capacity = 30;
    newWarehouse.stock = 20;

    LocationDTO locationDTO = new LocationDTO("ZWOLLE-001", 1, 40);

    when(warehouseStore.findByBusinessUnitCode("WH001")).thenReturn(existingWarehouse);
    when(locationValidator.validateAndResolveLocation("ZWOLLE-001")).thenReturn(locationDTO);
    doThrow(new WarehouseValidationException("New warehouse stock must match"))
        .when(capacityValidator).validateStockMatches(20, 10);

    // when & then
    assertThrows(WarehouseValidationException.class, () -> {
      replaceWarehouseUseCase.replace(newWarehouse);
    });
    verify(warehouseStore, never()).update(any());
    verify(warehouseStore, never()).create(any());
  }

  @Test
  public void testReplaceWarehouseFailsWhenInvalidLocation() {
    // given
    WarehouseDTO newWarehouse = new WarehouseDTO();
    newWarehouse.businessUnitCode = "WH001";
    newWarehouse.location = "INVALID";

    when(warehouseStore.findByBusinessUnitCode("WH001")).thenReturn(new WarehouseDTO());
    when(locationValidator.validateAndResolveLocation("INVALID"))
        .thenThrow(new WarehouseValidationException("Location INVALID does not exist"));

    // when & then
    assertThrows(WarehouseValidationException.class, () -> {
      replaceWarehouseUseCase.replace(newWarehouse);
    });
    verify(warehouseStore, never()).update(any());
    verify(warehouseStore, never()).create(any());
  }

  @Test
  public void testReplaceWarehouseFailsWhenStockExceedsNewCapacity() {
    // given
    WarehouseDTO existingWarehouse = new WarehouseDTO();
    existingWarehouse.businessUnitCode = "WH001";
    existingWarehouse.location = "ZWOLLE-001";
    existingWarehouse.capacity = 30;
    existingWarehouse.stock = 25;
    existingWarehouse.archivedAt = null;

    WarehouseDTO newWarehouse = new WarehouseDTO();
    newWarehouse.businessUnitCode = "WH001";
    newWarehouse.location = "ZWOLLE-001";
    newWarehouse.capacity = 20;
    newWarehouse.stock = 25;

    LocationDTO locationDTO = new LocationDTO("ZWOLLE-001", 1, 40);

    when(warehouseStore.findByBusinessUnitCode("WH001")).thenReturn(existingWarehouse);
    when(locationValidator.validateAndResolveLocation("ZWOLLE-001")).thenReturn(locationDTO);
    doThrow(new WarehouseValidationException("Warehouse stock cannot exceed warehouse capacity"))
        .when(capacityValidator).validateStockDoesNotExceedCapacity(25, 20);

    // when & then
    assertThrows(WarehouseValidationException.class, () -> {
      replaceWarehouseUseCase.replace(newWarehouse);
    });
    verify(warehouseStore, never()).update(any());
    verify(warehouseStore, never()).create(any());
  }

  @Test
  public void testReplaceWarehouseFailsWhenMaxWarehousesAtNewLocation() {
    // given
    WarehouseDTO existingWarehouse = new WarehouseDTO();
    existingWarehouse.businessUnitCode = "WH001";
    existingWarehouse.location = "ZWOLLE-001";
    existingWarehouse.capacity = 30;
    existingWarehouse.stock = 10;
    existingWarehouse.archivedAt = null;

    WarehouseDTO newWarehouse = new WarehouseDTO();
    newWarehouse.businessUnitCode = "WH001";
    newWarehouse.location = "TILBURG-001";
    newWarehouse.capacity = 30;
    newWarehouse.stock = 10;

    LocationDTO newLocation = new LocationDTO("TILBURG-001", 1, 40);

    when(warehouseStore.findByBusinessUnitCode("WH001")).thenReturn(existingWarehouse);
    when(locationValidator.validateAndResolveLocation("TILBURG-001")).thenReturn(newLocation);
    when(warehouseStore.getTotalCapacityByLocation("TILBURG-001")).thenReturn(0);
    when(warehouseStore.findActiveByLocation("TILBURG-001")).thenReturn(List.of(new WarehouseDTO()));
    doThrow(new WarehouseValidationException("Maximum number of warehouses reached"))
        .when(capacityValidator).validateMaxWarehousesAtLocation(1, newLocation, "TILBURG-001");

    // when & then
    assertThrows(WarehouseValidationException.class, () -> {
      replaceWarehouseUseCase.replace(newWarehouse);
    });
    verify(warehouseStore, never()).update(any());
    verify(warehouseStore, never()).create(any());
  }
}