package com.mds.datacenter.repository;

import com.mds.datacenter.entity.Device;
import com.mds.datacenter.entity.Device;
import com.mds.datacenter.exception.DataCenterException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Repository
@AllArgsConstructor
public class DeviceRepository {

    private final Map<String, Device> store = new ConcurrentHashMap<>();
    private final RackRepository rackRepository;

    public Device save(Device device) {
        if (store.containsKey(device.getSerialNumber())) {
            throw DataCenterException.builder().status(500).type("DeviceCreateError").code("DEVICE_SAVE_UNIQUE").build();
        }
        rackRepository.addDevice(device);
        store.put(device.getSerialNumber(), device);

        return device;
    }

    public Device delete(String serialNumber) {
        Device device = store.get(serialNumber);
        if (device == null) {
            throw DataCenterException.builder().status(404).type("DeviceDeleteError").code("DEVICE_DELETE_NOT_FOUND").build();
        }
        if (device.getRackSerialNumber() != null) {
            rackRepository.removeDevice(device);
        }
        return store.remove(serialNumber);
    }

    public Device update(Device device) {
        Device cDevice = store.get(device.getSerialNumber());
        if (cDevice == null) {
            throw DataCenterException.builder().status(404).type("DeviceUpdateError").code("DEVICE_UPDATE_NOT_FOUND").build();
        }
        rackRepository.moveDevice(cDevice, device);

        store.put(device.getSerialNumber(), device);

        return device;
    }

    public Device findById(String serialNumber) {
        return store.get(serialNumber);
    }

    public List<Device> findAll() {
        return store.values().stream().toList();
    }

    public void clear() {
        store.clear();
    }
}
