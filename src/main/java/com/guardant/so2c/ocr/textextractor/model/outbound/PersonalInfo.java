package com.guardant.so2c.ocr.textextractor.model.outbound;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.guardant.so2c.ocr.textextractor.utility.CommonUtility;
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
public class PersonalInfo {
  @JsonProperty("LastName")
  private String lastName;

  @JsonProperty("FirstName")
  private String firstName;

  @JsonProperty("MiddleName")
  private String middleName;

  @JsonProperty("DOB")
  private String dob;

  @JsonProperty("Sex")
  private String sex;

  @JsonProperty("SSN")
  private String ssn;

  @JsonProperty("Race")
  private String race;

  @JsonProperty("LanguagePref")
  private String languagePref;

  @JsonIgnore
  public boolean isMandatoryFieldEmpty () {
    return CommonUtility.isNullOrEmpty(lastName) && CommonUtility.isNullOrEmpty(firstName) &&
            CommonUtility.isNullOrEmpty(dob);
  }
}
