package com.fulfilment.application.monolith.fulfillment.domain.exceptions;

public class FulfillmentValidationException extends RuntimeException {

  public FulfillmentValidationException(String message) {
    super(message);
  }
}
