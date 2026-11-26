
package com.documind.performance;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.*;

import static org.junit.jupiter.api.Assertions.*;

class AskApiLoadTest {

    private static final String DOCUMENT_ID =
            "04dd7c05-4d6f-4c27-811e-731fec2657bb";

    private static final String API_ENDPOINT =
            "/api/v1/documents/" + DOCUMENT_ID + "/ask";

    private static final String QUESTION =
            "What are the three storage tiers?";

    @BeforeAll
    static void setup() {
        RestAssured.baseURI = "http://localhost:8000";
    }

    @Test
    void concurrentLoadTest() throws InterruptedException {

        int[] userLevels = {2, 5, 10};

        System.out.println("\n======================================");
        System.out.println("       DOCUMIND LOAD TEST REPORT");
        System.out.println("======================================");

        for (int users : userLevels) {
            runLoadTest(users);
        }
    }

    private void runLoadTest(int concurrentUsers)
            throws InterruptedException {

        ExecutorService executor =
                Executors.newFixedThreadPool(concurrentUsers);

        CountDownLatch ready =
                new CountDownLatch(concurrentUsers);

        CountDownLatch start =
                new CountDownLatch(1);

        List<Long> responseTimes =
                Collections.synchronizedList(new ArrayList<>());

        List<String> errors =
                Collections.synchronizedList(new ArrayList<>());

        List<Future<?>> futures = new ArrayList<>();

        System.out.println("\nStarting test with "
                + concurrentUsers + " concurrent users...");

        for (int i = 0; i < concurrentUsers; i++) {

            final int requestNumber = i + 1;

            Future<?> future = executor.submit(() -> {

                ready.countDown();

                try {
                    // Wait until all users are ready.
                    start.await();

                    long requestStart =
                            System.currentTimeMillis();

                    Response response = RestAssured
                            .given()
                            .contentType("application/json")
                            .accept("application/json")
                            .body("{\"question\":\"" + QUESTION + "\"}")
                            .when()
                            .post(API_ENDPOINT);

                    long duration =
                            System.currentTimeMillis() - requestStart;

                    // Validate HTTP status.
                    if (response.statusCode() != 200) {
                        throw new AssertionError(
                                "HTTP Status: " + response.statusCode()
                                        + ", Response: " + response.asString()
                        );
                    }

                    // Validate response DTO fields.
                    String documentId =
                            response.jsonPath().getString("documentId");

                    String returnedQuestion =
                            response.jsonPath().getString("question");

                    String answer =
                            response.jsonPath().getString("answer");

                    if (documentId == null || documentId.isBlank()) {
                        throw new AssertionError(
                                "Document ID is missing"
                        );
                    }

                    if (!DOCUMENT_ID.equals(documentId)) {
                        throw new AssertionError(
                                "Incorrect document ID: " + documentId
                        );
                    }

                    if (returnedQuestion == null
                            || returnedQuestion.isBlank()) {
                        throw new AssertionError(
                                "Question is missing"
                        );
                    }

                    if (!QUESTION.equals(returnedQuestion)) {
                        throw new AssertionError(
                                "Returned question does not match request"
                        );
                    }

                    if (answer == null || answer.isBlank()) {
                        throw new AssertionError(
                                "Answer is empty"
                        );
                    }

                    responseTimes.add(duration);

                    System.out.println(
                            "User " + requestNumber
                                    + " | " + duration + " ms"
                                    + " | SUCCESS"
                    );

                } catch (Exception | AssertionError e) {

                    errors.add(
                            "User " + requestNumber
                                    + " | FAILED | " + e.getMessage()
                    );
                }
            });

            futures.add(future);
        }

        // Wait until every worker is ready.
        boolean allReady = ready.await(30, TimeUnit.SECONDS);

        if (!allReady) {
            start.countDown();
            executor.shutdownNow();
            fail("Not all worker threads became ready");
        }

        // Release all requests simultaneously.
        long actualStart = System.currentTimeMillis();

        start.countDown();

        // Wait for all requests to finish.
        executor.shutdown();

        boolean completed = executor.awaitTermination(
                10, TimeUnit.MINUTES
        );

        long totalDuration =
                System.currentTimeMillis() - actualStart;

        if (!completed) {
            executor.shutdownNow();
            fail("Load test timed out");
        }

        // Calculate statistics.
        int successfulRequests = responseTimes.size();
        int failedRequests = errors.size();

        double average = responseTimes.stream()
                .mapToLong(Long::longValue)
                .average()
                .orElse(0);

        long minimum = responseTimes.stream()
                .mapToLong(Long::longValue)
                .min()
                .orElse(0);

        long maximum = responseTimes.stream()
                .mapToLong(Long::longValue)
                .max()
                .orElse(0);

        double throughput = totalDuration > 0
                ? successfulRequests / (totalDuration / 1000.0)
                : 0;

        double successRate =
                (successfulRequests * 100.0) / concurrentUsers;

        // Print report.
        System.out.println("\n======================================");
        System.out.println("         LOAD TEST SUMMARY");
        System.out.println("======================================");

        System.out.println("Concurrent Users : " + concurrentUsers);
        System.out.println("Total Requests   : " + concurrentUsers);
        System.out.println("Successful       : " + successfulRequests);
        System.out.println("Failed           : " + failedRequests);

        System.out.printf("Success Rate     : %.2f%%%n", successRate);
        System.out.printf("Average Time     : %.2f ms%n", average);

        System.out.println("Minimum Time     : " + minimum + " ms");
        System.out.println("Maximum Time     : " + maximum + " ms");
        System.out.println("Total Duration   : " + totalDuration + " ms");

        System.out.printf(
                "Throughput       : %.2f requests/sec%n",
                throughput
        );

        System.out.println("======================================");

        if (!errors.isEmpty()) {
            System.out.println("\nERROR DETAILS:");

            errors.forEach(System.out::println);
        }

        // Fail the test if any request failed.
        assertEquals(
                concurrentUsers,
                successfulRequests,
                "Some concurrent requests failed"
        );
    }
}
