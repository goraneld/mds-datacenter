package com.mds.datacenter.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mds.datacenter.entity.Device;
import com.mds.datacenter.entity.Rack;
import com.mds.datacenter.repository.DeviceRepository;
import com.mds.datacenter.repository.RackRepository;
import lombok.AllArgsConstructor;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
public class DataCenterManagementService {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final DeviceRepository deviceRepository;
    private final RackRepository rackRepository;
    private static Double minDelta;
    private static List<Rack> optimalRacks;

    public List<Rack> importData(String index) {
        try {
            deviceRepository.clear();
            rackRepository.clear();

//            for(int i=0; i < 1000; i++) {
//                deviceRepository.save(new Device("d_" + i, "device " + i, "", (int) (3.0 * Math.random()), 500 + (int)(300 * Math.random()), null));
//            }
//
//            for(int i=0; i < 100; i++) {
//                rackRepository.save(new Rack("r_" + i, "rack " + i, "", (int) (12 + 52 * Math.random()), 5000 + (int)(20000 * Math.random()), new ArrayList<>(), 0));
//            }

            InputStream inputStream =
                    new ClassPathResource("test-files/racks_" + index + ".json").getInputStream();

            objectMapper.readValue(
                    inputStream,
                    new TypeReference<List<Rack>>() {}
            ).forEach(rackRepository::save);

            inputStream =
                    new ClassPathResource("test-files/devices_" + index + ".json").getInputStream();

            objectMapper.readValue(
                    inputStream,
                    new TypeReference<List<Device>>() {}
            ).forEach(deviceRepository::save);
        } catch (Exception e) {
            throw new RuntimeException("Failed to load.", e);
        }

        return rackRepository.findAll();
    }

    private int[] getRackSums(Rack rack) {
        return rack.getDevices().stream().collect(
                () -> new int[2],
                (a, d) -> {
                    a[0]+= d.getUnits();
                    a[1]+= d.getPower();
                },
                (a1, a2) -> {
                    a1[0] += a2[0];
                    a1[1] += a2[1];
                });
    }

    private void nextDevice(
            int index,
            List<Device> devices,
            List<Rack> racks,
            List<String> current) {

        if (index == devices.size()) {
//            count++;
            // Check out max and min consumption percentage and clear out devices

            StringBuilder sb = new StringBuilder();
            double minPercent = Double.MAX_VALUE;
            double maxPercent = -1;
            for (Rack rack : racks) {
                int[] sums = getRackSums(rack);
                double powerPercent = Math.round(10000 * sums[1] / (double)rack.getMaxPower()) / 100d;
                if (powerPercent < minPercent) {
                    minPercent = powerPercent;
                }
                if (powerPercent > maxPercent) {
                    maxPercent = powerPercent;
                }
                sb.append("[");
                sb.append(powerPercent);
                sb.append("]");
            }
            if (optimalRacks == null || minDelta > maxPercent - minPercent) {
                optimalRacks = cloneRacks(racks);
                minDelta = maxPercent - minPercent;
            }

            return;
        }

        for (Rack rack : racks) {
            int[] sums = getRackSums(rack);
            Device device = devices.get(index);
            // Check if we can put device in rack - comparing unit and power sums
            if (device.getUnits() + sums[0] > rack.getUnits() || device.getPower() + sums[1] > rack.getMaxPower()) {
                continue;
            }
            current.add(devices.get(index).getName() + " in " + rack.getName());
            rack.getDevices().add(device);
            nextDevice(index + 1, devices, racks, current);
            rack.getDevices().removeLast();
            current.removeLast();
        }
    }

    private List<Rack> cloneRacks(List<Rack> racks) {
        List<Rack> newRacks = new ArrayList<>();
        for (Rack rack : racks) {
            List<Device> newDevices = new ArrayList<>();
            int usedPower = 0;
            for(Device device : rack.getDevices()) {
                usedPower+= device.getPower();
                newDevices.add(new Device(device.getSerialNumber(), device.getName(), device.getDescription(), device.getUnits(), device.getPower(), device.getRackSerialNumber()));
            }
            Rack newRack = new Rack(rack.getSerialNumber(), rack.getName(), rack.getDescription(), rack.getUnits(), rack.getMaxPower(), newDevices, usedPower);
            newRacks.add(newRack);
        }

        return newRacks;
    }

