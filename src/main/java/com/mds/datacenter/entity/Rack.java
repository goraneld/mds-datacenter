package com.mds.datacenter.entity;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Rack {
    @NotBlank(message = "Serial number is required")
    private String serialNumber;
    @NotBlank(message = "Name is required")
    private String name;
    private String description;
    @NotNull(message = "Units is required")
    @Min(value = 1, message = "Units must be greater than 0")
    private Integer units;
    @NotNull(message = "Max power is required")
    @Min(value = 1, message = "Max power must be greater than 0")
    private Integer maxPower;
    private List<Device> devices;
}
