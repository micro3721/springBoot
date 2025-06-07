package com.example.demo.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@RestController // Marks this class as a REST controller (combines @Controller and @ResponseBody)
public class HelloController {

    // --- Original Endpoints (Modified where necessary) ---

    /**
     * Simple GET endpoint returning a plain string.
     * Mapped to /hello
     *
     * @return A greeting string.
     */
    @GetMapping("/hello")
    public String sayHello() {
        return "Hello, World! Welcome to Spring Boot!";
    }

    /**
     * Simple GET endpoint for the root path, returning a plain string.
     * Mapped to /
     *
     * @return A home page message.
     */
    @GetMapping("/")
    public String home() {
        return "This is the home page.";
    }

    /**
     * Simple GET endpoint calculating a sum and returning a descriptive string.
     * Mapped to /sum
     *
     * @return String describing the sum of numbers 1 to 100.
     */
    @GetMapping("/sum")
    public String calculateSum() {
        // Keep this simple for now, returning string
        int sum = 0;
        for (int i = 1; i <= 100; i++) {
            sum += i;
        }
        // Note: Returning structured data (like JSON) is generally preferred for APIs.
        return "1到100的累加结果是: " + sum;
    }

    // --- Modified Endpoint: Bubble Sort (Returns JSON) ---

    /**
     * Demonstrates bubble sort on a predefined array and returns the
     * original and sorted arrays as a JSON object.
     * Mapped to GET /bubblesort
     *
     * @return ResponseEntity containing SortResponse DTO or an error.
     */
    @GetMapping("/bubblesort")
    public ResponseEntity<SortResponse> bubbleSortDemo() {
        int[] numbers = {64, 34, 25, 12, 22, 11, 90};
        // Create a List copy for the 'original' state before sorting
        List<Integer> originalList = Arrays.stream(numbers).boxed().collect(Collectors.toList());

        // Perform bubble sort (in-place sort modifies the original array 'numbers')
        int n = numbers.length;
        for (int i = 0; i < n - 1; i++) {
            boolean swapped = false;
            for (int j = 0; j < n - i - 1; j++) {
                if (numbers[j] > numbers[j + 1]) {
                    // Swap elements
                    int temp = numbers[j];
                    numbers[j] = numbers[j + 1];
                    numbers[j + 1] = temp;
                    swapped = true;
                }
            }
            // If no elements were swapped in inner loop, array is sorted
            if (!swapped) break;
        }
        // Create a List from the now sorted array
        List<Integer> sortedList = Arrays.stream(numbers).boxed().collect(Collectors.toList());

        // Create response DTO
        SortResponse response = new SortResponse(originalList, sortedList);
        // Return DTO as JSON with 200 OK status
        return ResponseEntity.ok(response);
    }

    // --- Modified Endpoint: Fibonacci (Returns JSON) ---

    /**
     * Calculates the 10th Fibonacci number (fixed) and returns the result as JSON.
     * Mapped to GET /fibonacci
     *
     * @return ResponseEntity containing FibonacciResponse DTO.
     */
    @GetMapping("/fibonacci")
    public ResponseEntity<FibonacciResponse> demonstrateFibonacciJson() {
        int n = 10; // Fixed value for this specific endpoint demonstration
        try {
            long fibResult = calculateNthFibonacci(n);
            FibonacciResponse response = new FibonacciResponse(n, fibResult, null);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            // Handle potential errors from the helper method (e.g., negative n, overflow)
            FibonacciResponse response = new FibonacciResponse(n, null, e.getMessage());
            // Return 400 Bad Request status with error details in JSON body
            return ResponseEntity.badRequest().body(response);
        }
    }


    // --- New Endpoints ---

    /**
     * Greets a user by name, where the name is provided as a path variable.
     * Mapped to GET /greet/{name}
     *
     * @param name The name extracted from the URL path.
     * @return ResponseEntity containing a JSON object with the greeting message or an error.
     */
    @GetMapping("/greet/{name}")
    public ResponseEntity<Map<String, String>> greetUser(@PathVariable String name) {
        // Basic validation for the path variable
        if (name == null || name.trim().isEmpty() || name.equals("{name}")) { // Check against placeholder too
            // Return a 400 Bad Request if name is missing or invalid
            return ResponseEntity.badRequest().body(Collections.singletonMap("error", "Name in path cannot be empty or placeholder."));
        }
        String greeting = "Hello, " + name + "! Welcome.";
        // Return JSON object: {"message": "Hello, [name]! Welcome."} with 200 OK
        return ResponseEntity.ok(Collections.singletonMap("message", greeting));
    }

