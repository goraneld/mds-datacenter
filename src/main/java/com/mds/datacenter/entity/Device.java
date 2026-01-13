package com.mds.datacenter.entity;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Device {
    @NotBlank(message = "Serial number is required")
    private String serialNumber;
    @NotBlank(message = "Name is required")
    private String name;
    private String description;
    @Min(value = 1, message = "Units must be greater than 0")
    private Integer units;
    @Min(value = 1, message = "Power must be greater than 0")
    private Integer power;
    private String rackSerialNumber;
}
