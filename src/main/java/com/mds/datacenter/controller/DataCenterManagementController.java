package com.mds.datacenter.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mds.datacenter.entity.Device;
import com.mds.datacenter.entity.Rack;
import com.mds.datacenter.repository.DeviceRepository;
import com.mds.datacenter.repository.RackRepository;
import com.mds.datacenter.service.DataCenterManagementService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.InputStream;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/management")
public class DataCenterManagementController {

    private final DataCenterManagementService dataCenterManagementService;

    @GetMapping("/import/{index}")
    public ResponseEntity<List<Rack>> importData(@PathVariable String index) {

        return ResponseEntity.ok(dataCenterManagementService.importData(index));
    }

    @GetMapping("/layout-combinations/{index}")
    public ResponseEntity<List<Rack>> getLayoutCombinations(@PathVariable String index) {

        return ResponseEntity.ok(dataCenterManagementService.getLayoutCombinations(index));
    }

    @GetMapping("/layout/{index}")
    public ResponseEntity<List<Rack>> getLayout(@PathVariable String index) {

        return ResponseEntity.ok(dataCenterManagementService.getLayout(index));
    }
}
