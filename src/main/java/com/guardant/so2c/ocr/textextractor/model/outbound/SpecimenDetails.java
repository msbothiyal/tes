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
public class SpecimenDetails {
  @JsonProperty("BCKId")
  private String bCKId;

  @JsonProperty("CollectionDate")
  private String collectionDate;

  @JsonProperty("LabName")
  private String labName;

  @JsonProperty("ColSpecimenPerson")
  private String colSpecimenPerson;

  @JsonProperty("ColLocation")
  private String colLocation;

  @JsonProperty("OtherKitTubeId")
  private String otherKitTubeId;
}
