package com.fulfilment.application.monolith.fulfillment.adapters.restapi;

import com.fulfilment.api.beans.FulfillmentRequest;
import com.fulfilment.api.beans.FulfillmentResponse;
import com.fulfilment.application.monolith.fulfillment.domain.models.FulfillmentDTO;
import com.fulfilment.application.monolith.fulfillment.domain.ports.CreateFulfillmentOperation;
import com.fulfilment.application.monolith.fulfillment.domain.ports.DeleteFulfillmentOperation;
import com.fulfilment.application.monolith.fulfillment.domain.ports.FulfillmentStore;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Response;
import java.util.List;
import org.jboss.logging.Logger;

@Path("/fulfillment")
@ApplicationScoped
@Produces("application/json")
@Consumes("application/json")
public class FulfillmentResourceImpl {

  private static final Logger LOG = Logger.getLogger(FulfillmentResourceImpl.class);

  private final CreateFulfillmentOperation createFulfillmentOperation;
  private final DeleteFulfillmentOperation deleteFulfillmentOperation;
  private final FulfillmentStore fulfillmentStore;

  public FulfillmentResourceImpl(
      CreateFulfillmentOperation createFulfillmentOperation,
      DeleteFulfillmentOperation deleteFulfillmentOperation,
      FulfillmentStore fulfillmentStore) {
    this.createFulfillmentOperation = createFulfillmentOperation;
    this.deleteFulfillmentOperation = deleteFulfillmentOperation;
    this.fulfillmentStore = fulfillmentStore;
  }

  @GET
  public List<FulfillmentResponse> listAllFulfillments() {
    LOG.debug("Listing all fulfillments");
    return fulfillmentStore.findAllFulfillments().stream().map(this::toResponse).toList();
  }

  @POST
  @Transactional
  public Response createFulfillment(FulfillmentRequest data) {
    LOG.infof("REST request to create fulfillment");
    FulfillmentDTO dto = new FulfillmentDTO();
    dto.warehouseId = data.getWarehouseId();
    dto.productId = data.getProductId();
    dto.storeId = data.getStoreId();

    FulfillmentDTO created = createFulfillmentOperation.create(dto);
    return Response.status(201).entity(toResponse(created)).build();
  }

  @DELETE
  @Path("/{id}")
  @Transactional
  public Response deleteFulfillmentById(@PathParam("id") long id) {
    LOG.infof("REST request to delete fulfillment: %d", id);
    deleteFulfillmentOperation.delete(id);
    return Response.status(204).build();
  }

  @GET
  @Path("/store/{storeId}")
  public List<FulfillmentResponse> listFulfillmentsByStore(@PathParam("storeId") long storeId) {
    LOG.debugf("Listing fulfillments for store: %d", storeId);
    return fulfillmentStore.findByStoreId(storeId).stream().map(this::toResponse).toList();
  }

  @GET
  @Path("/warehouse/{warehouseId}")
  public List<FulfillmentResponse> listFulfillmentsByWarehouse(
      @PathParam("warehouseId") long warehouseId) {
    LOG.debugf("Listing fulfillments for warehouse: %d", warehouseId);
    return fulfillmentStore.findByWarehouseId(warehouseId).stream().map(this::toResponse).toList();
  }

  private FulfillmentResponse toResponse(FulfillmentDTO dto) {
    FulfillmentResponse response = new FulfillmentResponse();
    response.setId(dto.id);
    response.setWarehouseId(dto.warehouseId);
    response.setProductId(dto.productId);
    response.setStoreId(dto.storeId);
    return response;
  }
}
