package com.fulfilment.application.monolith.warehouses.adapters.restapi;

import com.fulfilment.application.monolith.warehouses.domain.exceptions.WarehouseNotFoundException;
import com.fulfilment.application.monolith.warehouses.domain.models.WarehouseDTO;
import com.fulfilment.application.monolith.warehouses.domain.ports.ArchiveWarehouseOperation;
import com.fulfilment.application.monolith.warehouses.domain.ports.CreateWarehouseOperation;
import com.fulfilment.application.monolith.warehouses.domain.ports.ReplaceWarehouseOperation;
import com.fulfilment.application.monolith.warehouses.domain.ports.WarehouseStore;
import com.warehouse.api.beans.Warehouse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class WarehouseResourceImplTest {

  @Mock
  private CreateWarehouseOperation createWarehouseOperation;

  @Mock
  private ReplaceWarehouseOperation replaceWarehouseOperation;

  @Mock
  private ArchiveWarehouseOperation archiveWarehouseOperation;

  @Mock
  private WarehouseStore warehouseStore;

  private WarehouseResourceImpl warehouseResource;

  @BeforeEach
  public void setUp() {
    MockitoAnnotations.openMocks(this);
    warehouseResource = new WarehouseResourceImpl(
        createWarehouseOperation, replaceWarehouseOperation, archiveWarehouseOperation, warehouseStore
    );
  }

  @Test
  public void testListAllWarehousesUnits_empty() {
    when(warehouseStore.findAllActiveWarehouses()).thenReturn(Collections.emptyList());

    List<Warehouse> result = warehouseResource.listAllWarehousesUnits();

    assertTrue(result.isEmpty());
  }

  @Test
  public void testListAllWarehousesUnits_withData() {
    WarehouseDTO dto = new WarehouseDTO();
    dto.businessUnitCode = "WH001";
    dto.location = "ZWOLLE-001";
    dto.capacity = 30;
    dto.stock = 10;

    when(warehouseStore.findAllActiveWarehouses()).thenReturn(List.of(dto));

    List<Warehouse> result = warehouseResource.listAllWarehousesUnits();

    assertEquals(1, result.size());
    assertEquals("WH001", result.get(0).getId());
    assertEquals("ZWOLLE-001", result.get(0).getLocation());
    assertEquals(30, result.get(0).getCapacity());
    assertEquals(10, result.get(0).getStock());
  }

  @Test
  public void testCreateANewWarehouseUnit() {
    Warehouse input = new Warehouse();
    input.setId("WH001");
    input.setLocation("ZWOLLE-001");
    input.setCapacity(30);
    input.setStock(10);

    Warehouse result = warehouseResource.createANewWarehouseUnit(input);

    ArgumentCaptor<WarehouseDTO> captor = ArgumentCaptor.forClass(WarehouseDTO.class);
    verify(createWarehouseOperation).create(captor.capture());
    WarehouseDTO captured = captor.getValue();
    assertEquals("WH001", captured.businessUnitCode);
    assertEquals("ZWOLLE-001", captured.location);
    assertEquals(30, captured.capacity);
    assertEquals(10, captured.stock);

    assertEquals("WH001", result.getId());
    assertEquals("ZWOLLE-001", result.getLocation());
  }

  @Test
  public void testGetAWarehouseUnitByID_found() {
    WarehouseDTO dto = new WarehouseDTO();
    dto.businessUnitCode = "WH001";
    dto.location = "ZWOLLE-001";
    dto.capacity = 30;
    dto.stock = 10;

    when(warehouseStore.findByBusinessUnitCode("WH001")).thenReturn(dto);

    Warehouse result = warehouseResource.getAWarehouseUnitByID("WH001");

    assertNotNull(result);
    assertEquals("WH001", result.getId());
  }

  @Test
  public void testGetAWarehouseUnitByID_notFound() {
    when(warehouseStore.findByBusinessUnitCode("WH999")).thenReturn(null);

    assertThrows(WarehouseNotFoundException.class, () ->
        warehouseResource.getAWarehouseUnitByID("WH999")
    );
  }

  @Test
  public void testReplaceAWarehouseUnitByID() {
    Warehouse input = new Warehouse();
    input.setLocation("AMSTERDAM-001");
    input.setCapacity(50);
    input.setStock(10);

    Warehouse result = warehouseResource.replaceAWarehouseUnitByID("WH001", input);

    ArgumentCaptor<WarehouseDTO> captor = ArgumentCaptor.forClass(WarehouseDTO.class);
    verify(replaceWarehouseOperation).replace(captor.capture());
    WarehouseDTO captured = captor.getValue();
    assertEquals("WH001", captured.businessUnitCode);
    assertEquals("AMSTERDAM-001", captured.location);
    assertEquals(50, captured.capacity);
    assertEquals(10, captured.stock);

    assertEquals("WH001", result.getId());
  }

  @Test
  public void testArchiveAWarehouseUnitByID() {
    warehouseResource.archiveAWarehouseUnitByID("WH001");

    ArgumentCaptor<WarehouseDTO> captor = ArgumentCaptor.forClass(WarehouseDTO.class);
    verify(archiveWarehouseOperation).archive(captor.capture());
    assertEquals("WH001", captor.getValue().businessUnitCode);
  }
}