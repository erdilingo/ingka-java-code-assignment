package com.fulfilment.application.monolith.location;

import com.fulfilment.application.monolith.warehouses.domain.models.LocationDTO;
import com.fulfilment.application.monolith.warehouses.domain.ports.LocationResolver;
import jakarta.enterprise.context.ApplicationScoped;

import org.jboss.logging.Logger;

import java.util.ArrayList;
import java.util.List;

@ApplicationScoped
public class LocationGateway implements LocationResolver {

    private static final Logger LOG = Logger.getLogger(LocationGateway.class);

    private static final List<LocationDTO> locations = new ArrayList<>();

    static {
        locations.add(new LocationDTO("ZWOLLE-001", 1, 40));
        locations.add(new LocationDTO("ZWOLLE-002", 2, 50));
        locations.add(new LocationDTO("AMSTERDAM-001", 5, 100));
        locations.add(new LocationDTO("AMSTERDAM-002", 3, 75));
        locations.add(new LocationDTO("TILBURG-001", 1, 40));
        locations.add(new LocationDTO("HELMOND-001", 1, 45));
        locations.add(new LocationDTO("EINDHOVEN-001", 2, 70));
        locations.add(new LocationDTO("VETSBY-001", 1, 90));
    }

    @Override
    public LocationDTO resolveByIdentifier(String identifier) {
        return locations.stream()
                .filter(location -> location.identification.equals(identifier))
                .findFirst()
                .orElseGet(() -> {
                    LOG.warnf("Location with identifier %s does not exist", identifier);
                    return null;
                });
    }
}