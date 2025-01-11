package com.guardant.so2c.ocr.textextractor.enums;

import com.fasterxml.jackson.annotation.JsonValue;

public enum JobType {
    FAX_ORDER_INTAKE("Fax Order Intake"),
    PORTAL("Portal"),
    OTHER ("Other");


    @JsonValue
    private final String type;

    JobType(String type){ this.type = type; }

    public static JobType valueFrom(String label){
        for(JobType jobType: values()){
            if(jobType.type.equals(label)){
                return jobType;
            }
        }
        return null;
    }
}
