package com.mds.datacenter.service;

import com.mds.datacenter.entity.Rack;
import com.mds.datacenter.repository.RackRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class RackService {

    private final RackRepository rackRepository;

    public Rack addRack(Rack rack) {
        return rackRepository.save(rack);
    }

    public Rack updateRack(Rack rack) {
        return rackRepository.update(rack);
    }

    public Rack deleteRack(String serialNumber) {
        return rackRepository.delete(serialNumber);
    }

    public Rack getRack(String serialNumber) {
        return rackRepository.findById(serialNumber);
    }

    public List<Rack> getAllRacks() {
        return rackRepository.findAll();
    }

    public List<Rack> addRacks(List<Rack> racks) {
        rackRepository.clear();
        for (Rack rack : racks) {
            addRack(rack);
        }
        return getAllRacks();
    }
}
