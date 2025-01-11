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
public class BillingInformation {
  @JsonProperty("InsuredPersonalInfo")
  private InsuredPersonalInfo insuredPersonalInfo;

  @JsonProperty("InsuredPolicyInfo")
  private InsuredPolicyInfo insuredPolicyInfo;

  @JsonProperty("InsuranceCompany")
  private InsuranceCompany insuranceCompany;

  @JsonProperty("InsuredAddress")
  private Address insuredAddress;

  @JsonProperty("BillingLocation")
  private BillingLocation billingLocation;

  @JsonProperty("SelfPay")
  @Builder.Default
  private boolean selfPay = true;
}