    private void printRacksPowerPercent(List<Rack> racks) {
        StringBuilder sb = new StringBuilder();
        double minPercent = Double.MAX_VALUE;
        double maxPercent = -1;
        for (Rack rack : racks) {
            int[] sums = getRackSums(rack);
            double powerPercent = Math.round(10000 * sums[1] / (double)rack.getMaxPower()) / 100d;
            if (powerPercent < minPercent) {
                minPercent = powerPercent;
            }
            if (powerPercent > maxPercent) {
                maxPercent = powerPercent;
            }
            sb.append("[");
            sb.append(powerPercent);
            sb.append("]");
        }
        System.out.println(sb + " " + (maxPercent - minPercent));
    }

    // This method is used for full combinations calculation, so I can check out optimized algorithm
    public List<Rack> getLayoutCombinations(String index) {
        if (index != null) {
            importData(index);
        }
        List<Device> devices = deviceRepository.findAll();
        List<Rack> racks = rackRepository.findAll();

        racks = racks.stream().sorted((Rack rack1, Rack rack2) -> {
            if (rack1.getMaxPower().intValue() != rack2.getMaxPower().intValue()) {
                return rack2.getMaxPower() - rack1.getMaxPower();
            } else {
                return rack2.getUnits() - rack1.getUnits();
            }
        }).toList();

        devices = new ArrayList<>(devices.stream().sorted((Device device1, Device device2) -> {
            if (device1.getPower().intValue() != device2.getPower().intValue()) {
                return device2.getPower() - device1.getPower();
            } else {
                return device2.getUnits() - device1.getUnits();
            }
        }).toList());

        optimalRacks = null;
        nextDevice(0, devices, racks, new ArrayList<>());
        // number of combinations = number of racks ^ number of devices
        printRacksPowerPercent(optimalRacks);

        return optimalRacks;
    }

    private List<Device> optimalList(List<Device> allDevices, Rack rack, List<Device> currentDevices, double limit, int index) {
        while (index < allDevices.size() && currentDevices.stream().mapToDouble(Device::getPower).sum() < limit) {
            Device nextDevice = allDevices.get(index);
            if (rack.getUnits() >= currentDevices.stream().mapToInt(Device::getUnits).sum() + nextDevice.getUnits()) {
                currentDevices.add(nextDevice);
            }
            index++;
        }
        if (index < allDevices.size() && currentDevices.stream().mapToDouble(Device::getPower).sum() > limit) {
            currentDevices.removeLast();
            return optimalList(allDevices, rack, currentDevices, limit, index);
        }
        return currentDevices;
    }

    public List<Rack> getLayout(String index) {
        if (index != null) {
            importData(index);
        }
        List<Device> devices = deviceRepository.findAll();
        List<Rack> racks = rackRepository.findAll();

        double sumMaxPower = racks.stream().mapToDouble(Rack::getMaxPower).sum();
        double sumPower = devices.stream().mapToDouble(Device::getPower).sum();
        double mediumPower = sumPower / sumMaxPower;
        racks = racks.stream().sorted((Rack rack1, Rack rack2) -> {
            if (rack1.getMaxPower().intValue() != rack2.getMaxPower().intValue()) {
                return rack2.getMaxPower() - rack1.getMaxPower();
            } else {
                return rack2.getUnits() - rack1.getUnits();
            }
//            if (rack1.getMaxPower().intValue() != rack2.getMaxPower().intValue()) {
//                return rack1.getMaxPower() - rack2.getMaxPower();
//            } else {
//                return rack1.getUnits() - rack2.getUnits();
//            }
        }).toList();

        devices = new ArrayList<>(devices.stream().sorted((Device device1, Device device2) -> {
            if (device1.getPower().intValue() != device2.getPower().intValue()) {
                return device2.getPower() - device1.getPower();
            } else {
                return device2.getUnits() - device1.getUnits();
            }
//            if (device1.getPower().intValue() != device2.getPower().intValue()) {
//                return device1.getPower() - device2.getPower();
//            } else {
//                return device1.getUnits() - device2.getUnits();
//            }
        }).toList());

        for(Rack rack : racks) {
            double maxPower = rack.getMaxPower();
            double limit = mediumPower * maxPower;
            List<Device> chosenDevices = optimalList(devices, rack, new ArrayList<>(), limit, 0);
            double sumPower1 = chosenDevices.subList(0, chosenDevices.size()-1).stream().mapToDouble(Device::getPower).sum();
            double sumPower2 = chosenDevices.stream().mapToDouble(Device::getPower).sum();
            if (Math.abs(sumPower2 - limit) > Math.abs(sumPower1 - limit)) {
                devices.removeAll(chosenDevices.subList(0, chosenDevices.size()-1));
                rack.setDevices(chosenDevices.subList(0, chosenDevices.size()-1));
            } else {
                devices.removeAll(chosenDevices);
                rack.setDevices(chosenDevices);
            }
        }

        printRacksPowerPercent(racks);
        return racks;//rackRepository.findAll();
    }
}
