package com.logistics.platform;

import com.logistics.platform.api.dto.ErrorResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class ExceptionHandlingTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    void invalidDateParam_returns400() {
        ResponseEntity<ErrorResponse> response = restTemplate.getForEntity(
                "/organizations?from=not-a-date", ErrorResponse.class);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void malformedJson_returns400() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> entity = new HttpEntity<>("{invalid json", headers);
        ResponseEntity<ErrorResponse> response = restTemplate.postForEntity(
                "/organizations", entity, ErrorResponse.class);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void missingRequiredParam_returns400() {
        ResponseEntity<ErrorResponse> response = restTemplate.getForEntity(
                "/orders", ErrorResponse.class);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void invalidEnumValue_returns400() {
        ResponseEntity<ErrorResponse> response = restTemplate.getForEntity(
                "/organizations?status=BOGUS_STATUS", ErrorResponse.class);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void validationError_returns400WithFieldDetails() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> entity = new HttpEntity<>("{\"status\":\"ACTIVE\"}", headers);
        ResponseEntity<ErrorResponse> response = restTemplate.postForEntity(
                "/organizations", entity, ErrorResponse.class);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Validation failed", response.getBody().getMessage());
        assertNotNull(response.getBody().getErrors());
        assertTrue(response.getBody().getErrors().stream().anyMatch(e -> e.contains("name")));
    }

    @Test
    void notFound_returns404() {
        ResponseEntity<ErrorResponse> response = restTemplate.getForEntity(
                "/organizations/00000000-0000-0000-0000-000000000000", ErrorResponse.class);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void duplicateName_returns409() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        String body = "{\"name\":\"DuplicateTestOrg_" + System.nanoTime() + "\",\"status\":\"ACTIVE\"}";
        HttpEntity<String> entity = new HttpEntity<>(body, headers);
        restTemplate.postForEntity("/organizations", entity, String.class);
        ResponseEntity<ErrorResponse> response = restTemplate.postForEntity(
                "/organizations", entity, ErrorResponse.class);
        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
    }
}
