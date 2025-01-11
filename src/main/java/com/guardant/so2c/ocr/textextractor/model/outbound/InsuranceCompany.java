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
public class InsuranceCompany {
  @JsonProperty("InsuranceCompanyName")
  private String insuranceCompanyName;

  @JsonProperty("InsuranceCompanyId")
  private String insuranceCompanyId;

  @JsonProperty("InsuranceCompanyAddress")
  private String insuranceCompanyAddress;

  @JsonProperty("InsuranceCompanyState")
  private String insuranceCompanyState;

  @JsonProperty("InsuranceCompanyCity")
  private String insuranceCompanyCity;

  @JsonProperty("InsuranceCompanyZip")
  private String insuranceCompanyZip;

  @JsonProperty("InsuranceCompanyCountry")
  private String insuranceCompanyCountry;

  @JsonProperty("InsuranceCompanyPhone")
  private String insuranceCompanyPhone;
}
