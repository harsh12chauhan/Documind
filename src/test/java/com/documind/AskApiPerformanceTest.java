
package com.documind.performance;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class AskApiPerformanceTest {

    private static final String DOCUMENT_ID =
            "04dd7c05-4d6f-4c27-811e-731fec2657bb";

    private static final int REQUEST_COUNT = 5;

    @BeforeAll
    static void setup() {
        RestAssured.baseURI = "http://localhost:8000";
    }

    @Test
    void measureAskApiPerformance() {

        List<Long> responseTimes = new ArrayList<>();

        for (int i = 1; i <= REQUEST_COUNT; i++) {

            long start = System.currentTimeMillis();

            Response response = RestAssured
                    .given()
                    .contentType("application/json")
                    .body("""
                            {
                                "question": "What are the three storage tiers?"
                            }
                            """)
                    .when()
                    .post("/api/v1/documents/" + DOCUMENT_ID + "/ask");

            long duration = System.currentTimeMillis() - start;

            assertEquals(200, response.statusCode());

            String answer = response.jsonPath().getString("answer");

            assertNotNull(answer);
            assertFalse(answer.isBlank());

            responseTimes.add(duration);

            System.out.println(
                    "Request " + i + " = " + duration + " ms"
            );
        }

        double average = responseTimes.stream()
                .mapToLong(Long::longValue)
                .average()
                .orElse(0);

        long min = responseTimes.stream()
                .mapToLong(Long::longValue)
                .min()
                .orElse(0);

        long max = responseTimes.stream()
                .mapToLong(Long::longValue)
                .max()
                .orElse(0);

        System.out.println("\n===== PERFORMANCE REPORT =====");
        System.out.println("Total Requests: " + REQUEST_COUNT);
        System.out.println("Average: " + average + " ms");
        System.out.println("Minimum: " + min + " ms");
        System.out.println("Maximum: " + max + " ms");
    }
}
