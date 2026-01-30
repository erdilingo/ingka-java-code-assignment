package com.fulfilment.application.monolith.products;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

public class ProductTest {

  @Test
  public void testDefaultConstructor() {
    Product product = new Product();
    assertNull(product.name);
    assertNull(product.description);
    assertNull(product.price);
    assertEquals(0, product.stock);
  }

  @Test
  public void testNameConstructor() {
    Product product = new Product("KALLAX");
    assertEquals("KALLAX", product.name);
  }

  @Test
  public void testFieldAssignment() {
    Product product = new Product();
    product.name = "TONSTAD";
    product.description = "A nice shelf";
    product.price = new BigDecimal("199.99");
    product.stock = 42;

    assertEquals("TONSTAD", product.name);
    assertEquals("A nice shelf", product.description);
    assertEquals(new BigDecimal("199.99"), product.price);
    assertEquals(42, product.stock);
  }
}