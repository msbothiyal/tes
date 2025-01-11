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
public class InsuredPersonalInfo {

  @JsonProperty("FirstName")
  private String firstName;

  @JsonProperty("LastName")
  private String lastName;

  @JsonProperty("MiddleName")
  private String middleName;

  @JsonProperty("SSN")
  private String sSN;

  @JsonProperty("DateOfBirth")
  private String dateOfBirth;

  @JsonProperty("Gender")
  private String gender;

  @JsonProperty("PatientRelation")
  private String patientRelation;
}
