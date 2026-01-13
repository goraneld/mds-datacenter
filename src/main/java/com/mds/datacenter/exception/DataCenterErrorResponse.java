package com.mds.datacenter.exception;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class DataCenterErrorResponse {

    private LocalDateTime timestamp;
    private int status;
    private String type;
    private String code;

    public DataCenterErrorResponse(int status, String type, String code) {
        this.timestamp = LocalDateTime.now();
        this.status = status;
        this.type = type;
        this.code = code;
    }

}
