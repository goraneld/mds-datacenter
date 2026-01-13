package com.mds.datacenter.controller;

import com.mds.datacenter.entity.Device;
import com.mds.datacenter.service.DeviceService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/devices")
public class DeviceController {

    private final DeviceService deviceService;

    @PostMapping("/")
    public ResponseEntity<Device> addDevice(@Valid @RequestBody Device device) {
        return ResponseEntity.ok(deviceService.addDevice(device));
    }

    @PutMapping("/")
    public ResponseEntity<Device> updateDevice(@Valid @RequestBody Device device) {
        return ResponseEntity.ok(deviceService.updateDevice(device));
    }

    @DeleteMapping("/{serialNumber}")
    public ResponseEntity<Device> deleteDevice(@PathVariable String serialNumber) {
        return ResponseEntity.ok(deviceService.deleteDevice(serialNumber));
    }

    @GetMapping("/{serialNumber}")
    public ResponseEntity<Device> getDevice(@PathVariable String serialNumber) {
        return ResponseEntity.ok(deviceService.getDevice(serialNumber));
    }

    @GetMapping("/")
    public List<Device> getAllDevices() {
        return deviceService.getAllDevices();
    }
}
