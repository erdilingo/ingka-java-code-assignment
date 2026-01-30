package com.fulfilment.application.monolith.products;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;

@QuarkusTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ProductResourceTest {

  private static final String PATH = "product";

  @Test
  @Order(1)
  public void testListAllProducts() {
    given()
        .when().get(PATH)
        .then()
        .statusCode(200)
        .body("$.size()", greaterThanOrEqualTo(2));
  }

  @Test
  @Order(2)
  public void testGetSingleProduct() {
    given()
        .when().get(PATH + "/2")
        .then()
        .statusCode(200)
        .body("name", notNullValue());
  }

  @Test
  @Order(3)
  public void testGetSingleProductNotFound() {
    given()
        .when().get(PATH + "/99999")
        .then()
        .statusCode(404);
  }

  @Test
  @Order(4)
  public void testCreateProduct() {
    given()
        .contentType("application/json")
        .body("{\"name\": \"TESTPRODUCT\", \"description\": \"A test\", \"price\": 29.99, \"stock\": 5}")
        .when().post(PATH)
        .then()
        .statusCode(201)
        .body("name", equalTo("TESTPRODUCT"))
        .body("stock", equalTo(5));
  }

  @Test
  @Order(5)
  public void testCreateProductWithIdFails() {
    given()
        .contentType("application/json")
        .body("{\"id\": 999, \"name\": \"BAD\", \"stock\": 1}")
        .when().post(PATH)
        .then()
        .statusCode(422);
  }

  @Test
  @Order(6)
  public void testUpdateProduct() {
    given()
        .contentType("application/json")
        .body("{\"name\": \"UPDATED\", \"description\": \"Updated desc\", \"price\": 49.99, \"stock\": 20}")
        .when().put(PATH + "/2")
        .then()
        .statusCode(200)
        .body("name", equalTo("UPDATED"));
  }

  @Test
  @Order(7)
  public void testUpdateProductNotFound() {
    given()
        .contentType("application/json")
        .body("{\"name\": \"GHOST\", \"stock\": 1}")
        .when().put(PATH + "/99999")
        .then()
        .statusCode(404);
  }

  @Test
  @Order(8)
  public void testUpdateProductNoName() {
    given()
        .contentType("application/json")
        .body("{\"stock\": 1}")
        .when().put(PATH + "/2")
        .then()
        .statusCode(422);
  }

  @Test
  @Order(9)
  public void testDeleteProduct() {
    given()
        .when().delete(PATH + "/3")
        .then()
        .statusCode(204);
  }

  @Test
  @Order(10)
  public void testDeleteProductNotFound() {
    given()
        .when().delete(PATH + "/99999")
        .then()
        .statusCode(404);
  }
}