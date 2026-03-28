package com.logistics.platform;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class ValidationTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    void createOrder_withOversizedExternalOrderId_returns400() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        String orgBody = "{\"name\":\"ValTestOrg_" + System.nanoTime() + "\"}";
        ResponseEntity<String> orgResp = restTemplate.postForEntity("/organizations",
            new HttpEntity<>(orgBody, headers), String.class);
        String orgId = extractId(orgResp.getBody());

        String siteBody = "{\"code\":\"VT1\",\"name\":\"Val Site\",\"platform\":\"SHOPIFY\"}";
        ResponseEntity<String> siteResp = restTemplate.postForEntity(
            "/organizations/" + orgId + "/websites",
            new HttpEntity<>(siteBody, headers), String.class);
        String siteId = extractId(siteResp.getBody());

        String longId = "x".repeat(200);
        String orderBody = "{\"orgId\":\"" + orgId + "\",\"websiteId\":\"" + siteId + "\",\"externalOrderId\":\"" + longId + "\"}";
        ResponseEntity<String> resp = restTemplate.postForEntity("/orders",
            new HttpEntity<>(orderBody, headers), String.class);
        assertEquals(HttpStatus.BAD_REQUEST, resp.getStatusCode());
    }

    @Test
    void putOrder_withoutRequiredStatus_returns400() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        String orgBody = "{\"name\":\"ValTestOrg2_" + System.nanoTime() + "\"}";
        ResponseEntity<String> orgResp = restTemplate.postForEntity("/organizations",
            new HttpEntity<>(orgBody, headers), String.class);
        String orgId = extractId(orgResp.getBody());

        String siteBody = "{\"code\":\"VT2\",\"name\":\"Val Site 2\",\"platform\":\"SHOPIFY\"}";
        ResponseEntity<String> siteResp = restTemplate.postForEntity(
            "/organizations/" + orgId + "/websites",
            new HttpEntity<>(siteBody, headers), String.class);
        String siteId = extractId(siteResp.getBody());

        String createBody = "{\"orgId\":\"" + orgId + "\",\"websiteId\":\"" + siteId + "\",\"externalOrderId\":\"EXT-VAL\",\"status\":\"CREATED\",\"financialStatus\":\"PAID\",\"fulfillmentStatus\":\"UNFULFILLED\",\"orderTotal\":10.00}";
        ResponseEntity<String> createResp = restTemplate.postForEntity("/orders",
            new HttpEntity<>(createBody, headers), String.class);
        String orderId = extractId(createResp.getBody());

        String putBody = "{\"orgId\":\"" + orgId + "\",\"websiteId\":\"" + siteId + "\",\"externalOrderId\":\"EXT-VAL\"}";
        ResponseEntity<String> resp = restTemplate.exchange("/orders/" + orderId,
            HttpMethod.PUT, new HttpEntity<>(putBody, headers), String.class);
        assertEquals(HttpStatus.BAD_REQUEST, resp.getStatusCode());
    }

    @Test
    void createFulfillment_withShipFromLocation_succeeds() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        String orgBody = "{\"name\":\"ValTestOrg3_" + System.nanoTime() + "\"}";
        ResponseEntity<String> orgResp = restTemplate.postForEntity("/organizations",
            new HttpEntity<>(orgBody, headers), String.class);
        String orgId = extractId(orgResp.getBody());

        String siteBody = "{\"code\":\"VT3\",\"name\":\"Val Site 3\",\"platform\":\"SHOPIFY\"}";
        ResponseEntity<String> siteResp = restTemplate.postForEntity(
            "/organizations/" + orgId + "/websites",
            new HttpEntity<>(siteBody, headers), String.class);
        String siteId = extractId(siteResp.getBody());

        String orderBody = "{\"orgId\":\"" + orgId + "\",\"websiteId\":\"" + siteId + "\",\"externalOrderId\":\"EXT-SFL\"}";
        ResponseEntity<String> orderResp = restTemplate.postForEntity("/orders",
            new HttpEntity<>(orderBody, headers), String.class);
        String orderId = extractId(orderResp.getBody());

        String fulfBody = "{\"externalFulfillmentId\":\"FULF-SFL\",\"shipFromLocation\":\"Warehouse A, Chicago IL\"}";
        ResponseEntity<String> resp = restTemplate.postForEntity(
            "/orders/" + orderId + "/fulfillments",
            new HttpEntity<>(fulfBody, headers), String.class);
        assertEquals(HttpStatus.CREATED, resp.getStatusCode());
        assertTrue(resp.getBody().contains("Warehouse A, Chicago IL"));
    }

    private String extractId(String json) {
        int idx = json.indexOf("\"id\":\"") + 6;
        return json.substring(idx, idx + 36);
    }
}
