package com.mds.datacenter.exception;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DataCenterException extends RuntimeException {

    private String type;
    private int status;
    private String code;

}
