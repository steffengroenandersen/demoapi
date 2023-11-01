package com.example.demoapi;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Configuration
public class RemoteApiTester implements CommandLineRunner {

    private Mono<String> callSlowEndpoint() {
        Mono<String> slowResponse = WebClient.create()
                .get()
                .uri("http://localhost:8080/random-string-slow")
                .retrieve()
                .bodyToMono(String.class)
                .doOnError(e -> System.out.println("UUUPS : " + e.getMessage()));
        return slowResponse;
    }

    public void callEndpointBlocking() {
        long start = System.currentTimeMillis();
        List<String> ramdomStrings = new ArrayList<>();

        Mono<String> slowResponse = callSlowEndpoint();
        ramdomStrings.add(slowResponse.block()); //Three seconds spent

        slowResponse = callSlowEndpoint();
        ramdomStrings.add(slowResponse.block());//Three seconds spent

        slowResponse = callSlowEndpoint();
        ramdomStrings.add(slowResponse.block());//Three seconds spent
        long end = System.currentTimeMillis();
        ramdomStrings.add(0, "Time spent BLOCKING (ms): " + (end - start));

        System.out.println(ramdomStrings.stream().collect(Collectors.joining(",")));
        System.out.println(String.join(",", ramdomStrings));

    }

    public void callSlowEndpointNonBlocking() {
        long startTime = System.currentTimeMillis(); // Record the start time

        // Create three asynchronous operations to call the slow endpoint
        Mono<String> slowEndpointCall1 = callSlowEndpoint();
        Mono<String> slowEndpointCall2 = callSlowEndpoint();
        Mono<String> slowEndpointCall3 = callSlowEndpoint();

        // Combine the results of the three asynchronous operations into a single Mono
        var combinedResults = Mono.zip(
                slowEndpointCall1,
                slowEndpointCall2,
                slowEndpointCall3).map(resultTuple -> {
            List<String> randomStrings = new ArrayList<>();
            randomStrings.add(resultTuple.getT1());
            randomStrings.add(resultTuple.getT2());
            randomStrings.add(resultTuple.getT3());
            long endTime = System.currentTimeMillis(); // Record the end time
            randomStrings.add(0, "Time spent NON-BLOCKING (ms): " + (endTime - startTime)); // Calculate and add execution time
            return randomStrings;
        });

        // Block until all three asynchronous operations are completed
        List<String> resultList = combinedResults.block();

        // Print the results as a comma-separated string
        System.out.println(resultList.stream().collect(Collectors.joining(",")));
    }


    @Override
    public void run(String... args) throws Exception {

        // Doesn't work
        //System.out.println(callSlowEndpoint().toString());

        // Works
        //String randomStr = callSlowEndpoint().block();
        //System.out.println(randomStr);

        // Calls them with blocking
        //callEndpointBlocking();

        // Calls non blocking
        callSlowEndpointNonBlocking();
    }
}

