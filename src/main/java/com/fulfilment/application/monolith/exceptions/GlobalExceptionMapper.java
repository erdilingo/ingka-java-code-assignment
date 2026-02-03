package com.fulfilment.application.monolith.exceptions;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fulfilment.application.monolith.fulfillment.domain.exceptions.FulfillmentNotFoundException;
import com.fulfilment.application.monolith.fulfillment.domain.exceptions.FulfillmentValidationException;
import com.fulfilment.application.monolith.warehouses.domain.exceptions.WarehouseNotFoundException;
import com.fulfilment.application.monolith.warehouses.domain.exceptions.WarehouseValidationException;
import jakarta.inject.Inject;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import org.jboss.logging.Logger;

@Provider
public class GlobalExceptionMapper implements ExceptionMapper<Exception> {

  private static final Logger LOG = Logger.getLogger(GlobalExceptionMapper.class);

  @Inject
  ObjectMapper objectMapper;

  @Override
  public Response toResponse(Exception exception) {
    int code = mapToStatusCode(exception);

    if (code == Response.Status.INTERNAL_SERVER_ERROR.getStatusCode()) {
      LOG.error("Unexpected error handling request", exception);
    } else {
      LOG.warnf("Request failed with status %d: %s", code, exception.getMessage());
    }

    ObjectNode body = objectMapper.createObjectNode();
    body.put("exceptionType", exception.getClass().getName());
    body.put("code", code);
    if (exception.getMessage() != null) {
      body.put("error", exception.getMessage());
    }

    return Response.status(code).entity(body).build();
  }

  private int mapToStatusCode(Exception exception) {
    if (exception instanceof WarehouseNotFoundException) {
      return Response.Status.NOT_FOUND.getStatusCode();
    }
    if (exception instanceof WarehouseValidationException) {
      return Response.Status.BAD_REQUEST.getStatusCode();
    }
    if (exception instanceof FulfillmentNotFoundException) {
      return Response.Status.NOT_FOUND.getStatusCode();
    }
    if (exception instanceof FulfillmentValidationException) {
      return Response.Status.CONFLICT.getStatusCode();
    }
    if (exception instanceof jakarta.ws.rs.WebApplicationException wae) {
      return wae.getResponse().getStatus();
    }
    return Response.Status.INTERNAL_SERVER_ERROR.getStatusCode();
  }
}
