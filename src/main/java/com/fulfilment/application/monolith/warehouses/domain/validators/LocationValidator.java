package com.fulfilment.application.monolith.warehouses.domain.validators;

import com.fulfilment.application.monolith.warehouses.domain.exceptions.WarehouseValidationException;
import com.fulfilment.application.monolith.warehouses.domain.models.LocationDTO;
import com.fulfilment.application.monolith.warehouses.domain.ports.LocationResolver;
import jakarta.enterprise.context.ApplicationScoped;
import org.jboss.logging.Logger;

@ApplicationScoped
public class LocationValidator {

  private static final Logger LOG = Logger.getLogger(LocationValidator.class);

  private final LocationResolver locationResolver;

  public LocationValidator(LocationResolver locationResolver) {
    this.locationResolver = locationResolver;
  }

  public LocationDTO validateAndResolveLocation(String locationIdentifier) {
    LOG.debugf("Resolving location: %s", locationIdentifier);
    LocationDTO location = locationResolver.resolveByIdentifier(locationIdentifier);
    if (location == null) {
      LOG.errorf("Location %s does not exist", locationIdentifier);
      throw new WarehouseValidationException("Location " + locationIdentifier + " does not exist");
    }
    return location;
  }
}
