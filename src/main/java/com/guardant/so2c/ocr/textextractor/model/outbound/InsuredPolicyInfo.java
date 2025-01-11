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
public class InsuredPolicyInfo {
  @JsonProperty("Plan")
  private Plan plan;

  @JsonProperty("PolicyNumber")
  private String policyNumber;

  @JsonProperty("GroupNumber")
  private String groupNumber;

  @JsonProperty("GroupName")
  private String groupName;

  @JsonProperty("EffectiveDate")
  private String effectiveDate;

  @JsonProperty("ExpirationDate")
  private String expirationDate;

  @JsonProperty("PriorAuthCode")
  private String priorAuthCode;
}
