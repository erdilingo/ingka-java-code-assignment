package com.fulfilment.application.monolith.fulfillment;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.notNullValue;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

@QuarkusTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class FulfillmentResourceTest {

  private static final String PATH = "fulfillment";

  // Use warehouse 1, product 1, store 1 - these are not deleted by other tests
  @Test
  @Order(1)
  public void testListFulfillments() {
    given().when().get(PATH).then().statusCode(200);
  }

  @Test
  @Order(2)
  public void testCreateFulfillment() {
    given()
        .contentType(ContentType.JSON)
        .body("{\"warehouseId\": 1, \"productId\": 1, \"storeId\": 1}")
        .when()
        .post(PATH)
        .then()
        .statusCode(201)
        .body("id", notNullValue());
  }

  @Test
  @Order(3)
  public void testCreateFulfillmentMissingFields() {
    given()
        .contentType(ContentType.JSON)
        .body("{\"warehouseId\": 1}")
        .when()
        .post(PATH)
        .then()
        .statusCode(409);
  }

  @Test
  @Order(4)
  public void testCreateFulfillmentNonExistentWarehouse() {
    given()
        .contentType(ContentType.JSON)
        .body("{\"warehouseId\": 999, \"productId\": 1, \"storeId\": 1}")
        .when()
        .post(PATH)
        .then()
        .statusCode(404);
  }

  @Test
  @Order(5)
  public void testMaxWarehousesPerProductPerStore() {
    // Use product 2 and store 1 (store 2 gets deleted by StoreResourceTest)
    // First fulfillment - warehouse 1, product 2, store 1
    given()
        .contentType(ContentType.JSON)
        .body("{\"warehouseId\": 1, \"productId\": 2, \"storeId\": 1}")
        .when()
        .post(PATH)
        .then()
        .statusCode(201);

    // Second fulfillment - warehouse 2, product 2, store 1
    given()
        .contentType(ContentType.JSON)
        .body("{\"warehouseId\": 2, \"productId\": 2, \"storeId\": 1}")
        .when()
        .post(PATH)
        .then()
        .statusCode(201);

    // Third should fail — max 2 warehouses per product per store
    given()
        .contentType(ContentType.JSON)
        .body("{\"warehouseId\": 3, \"productId\": 2, \"storeId\": 1}")
        .when()
        .post(PATH)
        .then()
        .statusCode(409);
  }

  @Test
  @Order(6)
  public void testDeleteNonExistent() {
    given().when().delete(PATH + "/9999").then().statusCode(404);
  }
}
