package com.guardant.so2c.ocr.textextractor.model.outbound;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Objects;

/*
 * @author hsahu
 * @date 17/08/21,1:00 PM
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Demographics {
  @JsonProperty("PersonalInfo")
  private PersonalInfo personalInfo;

  @JsonProperty("Address")
  private Address address;

  @JsonProperty("ContactData")
  private ContactData contactData;

  @JsonIgnore
  public boolean isMandatoryFieldAbsent() {
    return (Objects.isNull(contactData) || contactData.isMandatoryFieldEmpty())
            && (Objects.isNull(personalInfo) || personalInfo.isMandatoryFieldEmpty()
            && (Objects.isNull(address) || address.isMandatoryFieldEmpty()));
  }
}
