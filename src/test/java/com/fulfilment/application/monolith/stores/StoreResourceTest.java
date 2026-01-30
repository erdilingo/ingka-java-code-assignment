package com.fulfilment.application.monolith.stores;

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
public class StoreResourceTest {

  private static final String PATH = "stores";

  @Test
  @Order(1)
  public void testListAllStores() {
    given()
        .when().get(PATH)
        .then()
        .statusCode(200)
        .body("$.size()", greaterThanOrEqualTo(3));
  }

  @Test
  @Order(2)
  public void testGetSingleStore() {
    given()
        .when().get(PATH + "/1")
        .then()
        .statusCode(200)
        .body("name", notNullValue());
  }

  @Test
  @Order(3)
  public void testGetSingleStoreNotFound() {
    given()
        .when().get(PATH + "/99999")
        .then()
        .statusCode(404);
  }

  @Test
  @Order(4)
  public void testCreateStore() {
    given()
        .contentType("application/json")
        .body("{\"name\": \"TESTSTORE\", \"quantityProductsInStock\": 15}")
        .when().post(PATH)
        .then()
        .statusCode(201)
        .body("name", equalTo("TESTSTORE"))
        .body("quantityProductsInStock", equalTo(15));
  }

  @Test
  @Order(5)
  public void testCreateStoreWithIdFails() {
    given()
        .contentType("application/json")
        .body("{\"id\": 999, \"name\": \"BAD\", \"quantityProductsInStock\": 1}")
        .when().post(PATH)
        .then()
        .statusCode(422);
  }

  @Test
  @Order(6)
  public void testUpdateStore() {
    given()
        .contentType("application/json")
        .body("{\"name\": \"UPDATED\", \"quantityProductsInStock\": 99}")
        .when().put(PATH + "/1")
        .then()
        .statusCode(200)
        .body("name", equalTo("UPDATED"));
  }

  @Test
  @Order(7)
  public void testUpdateStoreNotFound() {
    given()
        .contentType("application/json")
        .body("{\"name\": \"GHOST\", \"quantityProductsInStock\": 1}")
        .when().put(PATH + "/99999")
        .then()
        .statusCode(404);
  }

  @Test
  @Order(8)
  public void testUpdateStoreNoName() {
    given()
        .contentType("application/json")
        .body("{\"quantityProductsInStock\": 1}")
        .when().put(PATH + "/1")
        .then()
        .statusCode(422);
  }

  @Test
  @Order(9)
  public void testPatchStore() {
    given()
        .contentType("application/json")
        .body("{\"name\": \"PATCHED\", \"quantityProductsInStock\": 50}")
        .when().patch(PATH + "/1")
        .then()
        .statusCode(200)
        .body("name", equalTo("PATCHED"));
  }

  @Test
  @Order(10)
  public void testPatchStoreNotFound() {
    given()
        .contentType("application/json")
        .body("{\"name\": \"GHOST\", \"quantityProductsInStock\": 1}")
        .when().patch(PATH + "/99999")
        .then()
        .statusCode(404);
  }

  @Test
  @Order(11)
  public void testPatchStoreNoName() {
    given()
        .contentType("application/json")
        .body("{\"quantityProductsInStock\": 1}")
        .when().patch(PATH + "/1")
        .then()
        .statusCode(422);
  }

  @Test
  @Order(12)
  public void testDeleteStore() {
    given()
        .when().delete(PATH + "/2")
        .then()
        .statusCode(204);
  }

  @Test
  @Order(13)
  public void testDeleteStoreNotFound() {
    given()
        .when().delete(PATH + "/99999")
        .then()
        .statusCode(404);
  }
}