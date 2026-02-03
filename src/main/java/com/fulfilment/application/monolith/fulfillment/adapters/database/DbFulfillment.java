package com.fulfilment.application.monolith.fulfillment.adapters.database;

import com.fulfilment.application.monolith.products.Product;
import com.fulfilment.application.monolith.stores.Store;
import com.fulfilment.application.monolith.warehouses.adapters.database.DbWarehouse;
import jakarta.persistence.Cacheable;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Entity
@Cacheable
@Table(
    name = "fulfillment",
    uniqueConstraints =
        @UniqueConstraint(columnNames = {"warehouse_id", "product_id", "store_id"}))
public class DbFulfillment {

  @Id @GeneratedValue public Long id;

  @ManyToOne
  @JoinColumn(name = "warehouse_id", nullable = false)
  @OnDelete(action = OnDeleteAction.CASCADE)
  public DbWarehouse warehouse;

  @ManyToOne
  @JoinColumn(name = "product_id", nullable = false)
  @OnDelete(action = OnDeleteAction.CASCADE)
  public Product product;

  @ManyToOne
  @JoinColumn(name = "store_id", nullable = false)
  @OnDelete(action = OnDeleteAction.CASCADE)
  public Store store;

  public DbFulfillment() {}
}
