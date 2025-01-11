package com.guardant.so2c.ocr.textextractor.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class InboundMessage {

   @JsonProperty("S3_Bucket_Location")
   private String s3BucketLocation;
   @JsonInclude(JsonInclude.Include.NON_NULL)
   @JsonProperty("Type_Of_Document")
   private String typeOfDocument;
   @JsonInclude(JsonInclude.Include.NON_NULL)
   @JsonProperty("DocumentMode")
   private String documentMode;
   @JsonProperty("Fax_Track_Unique_Id")
   private String faxTrackUniqueId;
}
