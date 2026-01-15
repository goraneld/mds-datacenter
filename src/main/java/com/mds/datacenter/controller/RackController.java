package com.mds.datacenter.controller;

import com.mds.datacenter.entity.Device;
import com.mds.datacenter.entity.Rack;
import com.mds.datacenter.service.RackService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/racks")
public class RackController {

    private final RackService rackService;

    @PostMapping("/")
    public ResponseEntity<Rack> addRack(@Valid @RequestBody Rack rack) {
        return ResponseEntity.ok(rackService.addRack(rack));
    }

    @PutMapping("/")
    public ResponseEntity<Rack> updateRack(@Valid @RequestBody Rack rack) {
        return ResponseEntity.ok(rackService.updateRack(rack));
    }

    @DeleteMapping("/{serialNumber}")
    public ResponseEntity<Rack> deleteRack(@PathVariable String serialNumber) {
        return ResponseEntity.ok(rackService.deleteRack(serialNumber));
    }

    @GetMapping("/{serialNumber}")
    public ResponseEntity<Rack> getRack(@PathVariable String serialNumber) {
        return ResponseEntity.ok(rackService.getRack(serialNumber));
    }

    @GetMapping("/")
    public List<Rack> getAllRacks() {
        return rackService.getAllRacks().stream().peek(rack -> {
            // Adding sum of power used by devices - I would rather let FE calculate it
            rack.setUsedPower(rack.getDevices().stream().mapToInt(Device::getPower).sum());
        }).toList();
    }
}
