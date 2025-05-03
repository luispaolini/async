package dev.luispaolini.async.post;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import org.junit.jupiter.api.*;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.test.StepVerifier;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

public class PostClientMockWebServerTest {

    private static MockWebServer mockWebServer;
    private PostClient postClient;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeAll
    static void setup() throws IOException {
        mockWebServer = new MockWebServer();
        mockWebServer.start();
    }

    @AfterAll
    static void teardown() throws IOException {
        mockWebServer.shutdown();
    }

    @BeforeEach
    void initialize(){
        String baseUrl = String.format("http://localhost:%s", mockWebServer.getPort());
        postClient = new PostClient(WebClient.builder().baseUrl(baseUrl).build());
    }

    @Test
    void testFindAllPosts() throws JsonProcessingException, InterruptedException {
        var hello = new Post(1, 1, "Hello, World", "This is my first post");
        var goodbye = new Post(1, 2, "Goodbye, World", "This is my last post");
        var jsonResult = objectMapper.writeValueAsString(new Post[]{hello, goodbye});

        mockWebServer.enqueue(new MockResponse()
                .setBody(jsonResult)
                .addHeader("Content-Type", "application/json"));

        StepVerifier.create(postClient.findAll())
                .expectNext(hello)
                .expectNext(goodbye)
                .verifyComplete();

        RecordedRequest recordedRequest = mockWebServer.takeRequest();
        assertEquals("/posts", recordedRequest.getPath());
        assertEquals("GET", recordedRequest.getMethod());

    }
}
