package de.gedoplan;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import jakarta.ws.rs.core.MediaType;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.is;

@QuarkusTest
class JacksonBehaviorTest {

    // Fixed in 4.0.0.Beta2
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

    // Works in Quarkus 3.40 and with quarkus.rest.jackson.optimization.enable-reflection-free-serializers=false in Quarkus 4
    @Test
    void patchChangesOnlyGivenProperties() {
        String location = given()
                .contentType(ContentType.JSON)
                .body("""
                        {"title": "Booktitle", "author": "Bookauthor", "pages": 400, "publishedOn": "1900-01-01"}
                        """)
                .when().post("/books")
                .then()
                .statusCode(201)
                .extract().header("Location");

        given()
                .contentType(MediaType.APPLICATION_MERGE_PATCH_JSON)
//                .contentType("application/merge-patch+json") // Quarkus3.40
                .body("""
                        {"pages": 480}
                        """)
                .when().patch(location)
                .then()
                .statusCode(200)
                .body("pages", is(480))
                .body("title", is("Booktitle"));
    }

}
