package com.fulfilment.application.monolith.fulfillment.domain.usecases;

import com.fulfilment.application.monolith.fulfillment.domain.exceptions.FulfillmentNotFoundException;
import com.fulfilment.application.monolith.fulfillment.domain.exceptions.FulfillmentValidationException;
import com.fulfilment.application.monolith.fulfillment.domain.models.FulfillmentDTO;
import com.fulfilment.application.monolith.fulfillment.domain.ports.CreateFulfillmentOperation;
import com.fulfilment.application.monolith.fulfillment.domain.ports.FulfillmentStore;
import com.fulfilment.application.monolith.fulfillment.domain.validators.ProductPerWarehouseValidator;
import com.fulfilment.application.monolith.fulfillment.domain.validators.WarehousePerProductPerStoreValidator;
import com.fulfilment.application.monolith.fulfillment.domain.validators.WarehousePerStoreValidator;
import jakarta.enterprise.context.ApplicationScoped;
import org.jboss.logging.Logger;

@ApplicationScoped
public class CreateFulfillmentUseCase implements CreateFulfillmentOperation {

  private static final Logger LOG = Logger.getLogger(CreateFulfillmentUseCase.class);

  private final FulfillmentStore fulfillmentStore;
  private final WarehousePerProductPerStoreValidator warehousePerProductPerStoreValidator;
  private final WarehousePerStoreValidator warehousePerStoreValidator;
  private final ProductPerWarehouseValidator productPerWarehouseValidator;

  public CreateFulfillmentUseCase(
      FulfillmentStore fulfillmentStore,
      WarehousePerProductPerStoreValidator warehousePerProductPerStoreValidator,
      WarehousePerStoreValidator warehousePerStoreValidator,
      ProductPerWarehouseValidator productPerWarehouseValidator) {
    this.fulfillmentStore = fulfillmentStore;
    this.warehousePerProductPerStoreValidator = warehousePerProductPerStoreValidator;
    this.warehousePerStoreValidator = warehousePerStoreValidator;
    this.productPerWarehouseValidator = productPerWarehouseValidator;
  }

  @Override
  public FulfillmentDTO create(FulfillmentDTO fulfillmentDTO) {
    LOG.infof(
        "Creating fulfillment: warehouse=%d product=%d store=%d",
        fulfillmentDTO.warehouseId, fulfillmentDTO.productId, fulfillmentDTO.storeId);

    validateRequiredFields(fulfillmentDTO);
    validateEntitiesExist(fulfillmentDTO);
    validateConstraints(fulfillmentDTO);

    fulfillmentStore.persistFulfillment(fulfillmentDTO);

    LOG.infof(
        "Fulfillment created: warehouse=%d product=%d store=%d",
        fulfillmentDTO.warehouseId, fulfillmentDTO.productId, fulfillmentDTO.storeId);

    return fulfillmentDTO;
  }

  private void validateRequiredFields(FulfillmentDTO dto) {
    if (dto.warehouseId == null || dto.productId == null || dto.storeId == null) {
      throw new FulfillmentValidationException(
          "warehouseId, productId, and storeId are required.");
    }
  }

  private void validateEntitiesExist(FulfillmentDTO dto) {
    if (!fulfillmentStore.warehouseExists(dto.warehouseId)) {
      throw new FulfillmentNotFoundException(
          "Warehouse with id " + dto.warehouseId + " does not exist.");
    }
    if (!fulfillmentStore.productExists(dto.productId)) {
      throw new FulfillmentNotFoundException(
          "Product with id " + dto.productId + " does not exist.");
    }
    if (!fulfillmentStore.storeExists(dto.storeId)) {
      throw new FulfillmentNotFoundException(
          "Store with id " + dto.storeId + " does not exist.");
    }
  }

  private void validateConstraints(FulfillmentDTO dto) {
    warehousePerProductPerStoreValidator.validate(dto.productId, dto.storeId);
    warehousePerStoreValidator.validate(dto.storeId);
    productPerWarehouseValidator.validate(dto.warehouseId);
  }
}
