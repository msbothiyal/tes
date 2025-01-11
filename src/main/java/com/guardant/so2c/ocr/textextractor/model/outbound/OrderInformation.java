package com.guardant.so2c.ocr.textextractor.model.outbound;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

/*
 * @author hsahu
 * @date 17/08/21,1:00 PM
 */
@Data
@Builder
@AllArgsConstructor
public class OrderInformation {
  @JsonProperty("OrderId")
  private String orderId;

  @JsonProperty("OrderSource")
  private String orderSource;

  @JsonProperty("TestRequested")
  private TestRequested testRequested;

  @JsonProperty("TestOrderedDate")
  private String testOrderedDate;

  @JsonProperty("OrderStatus")
  private String orderStatus;

  @JsonProperty("OrderingPhysician")
  private OrderingPhysician orderingPhysician;

  @JsonProperty("SpecimenDetails")
  private SpecimenDetails specimenDetails;

  public OrderInformation() {
    this.orderingPhysician = OrderingPhysician.builder().build();
    this.specimenDetails = SpecimenDetails.builder().build();
  }
}
