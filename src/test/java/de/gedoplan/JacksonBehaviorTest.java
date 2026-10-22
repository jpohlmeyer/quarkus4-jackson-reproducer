package de.gedoplan;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;

@QuarkusTest
class JacksonBehaviorTest {

    // Only rejected with quarkus.rest.jackson.optimization.enable-reflection-free-serializers=false
    @Test
    void nullForPrimitiveIsRejected() {
        given()
                .contentType(ContentType.JSON)
                .body("""
                        {"title": "No pages", "author": "Tester", "pages": null}
                        """)
                .when().post("/books")
                .then()
                .statusCode(400);
    }
}
