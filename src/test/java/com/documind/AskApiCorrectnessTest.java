
package com.documind.performance;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AskApiCorrectnessTest {

    private static final String DOCUMENT_ID =
            "04dd7c05-4d6f-4c27-811e-731fec2657bb";

    @BeforeAll
    static void setup() {
        RestAssured.baseURI = "http://localhost:8000";
    }

    private String ask(String question) {

        Response response = RestAssured
                .given()
                .contentType("application/json")
                .body("{\"question\":\"" + question + "\"}")
                .when()
                .post("/api/v1/documents/" + DOCUMENT_ID + "/ask")
                .then()
                .statusCode(200)
                .extract()
                .response();

        // Adjust this JSON path if your DTO uses another property.
        return response.jsonPath().getString("answer");
    }

    @Test
    void shouldReturnStorageTiers() {

        String answer = ask(
                "What are the three storage tiers?"
        );

        assertNotNull(answer);
        assertTrue(answer.toLowerCase().contains("hot"));
        assertTrue(answer.toLowerCase().contains("cool"));
        assertTrue(answer.toLowerCase().contains("archive"));
    }

    @Test
    void shouldReturnCorrectHotStoragePrice() {

        String answer = ask(
                "What is the monthly storage price of the Hot tier?"
        );

        assertNotNull(answer);
        assertTrue(answer.contains("0.023"));
    }

    @Test
    void shouldReturnCorrectArchiveRetrievalFee() {

        String answer = ask(
                "What is the retrieval fee for Archive storage?"
        );

        assertNotNull(answer);
        assertTrue(answer.contains("0.030"));
    }

    @Test
    void shouldReturnCorrectHeadquarters() {

        String answer = ask(
                "Where is Aurora Cloud headquartered?"
        );

        assertNotNull(answer);
        assertTrue(answer.toLowerCase().contains("singapore"));
    }

    @Test
    void shouldReturnCorrectEnterpriseResponseTime() {

        String answer = ask(
                "What is the critical response time for Enterprise support?"
        );

        assertNotNull(answer);
        assertTrue(answer.toLowerCase().contains("15 minutes"));
    }
}
