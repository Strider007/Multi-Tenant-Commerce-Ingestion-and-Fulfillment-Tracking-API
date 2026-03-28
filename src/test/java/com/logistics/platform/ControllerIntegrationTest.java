package com.logistics.platform;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;

import com.logistics.platform.api.dto.ErrorResponse;
import com.logistics.platform.api.dto.OrderDto;
import com.logistics.platform.api.dto.OrganizationDto;
import com.logistics.platform.api.dto.WebsiteDto;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class ControllerIntegrationTest {

    @Autowired
    private TestRestTemplate restTemplate;

    private HttpHeaders jsonHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return headers;
    }

    private String extractId(String json) {
        int idx = json.indexOf("\"id\":\"") + 6;
        return json.substring(idx, idx + 36);
    }

    private String createOrg(long suffix) {
        String body = "{\"name\":\"CITOrg_" + suffix + "\",\"status\":\"ACTIVE\"}";
        ResponseEntity<String> resp = restTemplate.postForEntity(
                "/organizations",
                new HttpEntity<>(body, jsonHeaders()),
                String.class);
        assertEquals(HttpStatus.CREATED, resp.getStatusCode(),
                "Setup: createOrg failed – " + resp.getBody());
        return extractId(resp.getBody());
    }

    private String createWebsite(String orgId, String code) {
        String body = "{\"code\":\"" + code + "\",\"name\":\"CIT Site " + code
                + "\",\"platform\":\"SHOPIFY\"}";
        ResponseEntity<String> resp = restTemplate.postForEntity(
                "/organizations/" + orgId + "/websites",
                new HttpEntity<>(body, jsonHeaders()),
                String.class);
        assertEquals(HttpStatus.CREATED, resp.getStatusCode(),
                "Setup: createWebsite failed – " + resp.getBody());
        return extractId(resp.getBody());
    }

    @Test
    void createOrganization_returns201() {
        long ts = System.nanoTime();
        String body = "{\"name\":\"CIT_Org_Create_" + ts + "\",\"status\":\"ACTIVE\"}";
        ResponseEntity<OrganizationDto> resp = restTemplate.postForEntity(
                "/organizations",
                new HttpEntity<>(body, jsonHeaders()),
                OrganizationDto.class);

        assertEquals(HttpStatus.CREATED, resp.getStatusCode());
        assertNotNull(resp.getBody());
        assertNotNull(resp.getBody().getId());
        assertTrue(resp.getBody().getName().startsWith("CIT_Org_Create_"));
    }

    @Test
    void getOrganization_returns200() {
        long ts = System.nanoTime();
        String createBody = "{\"name\":\"CIT_Org_Get_" + ts + "\",\"status\":\"ACTIVE\"}";
        ResponseEntity<OrganizationDto> createResp = restTemplate.postForEntity(
                "/organizations",
                new HttpEntity<>(createBody, jsonHeaders()),
                OrganizationDto.class);
        assertEquals(HttpStatus.CREATED, createResp.getStatusCode());
        assertNotNull(createResp.getBody());
        String id = createResp.getBody().getId().toString();

        ResponseEntity<OrganizationDto> getResp = restTemplate.getForEntity(
                "/organizations/" + id,
                OrganizationDto.class);

        assertEquals(HttpStatus.OK, getResp.getStatusCode());
        assertNotNull(getResp.getBody());
        assertEquals(id, getResp.getBody().getId().toString());
        assertTrue(getResp.getBody().getName().startsWith("CIT_Org_Get_"));
    }

    @Test
    void listOrganizations_returns200() {
        ResponseEntity<String> resp = restTemplate.getForEntity(
                "/organizations?page=0&size=10",
                String.class);

        assertEquals(HttpStatus.OK, resp.getStatusCode());
        assertNotNull(resp.getBody());
        // PagedResponse should have a "data" field
        assertTrue(resp.getBody().contains("\"data\""),
                "Expected 'data' field in paged response, got: " + resp.getBody());
    }

    @Test
    void createWebsite_returns201() {
        long ts = System.nanoTime();
        String orgId = createOrg(ts);

        String siteBody = "{\"code\":\"CIT" + (ts % 100000) + "\",\"name\":\"CIT Site_" + ts
                + "\",\"platform\":\"SHOPIFY\",\"status\":\"ACTIVE\"}";
        ResponseEntity<WebsiteDto> resp = restTemplate.postForEntity(
                "/organizations/" + orgId + "/websites",
                new HttpEntity<>(siteBody, jsonHeaders()),
                WebsiteDto.class);

        assertEquals(HttpStatus.CREATED, resp.getStatusCode());
        assertNotNull(resp.getBody());
        assertNotNull(resp.getBody().getId());
        assertEquals(orgId, resp.getBody().getOrgId().toString());
    }

    @Test
    void getWebsite_returns200() {
        long ts = System.nanoTime();
        String orgId = createOrg(ts);

        // Use a code short enough to satisfy 2–100 char constraint
        String code = "GW" + (ts % 10000);
        String siteBody = "{\"code\":\"" + code + "\",\"name\":\"CIT Site Get\",\"platform\":\"MAGENTO\"}";
        ResponseEntity<WebsiteDto> createResp = restTemplate.postForEntity(
                "/organizations/" + orgId + "/websites",
                new HttpEntity<>(siteBody, jsonHeaders()),
                WebsiteDto.class);
        assertEquals(HttpStatus.CREATED, createResp.getStatusCode());
        assertNotNull(createResp.getBody());
        String siteId = createResp.getBody().getId().toString();

        ResponseEntity<WebsiteDto> getResp = restTemplate.getForEntity(
                "/organizations/" + orgId + "/websites/" + siteId,
                WebsiteDto.class);

        assertEquals(HttpStatus.OK, getResp.getStatusCode());
        assertNotNull(getResp.getBody());
        assertEquals(siteId, getResp.getBody().getId().toString());
        assertEquals(orgId, getResp.getBody().getOrgId().toString());
    }

    @Test
    void createOrder_returns201() {
        long ts = System.nanoTime();
        String orgId = createOrg(ts);
        String siteId = createWebsite(orgId, "CO" + (ts % 10000));

        String orderBody = "{"
                + "\"orgId\":\"" + orgId + "\","
                + "\"websiteId\":\"" + siteId + "\","
                + "\"externalOrderId\":\"EXT-CO-" + ts + "\","
                + "\"status\":\"CREATED\","
                + "\"financialStatus\":\"PAID\","
                + "\"fulfillmentStatus\":\"UNFULFILLED\","
                + "\"orderTotal\":99.95,"
                + "\"currency\":\"USD\""
                + "}";

        ResponseEntity<OrderDto> resp = restTemplate.postForEntity(
                "/orders",
                new HttpEntity<>(orderBody, jsonHeaders()),
                OrderDto.class);

        assertEquals(HttpStatus.CREATED, resp.getStatusCode());
        assertNotNull(resp.getBody());
        assertNotNull(resp.getBody().getId());
        assertEquals(orgId, resp.getBody().getOrgId().toString());
        assertEquals(siteId, resp.getBody().getWebsiteId().toString());
    }

    @Test
    void upsertOrder_returns200() {
        long ts = System.nanoTime();
        String orgId = createOrg(ts);
        String siteId = createWebsite(orgId, "UO" + (ts % 10000));
        String extOrderId = "EXT-UPSERT-" + ts;

        String orderBody = "{"
                + "\"orgId\":\"" + orgId + "\","
                + "\"websiteId\":\"" + siteId + "\","
                + "\"externalOrderId\":\"" + extOrderId + "\","
                + "\"status\":\"CREATED\""
                + "}";
        HttpEntity<String> entity = new HttpEntity<>(orderBody, jsonHeaders());

        ResponseEntity<OrderDto> firstResp = restTemplate.postForEntity(
                "/orders", entity, OrderDto.class);
        assertEquals(HttpStatus.CREATED, firstResp.getStatusCode(),
                "First upsert should be 201 CREATED");
        assertNotNull(firstResp.getBody());
        String firstId = firstResp.getBody().getId().toString();

        ResponseEntity<OrderDto> secondResp = restTemplate.postForEntity(
                "/orders", entity, OrderDto.class);
        assertEquals(HttpStatus.OK, secondResp.getStatusCode(),
                "Second upsert with same externalOrderId should be 200 OK");
        assertNotNull(secondResp.getBody());
        assertEquals(firstId, secondResp.getBody().getId().toString(),
                "Upsert should return the same order id");
    }

    @Test
    void getOrder_returns200() {
        long ts = System.nanoTime();
        String orgId = createOrg(ts);
        String siteId = createWebsite(orgId, "GO" + (ts % 10000));

        String orderBody = "{"
                + "\"orgId\":\"" + orgId + "\","
                + "\"websiteId\":\"" + siteId + "\","
                + "\"externalOrderId\":\"EXT-GET-" + ts + "\""
                + "}";
        ResponseEntity<OrderDto> createResp = restTemplate.postForEntity(
                "/orders",
                new HttpEntity<>(orderBody, jsonHeaders()),
                OrderDto.class);
        assertEquals(HttpStatus.CREATED, createResp.getStatusCode());
        assertNotNull(createResp.getBody());
        String orderId = createResp.getBody().getId().toString();

        ResponseEntity<OrderDto> getResp = restTemplate.getForEntity(
                "/orders/" + orderId + "?organizationId=" + orgId,
                OrderDto.class);

        assertEquals(HttpStatus.OK, getResp.getStatusCode());
        assertNotNull(getResp.getBody());
        assertEquals(orderId, getResp.getBody().getId().toString());
        assertEquals(orgId, getResp.getBody().getOrgId().toString());
    }

    @Test
    void createFulfillment_returns201() {
        long ts = System.nanoTime();
        String orgId = createOrg(ts);
        String siteId = createWebsite(orgId, "CF" + (ts % 10000));

        String orderBody = "{"
                + "\"orgId\":\"" + orgId + "\","
                + "\"websiteId\":\"" + siteId + "\","
                + "\"externalOrderId\":\"EXT-CF-" + ts + "\""
                + "}";
        ResponseEntity<String> orderResp = restTemplate.postForEntity(
                "/orders",
                new HttpEntity<>(orderBody, jsonHeaders()),
                String.class);
        assertEquals(HttpStatus.CREATED, orderResp.getStatusCode());
        String orderId = extractId(orderResp.getBody());

        String fulfBody = "{"
                + "\"externalFulfillmentId\":\"FULF-CF-" + ts + "\","
                + "\"carrier\":\"UPS\","
                + "\"status\":\"CREATED\""
                + "}";
        ResponseEntity<String> fulfResp = restTemplate.postForEntity(
                "/orders/" + orderId + "/fulfillments",
                new HttpEntity<>(fulfBody, jsonHeaders()),
                String.class);

        assertEquals(HttpStatus.CREATED, fulfResp.getStatusCode());
        assertNotNull(fulfResp.getBody());
        assertTrue(fulfResp.getBody().contains("\"id\""),
                "Expected id in fulfillment response, got: " + fulfResp.getBody());
    }

    @Test
    void createTracking_returns201() {
        long ts = System.nanoTime();
        String orgId = createOrg(ts);
        String siteId = createWebsite(orgId, "CT" + (ts % 10000));

        String orderBody = "{"
                + "\"orgId\":\"" + orgId + "\","
                + "\"websiteId\":\"" + siteId + "\","
                + "\"externalOrderId\":\"EXT-CT-" + ts + "\""
                + "}";
        ResponseEntity<String> orderResp = restTemplate.postForEntity(
                "/orders",
                new HttpEntity<>(orderBody, jsonHeaders()),
                String.class);
        assertEquals(HttpStatus.CREATED, orderResp.getStatusCode());
        String orderId = extractId(orderResp.getBody());

        String fulfBody = "{\"externalFulfillmentId\":\"FULF-CT-" + ts + "\"}";
        ResponseEntity<String> fulfResp = restTemplate.postForEntity(
                "/orders/" + orderId + "/fulfillments",
                new HttpEntity<>(fulfBody, jsonHeaders()),
                String.class);
        assertEquals(HttpStatus.CREATED, fulfResp.getStatusCode());
        String fulfillmentId = extractId(fulfResp.getBody());

        String trackBody = "{"
                + "\"trackingNumber\":\"TRK" + ts + "\","
                + "\"carrier\":\"FEDEX\","
                + "\"status\":\"IN_TRANSIT\""
                + "}";
        ResponseEntity<String> trackResp = restTemplate.postForEntity(
                "/fulfillments/" + fulfillmentId + "/tracking",
                new HttpEntity<>(trackBody, jsonHeaders()),
                String.class);

        assertEquals(HttpStatus.CREATED, trackResp.getStatusCode());
        assertNotNull(trackResp.getBody());
        assertTrue(trackResp.getBody().contains("\"id\""),
                "Expected id in tracking response, got: " + trackResp.getBody());
    }

    @Test
    void invalidUuid_returns400() {
        ResponseEntity<ErrorResponse> resp = restTemplate.getForEntity(
                "/organizations/not-a-uuid",
                ErrorResponse.class);

        assertEquals(HttpStatus.BAD_REQUEST, resp.getStatusCode());
    }

    @Test
    void sortFieldValidation_works() {
        ResponseEntity<String> resp = restTemplate.getForEntity(
                "/organizations?sort=name,asc&page=0&size=10",
                String.class);

        assertEquals(HttpStatus.OK, resp.getStatusCode());
        assertNotNull(resp.getBody());
        assertTrue(resp.getBody().contains("\"data\""),
                "Expected paged 'data' field in response, got: " + resp.getBody());
    }
}
