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
public class Address {
  @JsonProperty("StreetAddress")
  private String streetAddress;

  @JsonProperty("City")
  private String city;

  @JsonProperty("State")
  private String state;

  @JsonProperty("County")
  private String county;

  @JsonProperty("Country")
  private String country;

  @JsonProperty("Zip")
  private String zip;

  @JsonIgnore
  public boolean isMandatoryFieldEmpty() {
    return CommonUtility.isNullOrEmpty(streetAddress) &&
            CommonUtility.isNullOrEmpty(city) &&
            CommonUtility.isNullOrEmpty(zip);
  }
}
