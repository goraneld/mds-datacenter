package com.mds.datacenter.repository;

import com.mds.datacenter.entity.Device;
import com.mds.datacenter.entity.Rack;
import com.mds.datacenter.exception.DataCenterException;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class RackRepository {

    private final Map<String, Rack> store = new ConcurrentHashMap<>();

    public Rack save(Rack rack) {
        if (store.containsKey(rack.getSerialNumber())) {
            throw DataCenterException.builder().status(500).type("RackCreateError").code("RACK_SAVE_UNIQUE").build();
        }
        rack.setDevices(new ArrayList<>());
        store.put(rack.getSerialNumber(), rack);

        return rack;
    }

    public Rack delete(String serialNumber) {
        Rack cRack = store.get(serialNumber);
        if (cRack == null) {
            throw DataCenterException.builder().status(404).type("RackDeleteError").code("RACK_DELETE_NOT_FOUND").build();
        }
        cRack.getDevices().forEach(device -> device.setRackSerialNumber(null));
        return store.remove(serialNumber);
    }

    public Rack update(Rack rack) {
        Rack cRack = store.get(rack.getSerialNumber());
        if (cRack == null) {
            throw DataCenterException.builder().status(404).type("RackUpdateError").code("RACK_UPDATE_NOT_FOUND").build();
        }
        rack.setDevices(cRack.getDevices());
        store.put(rack.getSerialNumber(), rack);

        return rack;
    }

    public Rack findById(String serialNumber) {
        return store.get(serialNumber);
    }

    public List<Rack> findAll() {
        return store.values().stream().toList();
    }

    public void addDevice(Device device) {
        if (device.getRackSerialNumber() != null) {
            Rack rack = findById(device.getRackSerialNumber());
            if (rack != null) {
                int[] sums = rack.getDevices().stream().collect(
                        () -> new int[2],
                        (a, d) -> {
                            a[0]+= d.getUnits();
                            a[1]+= d.getPower();
                        },
                        (a1, a2) -> {
                            a1[0] += a2[0];
                            a1[1] += a2[1];
                        });
                if (device.getUnits() + sums[0] > rack.getUnits() || device.getPower() + sums[1] > rack.getMaxPower()) {
                    device.setRackSerialNumber(null);
                } else {
                    rack.getDevices().add(device);
                }
            }
        }
    }

    public void removeDevice(Device device) {
        if (device.getRackSerialNumber() != null) {
            Rack rack = findById(device.getRackSerialNumber());
            if (rack != null) {
                rack.getDevices().removeIf(d -> d.getSerialNumber().equals(device.getSerialNumber()));
            }
        }
    }

    public void moveDevice(Device cDevice, Device device) {
        if (cDevice.getRackSerialNumber() == null) {
            addDevice(device);
        } else if (device.getSerialNumber() == null) {
            removeDevice(cDevice);
        } else if (!cDevice.getRackSerialNumber().equals(device.getSerialNumber())) {
            removeDevice(cDevice);
            addDevice(device);
        }
    }
}
