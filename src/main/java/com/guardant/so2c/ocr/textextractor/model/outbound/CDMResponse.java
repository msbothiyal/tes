package com.guardant.so2c.ocr.textextractor.model.outbound;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
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
public class CDMResponse {
  @JsonInclude(JsonInclude.Include.NON_NULL)
  @JsonProperty("Order")
  private Order order;
  @JsonIgnore
  public boolean isPatientInfoAbsent() {
    return order == null || order.isPatientInfoAbsent();
  }
}
