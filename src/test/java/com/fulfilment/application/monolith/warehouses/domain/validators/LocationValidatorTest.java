package com.fulfilment.application.monolith.warehouses.domain.validators;

import com.fulfilment.application.monolith.warehouses.domain.exceptions.WarehouseValidationException;
import com.fulfilment.application.monolith.warehouses.domain.models.LocationDTO;
import com.fulfilment.application.monolith.warehouses.domain.ports.LocationResolver;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

public class LocationValidatorTest {

  @Mock
  private LocationResolver locationResolver;

  private LocationValidator locationValidator;

  @BeforeEach
  public void setUp() {
    MockitoAnnotations.openMocks(this);
    locationValidator = new LocationValidator(locationResolver);
  }

  @Test
  public void testValidateAndResolveLocation_valid() {
    LocationDTO locationDTO = new LocationDTO("ZWOLLE-001", 1, 40);
    when(locationResolver.resolveByIdentifier("ZWOLLE-001")).thenReturn(locationDTO);

    LocationDTO result = locationValidator.validateAndResolveLocation("ZWOLLE-001");

    assertNotNull(result);
    assertEquals("ZWOLLE-001", result.identification);
  }

  @Test
  public void testValidateAndResolveLocation_notFound_throws() {
    when(locationResolver.resolveByIdentifier("INVALID")).thenReturn(null);

    WarehouseValidationException ex = assertThrows(WarehouseValidationException.class, () ->
        locationValidator.validateAndResolveLocation("INVALID")
    );
    assertTrue(ex.getMessage().contains("INVALID"));
    assertTrue(ex.getMessage().contains("does not exist"));
  }
}