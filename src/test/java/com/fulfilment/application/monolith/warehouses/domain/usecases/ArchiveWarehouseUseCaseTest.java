package com.fulfilment.application.monolith.warehouses.domain.usecases;

import com.fulfilment.application.monolith.warehouses.domain.exceptions.WarehouseNotFoundException;
import com.fulfilment.application.monolith.warehouses.domain.exceptions.WarehouseValidationException;
import com.fulfilment.application.monolith.warehouses.domain.models.WarehouseDTO;
import com.fulfilment.application.monolith.warehouses.domain.ports.WarehouseStore;
import com.fulfilment.application.monolith.warehouses.domain.validators.WarehouseExistenceValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.ZonedDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class ArchiveWarehouseUseCaseTest {

  @Mock
  private WarehouseStore warehouseStore;

  @Mock
  private WarehouseExistenceValidator warehouseExistenceValidator;

  private ArchiveWarehouseUseCase archiveWarehouseUseCase;

  @BeforeEach
  public void setUp() {
    MockitoAnnotations.openMocks(this);
    archiveWarehouseUseCase = new ArchiveWarehouseUseCase(
        warehouseStore,
        warehouseExistenceValidator
    );
  }

  @Test
  public void testArchiveWarehouseSuccess() {
    // given
    WarehouseDTO existingWarehouseDTO = new WarehouseDTO();
    existingWarehouseDTO.businessUnitCode = "WH001";
    existingWarehouseDTO.location = "ZWOLLE-001";
    existingWarehouseDTO.archivedAt = null;

    WarehouseDTO warehouseDTOToArchive = new WarehouseDTO();
    warehouseDTOToArchive.businessUnitCode = "WH001";

    when(warehouseStore.findByBusinessUnitCode("WH001")).thenReturn(existingWarehouseDTO);

    // when
    archiveWarehouseUseCase.archive(warehouseDTOToArchive);

    // then
    verify(warehouseExistenceValidator).validateWarehouseExists(existingWarehouseDTO, "WH001");
    verify(warehouseExistenceValidator).validateWarehouseNotArchived(existingWarehouseDTO);
    verify(warehouseStore, times(1)).update(warehouseDTOToArchive);
    assertNotNull(warehouseDTOToArchive.archivedAt);
  }

  @Test
  public void testArchiveWarehouseFailsWhenWarehouseNotFound() {
    // given
    WarehouseDTO warehouseDTO = new WarehouseDTO();
    warehouseDTO.businessUnitCode = "WH999";

    when(warehouseStore.findByBusinessUnitCode("WH999")).thenReturn(null);
    doThrow(new WarehouseNotFoundException("does not exist"))
        .when(warehouseExistenceValidator).validateWarehouseExists(null, "WH999");

    // when & then
    WarehouseNotFoundException exception = assertThrows(WarehouseNotFoundException.class, () -> {
      archiveWarehouseUseCase.archive(warehouseDTO);
    });
    assertTrue(exception.getMessage().contains("does not exist"));
    verify(warehouseStore, never()).update(any());
  }

  @Test
  public void testArchiveWarehouseFailsWhenAlreadyArchived() {
    // given
    WarehouseDTO existingWarehouseDTO = new WarehouseDTO();
    existingWarehouseDTO.businessUnitCode = "WH001";
    existingWarehouseDTO.archivedAt = ZonedDateTime.now();

    WarehouseDTO warehouseDTOToArchive = new WarehouseDTO();
    warehouseDTOToArchive.businessUnitCode = "WH001";

    when(warehouseStore.findByBusinessUnitCode("WH001")).thenReturn(existingWarehouseDTO);
    doThrow(new WarehouseValidationException("already archived"))
        .when(warehouseExistenceValidator).validateWarehouseNotArchived(existingWarehouseDTO);

    // when & then
    WarehouseValidationException exception = assertThrows(WarehouseValidationException.class, () -> {
      archiveWarehouseUseCase.archive(warehouseDTOToArchive);
    });
    assertTrue(exception.getMessage().contains("already archived"));
    verify(warehouseStore, never()).update(any());
  }
}