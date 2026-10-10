package com.connection.api;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.resttestclient.TestRestTemplate;
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureTestRestTemplate;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureTestRestTemplate
class ApiApplicationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    private String url(String path) {
        return "http://localhost:" + port + path;
    }

    @Test
    void shouldFindPathWithinThreeHops() {

        int a = createPerson("A");
        int b = createPerson("B");
        int c = createPerson("C");
        int d = createPerson("D");

        connect(a, b);
        connect(b, c);
        connect(c, d);

        ResponseEntity<Map> response =
                restTemplate.getForEntity(
                        url("/path?from=" + a + "&to=" + d),
                        Map.class
                );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(true, response.getBody().get("connected"));
        assertEquals(3, response.getBody().get("hops"));
    }

    @Test
    void shouldNotFindPathOverThreeHops() {

        int a = createPerson("A");
        int b = createPerson("B");
        int c = createPerson("C");
        int d = createPerson("D");
        int e = createPerson("E");

        connect(a, b);
        connect(b, c);
        connect(c, d);
        connect(d, e);

        ResponseEntity<Map> response =
                restTemplate.getForEntity(
                        url("/path?from=" + a + "&to=" + e),
                        Map.class
                );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(false, response.getBody().get("connected"));
    }

    @Test
    void shouldReturnSamePersonForZeroHops() {

        int a = createPerson("A");

        ResponseEntity<Map> response =
                restTemplate.getForEntity(
                        url("/path?from=" + a + "&to=" + a),
                        Map.class
                );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(true, response.getBody().get("connected"));
        assertEquals(0, response.getBody().get("hops"));
    }

    @Test
    void shouldPreserveUnicodeName() {

        ResponseEntity<Map> response =
                restTemplate.postForEntity(
                        url("/people"),
                        Map.of("name", "प्रशांत"),
                        Map.class
                );

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals("प्रशांत", response.getBody().get("name"));
    }

    @Test
    void shouldReturnBadRequestForEmptyName() {

        ResponseEntity<Map> response =
                restTemplate.postForEntity(
                        url("/people"),
                        Map.of("name", ""),
                        Map.class
                );

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    private int createPerson(String name) {

        ResponseEntity<Map> response =
                restTemplate.postForEntity(
                        url("/people"),
                        Map.of("name", name),
                        Map.class
                );

        assertEquals(HttpStatus.CREATED, response.getStatusCode());

        return (Integer) response.getBody().get("id");
    }

    private void connect(int a, int b) {

        ResponseEntity<Void> response =
                restTemplate.postForEntity(
                        url("/knows"),
                        Map.of("a", a, "b", b),
                        Void.class
                );

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    }
}