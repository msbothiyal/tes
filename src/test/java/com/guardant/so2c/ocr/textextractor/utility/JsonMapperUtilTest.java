package com.guardant.so2c.ocr.textextractor.utility;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.guardant.so2c.ocr.textextractor.TestDataSource;
import com.guardant.so2c.ocr.textextractor.constants.CommonConstants;
import com.guardant.so2c.ocr.textextractor.model.DocumentResponse;
import com.guardant.so2c.ocr.textextractor.model.InboundMessage;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/*
 * JsonMapperUtilTest class
 * @author hsahu
 * @date 25/08/21,11:02 AM
 */

class JsonMapperUtilTest {


    /**
     * mapFromJson test method, Testing from json to object type.
     */
    @Test
    void mapFromJson() throws JsonProcessingException {
        final var inbound = JsonMapperUtil.mapFromJson(TestDataSource.getInbound(), InboundMessage.class);
        assertNotNull(inbound);
        assertEquals("ZPaper", inbound.getDocumentMode());
    }

    /**
     * mapFromJsonJsonProcessingException test method, Testing Exception scenario.
     */
    @Test
    void mapFromJsonProcessingException() {

        final var jsonProcessingException = assertThrows(JsonProcessingException.class,
                () -> JsonMapperUtil.mapFromJson("", InboundMessage.class));
        assertEquals("MismatchedInputException", jsonProcessingException.getClass().getSimpleName());
    }

    @Test
    void convertToCDMFormat() {

        var documentResponse = TestDataSource.getDocumentResponse();
        documentResponse.setThreshold(92);
        final var cdmResponse = JsonMapperUtil.convertToCDMFormat(documentResponse);
        assertNotNull(cdmResponse);
        assertEquals("MR090807001", cdmResponse.getOrder().getPatient().getIdentification().getMedicalRecordNumber());
    }

    @Test
    void convertToCDMFormatNative() {
        final DocumentResponse documentResponse = getDocumentResponse();
        documentResponse.setThreshold(92);
        final var cdmResponse = JsonMapperUtil.convertToCDMFormat(documentResponse);
        assertNotNull(cdmResponse);
        assertEquals("MR090807001", cdmResponse.getOrder().getPatient().getIdentification().getMedicalRecordNumber());
    }

    @Test
    void convertToCDMFormatNativeForOther() {
        final DocumentResponse documentResponse = getDocumentResponseForOther();
        documentResponse.setThreshold(91);
        final var cdmResponse = JsonMapperUtil.convertToCDMFormat(documentResponse);
        assertNotNull(cdmResponse);
        assertEquals("MR090807001", cdmResponse.getOrder().getPatient().getIdentification().getMedicalRecordNumber());
    }

    private DocumentResponse getDocumentResponseForOther() {
        final var documentResponse = TestDataSource.getDocumentResponse();
        documentResponse.getSectionList().get(0).getSubSections().get(0).setKey(CommonConstants.F);
        documentResponse.getSectionList().get(0).getSubSections().get(1).setValueConfidence(70.0f);
        documentResponse.getSectionList().get(0).getSubSections().get(10).setValue(CommonConstants.SELECTED);
        documentResponse.getSectionList().get(0).getSubSections().get(11).setValue(CommonConstants.NOT_SELECTED);

        documentResponse.getSectionList().get(1).getSubSections().get(0).setValue(CommonConstants.SELECTED);
        documentResponse.getSectionList().get(1).getSubSections().get(1).setValue(CommonConstants.NOT_SELECTED);
        documentResponse.getSectionList().get(1).getSubSections().get(3).setValue(CommonConstants.NOT_SELECTED);
        documentResponse.getSectionList().get(1).getSubSections().get(4).setValue(CommonConstants.SELECTED);
        documentResponse.getSectionList().get(1).getSubSections().get(5).setValue(CommonConstants.SELECTED);
        documentResponse.getSectionList().get(1).getSubSections().get(1).setValue(CommonConstants.SELECTED);
        documentResponse.getSectionList().get(1).getSubSections().get(2).setValue(CommonConstants.SELECTED);
        documentResponse.getSectionList().get(1).getSubSections().get(10).setValue(CommonConstants.NOT_SELECTED);
        documentResponse.getSectionList().get(1).getSubSections().get(11).setValue(CommonConstants.SELECTED);
        documentResponse.getSectionList().get(1).getSubSections().get(12).setValue(CommonConstants.NOT_SELECTED);

        documentResponse.getSectionList().get(3).getSubSections().get(0).setValue(CommonConstants.SELECTED);
        documentResponse.getSectionList().get(3).getSubSections().get(1).setValue(CommonConstants.NOT_SELECTED);
        documentResponse.getSectionList().get(3).getSubSections().get(2).setValue(CommonConstants.SELECTED);
        return documentResponse;
    }

    private DocumentResponse getDocumentResponse() {
        final var documentResponse = TestDataSource.getDocumentResponse();
        documentResponse.getSectionList().get(0).getSubSections().get(11).setValue(CommonConstants.EMPTY);
        documentResponse.getSectionList().get(0).getSubSections().get(12).setValue(CommonConstants.EMPTY);
        documentResponse.getSectionList().get(0).getSubSections().get(0).setKey(CommonConstants.F);
        documentResponse.getSectionList().get(0).getSubSections().get(1).setValueConfidence(70.0f);
        documentResponse.getSectionList().get(0).getSubSections().get(0).setValue(CommonConstants.EMPTY);
        documentResponse.getSectionList().get(1).getSubSections().get(4).setValue(CommonConstants.NOT_SELECTED);
        documentResponse.getSectionList().get(1).getSubSections().get(5).setValue(CommonConstants.SELECTED);
        documentResponse.getSectionList().get(1).getSubSections().get(0).setValue(CommonConstants.EMPTY);
        documentResponse.getSectionList().get(1).getSubSections().get(1).setValue(CommonConstants.SELECTED);
        documentResponse.getSectionList().get(1).getSubSections().get(2).setValue(CommonConstants.SELECTED);
        documentResponse.getSectionList().get(1).getSubSections().get(8).setValue(CommonConstants.EMPTY);
        documentResponse.getSectionList().get(1).getSubSections().get(10).setValue(CommonConstants.EMPTY);
        documentResponse.getSectionList().get(1).getSubSections().get(11).setValue(CommonConstants.SELECTED);
        documentResponse.getSectionList().get(1).getSubSections().get(12).setValue(CommonConstants.NOT_SELECTED);
        documentResponse.getSectionList().get(1).getSubSections().get(3).setValue(CommonConstants.EMPTY);

        documentResponse.getSectionList().get(2).getSubSections().get(2).setValue(CommonConstants.EMPTY);
        documentResponse.getSectionList().get(2).getSubSections().get(8).setValue(CommonConstants.EMPTY);
        documentResponse.getSectionList().get(2).getSubSections().get(5).setValue(CommonConstants.EMPTY);


        documentResponse.getSectionList().get(3).getSubSections().get(0).setValue(CommonConstants.SELECTED);
        documentResponse.getSectionList().get(3).getSubSections().get(1).setValue(CommonConstants.NOT_SELECTED);
        documentResponse.getSectionList().get(4).getSubSections().get(8).setValue(CommonConstants.EMPTY);
        documentResponse.getSectionList().get(4).getSubSections().get(5).setValue(CommonConstants.EMPTY);
        documentResponse.getSectionList().get(4).getSubSections().get(9).setValue(CommonConstants.EMPTY);
        documentResponse.getSectionList().get(4).getSubSections().get(3).setValue(CommonConstants.EMPTY);
        return documentResponse;
    }




}