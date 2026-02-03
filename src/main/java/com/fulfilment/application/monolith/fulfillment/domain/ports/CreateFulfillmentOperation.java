package com.fulfilment.application.monolith.fulfillment.domain.ports;

import com.fulfilment.application.monolith.fulfillment.domain.models.FulfillmentDTO;

public interface CreateFulfillmentOperation {

  FulfillmentDTO create(FulfillmentDTO fulfillmentDTO);
}
