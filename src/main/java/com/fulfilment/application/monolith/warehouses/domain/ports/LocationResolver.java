package com.fulfilment.application.monolith.warehouses.domain.ports;

import com.fulfilment.application.monolith.warehouses.domain.models.LocationDTO;

public interface LocationResolver {
  LocationDTO resolveByIdentifier(String identifier);
}
