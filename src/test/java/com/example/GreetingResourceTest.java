// NotificationResourceTest.java
package com.example;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;

@QuarkusTest
class NotificationResourceTest {

    @Test
    void testGetAllNotifications() {
        given()
          .when().get("/notifications")
          .then()
             .statusCode(200)
             .body("$.size()", is(0)); // Initially empty
    }

    @Test
    void testGetNotificationCount() {
        given()
          .when().get("/notifications/count")
          .then()
             .statusCode(200)
             .body(is("0")); // Initially 0
    }

    @Test
    void testGetLatestNotificationWhenEmpty() {
        given()
          .when().get("/notifications/latest")
          .then()
             .statusCode(204); // No content when empty
    }

    @Test
    void testAddTestNotification() {
        given()
          .contentType("application/json")
          .body("{\"title\":\"Test Title\",\"message\":\"Test Message\",\"type\":\"info\"}")
          .when().post("/notifications")
          .then()
             .statusCode(200)
             .body("title", is("Test Title"))
             .body("message", is("Test Message"))
             .body("type", is("info"));
    }

    @Test
    void testAddTestNotificationWithDefaults() {
        given()
          .contentType("application/json")
          .body("{}")
          .when().post("/notifications")
          .then()
             .statusCode(200)
             .body("title", is("Test Notification"))
             .body("message", is("This is a test notification"))
             .body("type", is("info"));
    }
}