package com.guardant.so2c.ocr.textextractor.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
/*
 * @author hsahu
 * @date 27/07/21,2:58 PM
 */
@Setter
@Getter
@Builder
@ToString
public class ErrorRecord {

    @JsonProperty("Efax Message")
    private InboundMessage inboundMessage;
    @JsonProperty("Error Desc")
    private String description;
}