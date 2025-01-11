package com.guardant.so2c.ocr.textextractor.model.outbound;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.guardant.so2c.ocr.textextractor.utility.CommonUtility;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/*
 * @author hsahu
 * @date 17/08/21,1:00 PM
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ContactData {
    @JsonProperty("Phone")
    private List<Phone> phone;

    @JsonProperty("Email")
    private String email;

    @JsonProperty("PatientContactConsent")
    private boolean patientContactConsent;

    @JsonIgnore
    public boolean isMandatoryFieldEmpty() {
        return CommonUtility.isNullOrEmpty(email);
    }
}
