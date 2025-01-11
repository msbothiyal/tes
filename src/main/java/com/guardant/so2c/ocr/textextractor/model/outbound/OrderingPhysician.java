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
public class OrderingPhysician {
  @JsonProperty("Identification")
  private Identity identification;

  @JsonProperty("LastName")
  private String lastName;

  @JsonProperty("FirstName")
  private String firstName;

  @JsonProperty("FacilityName")
  private String facilityName;

  @JsonProperty("FacilityAcctNumber")
  private String facilityAcctNumber;

  @JsonProperty("ProviderAddress")
  private Address providerAddress;

  @JsonProperty("ProviderContactInfo")
  private ProviderContactInfo providerContactInfo;
}