    /**
     * Calculates the nth Fibonacci number based on a request parameter 'n'.
     * Mapped to GET /fibonacci-param?n=...
     *
     * @param n The Fibonacci sequence index (defaults to 0 if not provided).
     * @return ResponseEntity containing FibonacciResponse DTO with result or error.
     */
    @GetMapping("/fibonacci-param")
    public ResponseEntity<FibonacciResponse> calculateFibonacciWithParam(@RequestParam(name = "n", defaultValue = "0") int n) {
        // Add validation for the request parameter
        if (n < 0) {
            FibonacciResponse response = new FibonacciResponse(n, null, "Input 'n' cannot be negative.");
            return ResponseEntity.badRequest().body(response); // 400 Bad Request
        }
        // Add a practical limit to prevent very long calculations or Long overflow
        if (n > 92) { // Long.MAX_VALUE is F(92)
            FibonacciResponse response = new FibonacciResponse(n, null, "Input 'n' is too large (max 92 for standard long).");
            return ResponseEntity.badRequest().body(response); // 400 Bad Request
        }

        try {
            long fibResult = calculateNthFibonacci(n);
            FibonacciResponse response = new FibonacciResponse(n, fibResult, null);
            return ResponseEntity.ok(response); // 200 OK
        } catch (IllegalArgumentException e) {
            // Catch potential overflow error from helper if limit above wasn't sufficient
            FibonacciResponse response = new FibonacciResponse(n, null, e.getMessage());
            return ResponseEntity.badRequest().body(response); // 400 Bad Request
        }
    }

    /**
     * Calculates basic statistics (sum, average, min, max) for a list of numbers
     * provided in the JSON request body.
     * Mapped to POST /calculate-stats
     *
     * @param numbers A List of Double values from the request body. Can be null/empty.
     * @return ResponseEntity containing StatsResponse DTO or a JSON error object.
     */
    @PostMapping("/calculate-stats")
    public ResponseEntity<?> calculateStats(@RequestBody(required = false) List<Double> numbers) {
        // Validate the request body
        if (numbers == null || numbers.isEmpty()) {
            // Return 400 Bad Request if list is null or empty
            return ResponseEntity
                    .badRequest()
                    .body(Collections.singletonMap("error", "Request body must contain a non-empty JSON array of numbers."));
        }

        // Perform calculations using streams
        double sum = numbers.stream()
                .mapToDouble(Double::doubleValue) // Handle potential nulls if list could contain them
                .sum();
        double average = sum / numbers.size();
        // Use orElse for min/max in case stream is empty (though we checked above)
        double min = numbers.stream()
                .mapToDouble(Double::doubleValue)
                .min().orElse(Double.NaN);
        double max = numbers.stream()
                .mapToDouble(Double::doubleValue)
                .max().orElse(Double.NaN);

        // Create response DTO
        StatsResponse response = new StatsResponse(numbers.size(), sum, average, min, max);
        return ResponseEntity.ok(response); // Return JSON with 200 OK
    }


    // --- Helper Method & DTOs ---

    /**
     * Calculates the nth Fibonacci number iteratively.
     * Throws IllegalArgumentException for negative n or potential overflow.
     *
     * @param n The non-negative index in the Fibonacci sequence (F0=0, F1=1).
     * @return The nth Fibonacci number as a long.
     * @throws IllegalArgumentException if n is negative or result exceeds Long.MAX_VALUE.
     */
    private long calculateNthFibonacci(int n) {
        if (n < 0) throw new IllegalArgumentException("Input 'n' cannot be negative.");
        if (n == 0) return 0;
        if (n == 1) return 1;

        long fibPrev = 0;
        long fibCurr = 1;
        // Start from i = 2 because F(0) and F(1) are handled
        for (int i = 2; i <= n; i++) {
            // Check for potential overflow *before* the addition
            if (fibPrev > Long.MAX_VALUE - fibCurr) {
                // This check prevents overflow during 'fibPrev + fibCurr'
                throw new IllegalArgumentException("Fibonacci number exceeds Long.MAX_VALUE limit for n=" + n + " at step " + i);
            }
            long nextFib = fibPrev + fibCurr;
            fibPrev = fibCurr;
            fibCurr = nextFib;
        }
        return fibCurr;
    }

    // --- Data Transfer Objects (DTOs) for JSON Responses ---
    // Using static inner classes for simplicity here. Could be separate files.

    /**
     * DTO for returning bubble sort results.
     */
    public record SortResponse(List<Integer> original, List<Integer> sorted) {
    }

    /**
     * DTO for returning Fibonacci calculation results or errors.
     *
     * @param n      Input n
     * @param result Result (null if error)
     * @param error  Error message (null if success)
     */
    public record FibonacciResponse(int n, Long result, String error) {

        /**
         * Helper method to easily check status in frontend (not serialized)
         */
        @com.fasterxml.jackson.annotation.JsonIgnore // Prevent Jackson from serializing this getter
        public boolean isSuccess() {
            return error == null && result != null;
        }
    }

    /**
     * DTO for returning statistics calculation results.
     */
    public record StatsResponse(int count, double sum, double average, double min, double max) {
    }
}
