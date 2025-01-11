package com.guardant.so2c.ocr.textextractor.model.outbound;

import com.fasterxml.jackson.annotation.JsonIgnore;
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
public class Order {
  @JsonProperty("Patient")
  private Patient patient;

  @JsonProperty("OrderInformation")
  private OrderInformation orderInformation;

  @JsonProperty("BillingInformation")
  private BillingInformation billingInformation;

  @JsonProperty("PatientAuthorization")
  private PatientAuthorization patientAuthorization;

  @JsonIgnore
  public boolean isPatientInfoAbsent() {
    return patient.isPatientInfoAbsent();
  }

  public Order(){
    this.patient = Patient.builder().build();
    this.orderInformation = new OrderInformation();
    this.billingInformation = BillingInformation.builder().build();
    this.patientAuthorization = PatientAuthorization.builder().build();
  }
}
