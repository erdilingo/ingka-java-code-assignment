package com.fulfilment.application.monolith.warehouses.adapters.database;

import com.fulfilment.application.monolith.warehouses.domain.models.WarehouseDTO;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.ZonedDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
public class WarehouseRepositoryTest {

  @Inject
  WarehouseRepository warehouseRepository;

  @BeforeEach
  @Transactional
  public void cleanUp() {
    warehouseRepository.deleteAll();
  }

  @Test
  @Transactional
  public void testCreateAndFindByBusinessUnitCode() {
    WarehouseDTO dto = new WarehouseDTO();
    dto.businessUnitCode = "TEST-001";
    dto.location = "ZWOLLE-001";
    dto.capacity = 50;
    dto.stock = 10;
    dto.creationAt = ZonedDateTime.now();
    dto.archivedAt = null;

    warehouseRepository.create(dto);

    WarehouseDTO found = warehouseRepository.findByBusinessUnitCode("TEST-001");
    assertNotNull(found);
    assertEquals("TEST-001", found.businessUnitCode);
    assertEquals("ZWOLLE-001", found.location);
    assertEquals(50, found.capacity);
    assertEquals(10, found.stock);
    assertNotNull(found.creationAt);
    assertNull(found.archivedAt);
  }

  @Test
  @Transactional
  public void testFindByBusinessUnitCodeReturnsNullWhenNotFound() {
    WarehouseDTO found = warehouseRepository.findByBusinessUnitCode("NONEXISTENT");
    assertNull(found);
  }

  @Test
  @Transactional
  public void testFindByBusinessUnitCodeIgnoresArchivedWarehouses() {
    WarehouseDTO dto = new WarehouseDTO();
    dto.businessUnitCode = "ARCHIVED-001";
    dto.location = "ZWOLLE-001";
    dto.capacity = 30;
    dto.stock = 5;
    dto.creationAt = ZonedDateTime.now();
    dto.archivedAt = ZonedDateTime.now();

    warehouseRepository.create(dto);

    WarehouseDTO found = warehouseRepository.findByBusinessUnitCode("ARCHIVED-001");
    assertNull(found);
  }

  @Test
  @Transactional
  public void testUpdate() {
    WarehouseDTO dto = new WarehouseDTO();
    dto.businessUnitCode = "UPD-001";
    dto.location = "ZWOLLE-001";
    dto.capacity = 40;
    dto.stock = 10;
    dto.creationAt = ZonedDateTime.now();

    warehouseRepository.create(dto);

    WarehouseDTO updateDto = new WarehouseDTO();
    updateDto.businessUnitCode = "UPD-001";
    updateDto.location = "AMSTERDAM-001";
    updateDto.capacity = 60;
    updateDto.stock = 15;
    updateDto.archivedAt = ZonedDateTime.now();

    warehouseRepository.update(updateDto);

    // After archiving, findByBusinessUnitCode should return null (it filters archived)
    WarehouseDTO found = warehouseRepository.findByBusinessUnitCode("UPD-001");
    assertNull(found);
  }

  @Test
  @Transactional
  public void testUpdateNonExistentWarehouseDoesNothing() {
    WarehouseDTO updateDto = new WarehouseDTO();
    updateDto.businessUnitCode = "GHOST-001";
    updateDto.location = "ZWOLLE-001";
    updateDto.capacity = 30;
    updateDto.stock = 5;

    // Should not throw
    assertDoesNotThrow(() -> warehouseRepository.update(updateDto));
  }

  @Test
  @Transactional
  public void testRemove() {
    WarehouseDTO dto = new WarehouseDTO();
    dto.businessUnitCode = "DEL-001";
    dto.location = "ZWOLLE-001";
    dto.capacity = 20;
    dto.stock = 5;
    dto.creationAt = ZonedDateTime.now();

    warehouseRepository.create(dto);
    assertNotNull(warehouseRepository.findByBusinessUnitCode("DEL-001"));

    warehouseRepository.remove(dto);
    assertNull(warehouseRepository.findByBusinessUnitCode("DEL-001"));
  }

