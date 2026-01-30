package com.fulfilment.application.monolith.warehouses.domain.models;

import java.time.ZonedDateTime;

public class WarehouseDTO {

  // unique identifier
  public String businessUnitCode;

  public String location;

  public Integer capacity;

  public Integer stock;

  public ZonedDateTime creationAt;

  public ZonedDateTime archivedAt;
}
