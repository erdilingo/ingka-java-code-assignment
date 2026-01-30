package com.fulfilment.application.monolith.location;

import com.fulfilment.application.monolith.warehouses.domain.models.LocationDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class LocationDTOGatewayTest {

  private LocationGateway locationGateway;

  @BeforeEach
  public void setUp() {
    locationGateway = new LocationGateway();
  }

  @Test
  public void testWhenResolveExistingLocationShouldReturn() {
    // when
    LocationDTO locationDTO = locationGateway.resolveByIdentifier("ZWOLLE-001");

    // then
    assertNotNull(locationDTO);
    assertEquals("ZWOLLE-001", locationDTO.identification);
    assertEquals(1, locationDTO.maxNumberOfWarehouses);
    assertEquals(40, locationDTO.maxCapacity);
  }

  @Test
  public void testWhenResolveNonExistingLocationShouldReturnNull() {
    // when
    LocationDTO locationDTO = locationGateway.resolveByIdentifier("NON-EXISTENT");

    // then
    assertNull(locationDTO);
  }
}
