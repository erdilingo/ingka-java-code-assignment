package com.fulfilment.application.monolith.fulfillment.domain.usecases;

import com.fulfilment.application.monolith.fulfillment.domain.exceptions.FulfillmentNotFoundException;
import com.fulfilment.application.monolith.fulfillment.domain.models.FulfillmentDTO;
import com.fulfilment.application.monolith.fulfillment.domain.ports.DeleteFulfillmentOperation;
import com.fulfilment.application.monolith.fulfillment.domain.ports.FulfillmentStore;
import jakarta.enterprise.context.ApplicationScoped;
import org.jboss.logging.Logger;

@ApplicationScoped
public class DeleteFulfillmentUseCase implements DeleteFulfillmentOperation {

  private static final Logger LOG = Logger.getLogger(DeleteFulfillmentUseCase.class);

  private final FulfillmentStore fulfillmentStore;

  public DeleteFulfillmentUseCase(FulfillmentStore fulfillmentStore) {
    this.fulfillmentStore = fulfillmentStore;
  }

  @Override
  public void delete(Long id) {
    LOG.infof("Deleting fulfillment with id: %d", id);

    FulfillmentDTO existing = fulfillmentStore.findFulfillmentById(id);
    if (existing == null) {
      throw new FulfillmentNotFoundException("Fulfillment with id " + id + " does not exist.");
    }

    fulfillmentStore.deleteFulfillment(id);
    LOG.infof("Fulfillment %d deleted", id);
  }
}