  @Test
  @Transactional
  public void testFindActiveByLocation() {
    WarehouseDTO active1 = new WarehouseDTO();
    active1.businessUnitCode = "ACT-001";
    active1.location = "TILBURG-001";
    active1.capacity = 20;
    active1.stock = 5;
    active1.creationAt = ZonedDateTime.now();

    WarehouseDTO active2 = new WarehouseDTO();
    active2.businessUnitCode = "ACT-002";
    active2.location = "TILBURG-001";
    active2.capacity = 10;
    active2.stock = 3;
    active2.creationAt = ZonedDateTime.now();

    WarehouseDTO archived = new WarehouseDTO();
    archived.businessUnitCode = "ACT-003";
    archived.location = "TILBURG-001";
    archived.capacity = 15;
    archived.stock = 2;
    archived.creationAt = ZonedDateTime.now();
    archived.archivedAt = ZonedDateTime.now();

    warehouseRepository.create(active1);
    warehouseRepository.create(active2);
    warehouseRepository.create(archived);

    List<WarehouseDTO> result = warehouseRepository.findActiveByLocation("TILBURG-001");
    assertEquals(2, result.size());
  }

  @Test
  @Transactional
  public void testFindActiveByLocationReturnsEmptyForUnknownLocation() {
    List<WarehouseDTO> result = warehouseRepository.findActiveByLocation("NOWHERE-001");
    assertTrue(result.isEmpty());
  }

  @Test
  @Transactional
  public void testFindAllActiveWarehouses() {
    WarehouseDTO active = new WarehouseDTO();
    active.businessUnitCode = "ALL-001";
    active.location = "ZWOLLE-001";
    active.capacity = 30;
    active.stock = 10;
    active.creationAt = ZonedDateTime.now();

    WarehouseDTO archived = new WarehouseDTO();
    archived.businessUnitCode = "ALL-002";
    archived.location = "ZWOLLE-001";
    archived.capacity = 20;
    archived.stock = 5;
    archived.creationAt = ZonedDateTime.now();
    archived.archivedAt = ZonedDateTime.now();

    warehouseRepository.create(active);
    warehouseRepository.create(archived);

    List<WarehouseDTO> result = warehouseRepository.findAllActiveWarehouses();
    assertEquals(1, result.size());
    assertEquals("ALL-001", result.get(0).businessUnitCode);
  }

  @Test
  @Transactional
  public void testGetTotalCapacityByLocation() {
    WarehouseDTO wh1 = new WarehouseDTO();
    wh1.businessUnitCode = "CAP-001";
    wh1.location = "AMSTERDAM-001";
    wh1.capacity = 40;
    wh1.stock = 10;
    wh1.creationAt = ZonedDateTime.now();

    WarehouseDTO wh2 = new WarehouseDTO();
    wh2.businessUnitCode = "CAP-002";
    wh2.location = "AMSTERDAM-001";
    wh2.capacity = 30;
    wh2.stock = 5;
    wh2.creationAt = ZonedDateTime.now();

    WarehouseDTO archived = new WarehouseDTO();
    archived.businessUnitCode = "CAP-003";
    archived.location = "AMSTERDAM-001";
    archived.capacity = 50;
    archived.stock = 0;
    archived.creationAt = ZonedDateTime.now();
    archived.archivedAt = ZonedDateTime.now();

    warehouseRepository.create(wh1);
    warehouseRepository.create(wh2);
    warehouseRepository.create(archived);

    int totalCapacity = warehouseRepository.getTotalCapacityByLocation("AMSTERDAM-001");
    assertEquals(70, totalCapacity);
  }

  @Test
  @Transactional
  public void testGetTotalCapacityByLocationReturnsZeroForEmptyLocation() {
    int totalCapacity = warehouseRepository.getTotalCapacityByLocation("EMPTY-LOC");
    assertEquals(0, totalCapacity);
  }
}