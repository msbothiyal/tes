package com.guardant.so2c.ocr.textextractor.model.outbound;

import com.fasterxml.jackson.annotation.JsonProperty;
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
public class TestRequested {
  @JsonProperty("TestName")
  private String testName;

  @JsonProperty("ResultDisclosure")
  private boolean resultDisclosure;

  @JsonProperty("CancerType")
  private List<CancerType> cancerType;

  @JsonProperty("DiagnosisCode")
  private List<DiagnosisCode> diagnosisCode;
}
