package com.guardant.so2c.ocr.textextractor.model.outbound;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/*
 * @author hsahu
 * @date 17/08/21,1:00 PM
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PatientAuthorization {
    @JsonProperty("PatientAuthName")
    private String patientAuthName;

    @JsonProperty("PatientAuthDate")
    private String patientAuthDate;

    @JsonProperty("PatientSignaturePresent")
    private boolean patientSignaturePresent;

}
