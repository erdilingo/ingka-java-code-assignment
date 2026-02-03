package com.fulfilment.application.monolith.fulfillment.domain.exceptions;

public class FulfillmentNotFoundException extends RuntimeException {

  public FulfillmentNotFoundException(String message) {
    super(message);
  }
}
