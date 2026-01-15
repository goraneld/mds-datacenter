package com.mds.datacenter.service;

import com.mds.datacenter.entity.Device;
import com.mds.datacenter.repository.DeviceRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class DeviceService {

    private final DeviceRepository deviceRepository;

    public Device addDevice(Device device) {
        return deviceRepository.save(device);
    }

    public Device updateDevice(Device device) {
        return deviceRepository.update(device);
    }

    public Device deleteDevice(String serialNumber) {
        return deviceRepository.delete(serialNumber);
    }

    public Device getDevice(String serialNumber) {
        return deviceRepository.findById(serialNumber);
    }

    public List<Device> getAllDevices() {
        return deviceRepository.findAll();
    }

    public List<Device> addDevices(List<Device> devices) {
        deviceRepository.clear();
        for (Device device : devices) {
            addDevice(device);
        }
        return getAllDevices();
    }
}
