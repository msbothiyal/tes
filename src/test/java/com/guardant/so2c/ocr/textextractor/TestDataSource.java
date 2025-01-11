package com.guardant.so2c.ocr.textextractor;

import com.amazonaws.services.textract.model.GetDocumentAnalysisResult;
import com.amazonaws.services.textract.model.StartDocumentAnalysisResult;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.guardant.so2c.ocr.textextractor.enums.DocType;
import com.guardant.so2c.ocr.textextractor.model.DocumentResponse;
import com.guardant.so2c.ocr.textextractor.model.ErrorRecord;
import com.guardant.so2c.ocr.textextractor.model.InboundMessage;
import com.guardant.so2c.ocr.textextractor.model.outbound.CDMResponse;
import com.guardant.so2c.ocr.textextractor.model.outbound.Identification;
import com.guardant.so2c.ocr.textextractor.model.outbound.Order;
import com.guardant.so2c.ocr.textextractor.model.outbound.Patient;
import com.guardant.so2c.ocr.textextractor.utility.ExtractUtil;
import com.guardant.so2c.ocr.textextractor.utility.JsonMapperUtil;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.io.IOException;

/*
 * TestDataSource class
 * @author hsahu
 * @date 25/08/21,11:02 AM
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class TestDataSource {

    public static GetDocumentAnalysisResult getDocumentAnalysisResult(String jsonFileName) throws IOException {

        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES, true);
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        final File file = ResourceUtils.getFile("src/test/resources/" + jsonFileName + ".json");
        return mapper.readValue(file, GetDocumentAnalysisResult.class);
    }

    public static String getInbound() {
        return "{\n" +
                "  \"S3_Bucket_Location\": \"https://so2c-o2r-sfdc-ocr-local.s3.us-west-2.amazonaws.com/input/v10-DF_TRF.pdf\",\n" +
                "  \"Type_Of_Document\": \"LUNAR2 TRF\",\n" +
                "  \"Fax_Track_Unique_Id\": \"1000001\",\n" +
                "  \"DocumentMode\": \"ZPaper\"\n" +
                "}";
    }

    public static InboundMessage getInboundDto() throws JsonProcessingException {
        return JsonMapperUtil.mapFromJson(getInbound(), InboundMessage.class);
    }

    public static String getInboundEmpty() {
        return "{\n" +
                "  \"S3_Bucket_Location\": \"\",\n" +
                "  \"Type_Of_Document\": \"\",\n" +
                "  \"Fax_Track_Unique_Id\": \"\",\n" +
                "  \"DocumentMode\": \"\"\n" +
                "}";
    }

    public static String getInvalidInboundMessage() {
        return "" +
                "  \"S3_Bucket_Location\": \"\",\n" +
                "  \"Type_Of_Document\": \"\",\n" +
                "  \"DocumentMode\": \"\"\n" +
                "}";
    }

    public static String getInvalidInboundMessageMissingTrackId() {
        return "{\n" +
                "  \"S3_Bucket_Location\": \"https://so2c-o2r-sfdc-ocr-local.s3.us-west-2.amazonaws.com/input/v10-DF_TRF.pdf\",\n" +
                "  \"Type_Of_Document\": \"LUNAR2 TRF\",\n" +
                "  \"Fax_Track_Unique_Id\": \"\",\n" +
                "  \"DocumentMode\": \"ZPaper\"\n" +
                "}";
    }

    public static String getInboundEmptyTypeOfDocument() {
        return "{\n" +
                "}";
    }

    public static ErrorRecord getDeadLetter() throws JsonProcessingException {

        return ErrorRecord.builder()
                .inboundMessage(getInboundDto())
                .description(getExceptionMessage(getInboundDto()))
                .build();
    }

    private static String getExceptionMessage(InboundMessage inboundMessage) {

        return inboundMessage.getS3BucketLocation();
    }

    @SneakyThrows
    public static DocumentResponse getDocumentResponse() {
        return new DocumentResponse(ExtractUtil
                .extractDataFromDocument(getDocumentAnalysisResult("apiResponseLunar2"), DocType.LUNAR_2),92);
    }

    public static CDMResponse getCDMResponse() {
        return CDMResponse.builder()
                .order(Order.builder().patient(Patient.builder().identification(Identification.builder()
                        .medicalRecordNumber("MR123456789").build()).build()).build())
                .build();
    }

    public static ErrorRecord getErrorRecord() throws JsonProcessingException {
        return ErrorRecord.builder()
                .inboundMessage(getInboundDto())
                .description(getExceptionMessage(getInboundDto()))
                .build();
    }

    public static String getCDMResponseString() {
        return " {\"Order\":{\"Patient\":{\"Identification\":{\"MedicalRecordNumber\":\"MRN5714496295\"},\"Demographics\":{\"PersonalInfo\":{\"LastName\":\"BWMMZL\",\"FirstName\":\"FFZSCHJ\",\"MiddleName\":null,\"DOB\":\"02/01/1962\",\"Sex\":\"Male\",\"SSN\":null,\"Race\":null,\"LanguagePref\":\"Spanish\"},\"Address\":{\"StreetAddress\":\"85438 Pachicho Dr\",\"City\":\"Boulder\",\"State\":\"AZ\",\"County\":null,\"Country\":\"US\",\"Zip\":\"79075\"},\"ContactData\":{\"Phone\":[{\"PhoneType\":\"Mobile\",\"PhoneNumber\":\"5762919467\"}],\"Email\":\"FFZSCHJ123@yopmail.com\",\"PatientContactConsent\":true}}},\"OrderInformation\":{\"OrderId\":null,\"OrderSource\":\"Fax\",\"TestRequested\":{\"TestName\":\"Lunar 2\",\"ResultDisclosure\":false,\"CancerType\":[{\"Name\":\"Shield LDT\"}],\"DiagnosisCode\":[]},\"TestOrderedDate\":\"07/10/2022\",\"OrderStatus\":\"New\",\"OrderingPhysician\":{\"Identification\":{\"NPI\":\"1235158254\"},\"LastName\":\"Swift\",\"FirstName\":\"Jodi\",\"FacilityName\":\"Evanston Hospital\",\"FacilityAcctNumber\":\"GHSA-00000031\",\"ProviderAddress\":{\"StreetAddress\":\"2650 Ridge Ave\",\"City\":\"Evanston\",\"State\":\"IL\",\"County\":null,\"Country\":\"US\",\"Zip\":\"60201\"},\"ProviderContactInfo\":{\"ProviderEmail\":\"jodi.swift@yopmail.com\",\"ProviderFax\":null,\"ProviderPhoneNumber\":\"\"}},\"SpecimenDetails\":{\"BCKId\":null,\"CollectionDate\":\"07/10/2022\",\"LabName\":\"Guardant\",\"ColSpecimenPerson\":\"Jack Heinz\",\"ColLocation\":\"\",\"OtherKitTubeId\":null}},\"BillingInformation\":{\"InsuredPersonalInfo\":{\"FirstName\":\"\",\"LastName\":\"\",\"MiddleName\":null,\"SSN\":null,\"DateOfBirth\":\"\",\"Gender\":null,\"PatientRelation\":\"\"},\"InsuredPolicyInfo\":{\"Plan\":{\"PlanId\":null,\"PlanIdType\":null,\"PlanType\":\"\",\"PlanName\":null},\"PolicyNumber\":\"PID33192174\",\"GroupNumber\":\"GID244221\",\"GroupName\":null,\"EffectiveDate\":null,\"ExpirationDate\":null,\"PriorAuthCode\":null},\"InsuranceCompany\":null,\"InsuredAddress\":null,\"BillingLocation\":{\"LocationType\":\"\"},\"SelfPay\":false},\"PatientAuthorization\":{\"PatientAuthName\":\"FFZSCHJ\",\"PatientAuthDate\":\"07/10/2022\",\"PatientSignaturePresent\":true}}}";
    }

    public static StartDocumentAnalysisResult getStartDocumentAnalysisResult() {
        var sar = new StartDocumentAnalysisResult();
        sar.setJobId("123456");
        return sar;
    }
}
