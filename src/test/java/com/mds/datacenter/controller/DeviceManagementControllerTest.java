package com.mds.datacenter.controller;

import com.mds.datacenter.entity.Rack;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureRestTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.test.web.servlet.client.EntityExchangeResult;
import org.springframework.test.web.servlet.client.RestTestClient;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureRestTestClient
class DeviceManagementControllerTest {

    @LocalServerPort
    private int port;

    @Autowired
    private RestTestClient restTestClient;

    @Test
    void checkIfImportWorksOk() {
        EntityExchangeResult<List<Rack>> response = restTestClient.get()
                .uri("http://localhost:" + port + "/api/management/import/1")
                .exchange()
                .expectStatus().isOk()
                .expectBody(new ParameterizedTypeReference<List<Rack>>() {
                })
                .returnResult();

        assertNotNull(response.getResponseBody());
        assertEquals(3, response.getResponseBody().size());
    }

    // Comparing full combinations with optimized
    @Test
    void testLayout1() {
        EntityExchangeResult<List<Rack>> response = restTestClient.get()
                .uri("http://localhost:" + port + "/api/management/layout-combinations/1")
                .exchange()
                .expectStatus().isOk()
                .expectBody(new ParameterizedTypeReference<List<Rack>>() {
                })
                .returnResult();

        List<Rack> racks1 = response.getResponseBody();
        assertNotNull(racks1);

        EntityExchangeResult<List<Rack>> responseOptimized = restTestClient.get()
                .uri("http://localhost:" + port + "/api/management/layout/1")
                .exchange()
                .expectStatus().isOk()
                .expectBody(new ParameterizedTypeReference<List<Rack>>() {
                })
                .returnResult();

        List<Rack> racks2 = responseOptimized.getResponseBody();
        assertNotNull(racks2);

        assertEquals(racks1.size(), racks2.size());
        for(int i = 0; i < racks1.size(); i++) {
            assertEquals(racks1.get(i).getDevices().size(), racks2.get(i).getDevices().size());
            for(int j = 0; j < racks1.get(i).getDevices().size(); j++) {
                assertEquals(racks1.get(i).getDevices().get(j).getSerialNumber(), racks2.get(i).getDevices().get(j).getSerialNumber());
            }
        }

    }
}
