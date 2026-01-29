package com.fulfilment.application.monolith.location;

import com.fulfilment.application.monolith.warehouses.domain.models.Location;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class LocationGatewayTest {

  private LocationGateway locationGateway;

  @BeforeEach
  public void setUp() {
    locationGateway = new LocationGateway();
  }

  @Test
  public void testWhenResolveExistingLocationShouldReturn() {
    // when
    Location location = locationGateway.resolveByIdentifier("ZWOLLE-001");

    // then
    assertNotNull(location);
    assertEquals("ZWOLLE-001", location.identification);
    assertEquals(1, location.maxNumberOfWarehouses);
    assertEquals(40, location.maxCapacity);
  }

  @Test
  public void testWhenResolveNonExistingLocationShouldReturnNull() {
    // when
    Location location = locationGateway.resolveByIdentifier("NON-EXISTENT");

    // then
    assertNull(location);
  }
}
