package com.guardant.so2c.ocr.textextractor.utility;

import com.guardant.so2c.ocr.textextractor.TestDataSource;
import com.guardant.so2c.ocr.textextractor.constants.CommonConstants;
import com.guardant.so2c.ocr.textextractor.enums.DocType;
import com.guardant.so2c.ocr.textextractor.model.Section;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.LinkedList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/*
 * ExtractUtilTest class
 * @author hsahu
 * @date 25/08/21,11:02 AM
 */
class ExtractUtilTest {

    Integer trfMinBlockSize = 500;
    Integer trfIdentifiersLDThreshold = 2;

    List<String> lunar2TRFHeaderIdentifiers =
            new LinkedList<>(List.of("PLEASE GO TO GUARDANTGO.COM", "SHIELD", "TEST REQUISITION FORM"));
    List<String> lunar2TRFFooterIdentifiers = new LinkedList<>(List.of("REC-REG-000431 R, MKT-000227 R"));

    /**
     * extractDataFromDocumentDocTypeG360 test method, Testing happy path scenario for DocTypeG360.
     *
     * @throws Exception
     */
    @Test
    void extractDataFromDocumentDocTypeG360() throws Exception {

        List<Section> sectionList = ExtractUtil.extractDataFromDocument(TestDataSource
                .getDocumentAnalysisResult("apiResponse360"), DocType.G360);
        assertNotNull(sectionList);
        assertEquals(10, sectionList.size());
    }

    /**
     * extractDataFromDocumentDocTypeLUNAR_2 test method, Testing path scenario for DocTypeLUNAR_2.
     *
     * @throws Exception
     */
    @Test
    void extractDataFromDocumentDocTypeLUNAR_2() throws Exception {

        List<Section> sectionList = ExtractUtil.extractDataFromDocument(TestDataSource
                .getDocumentAnalysisResult("apiResponseLunar2"), DocType.LUNAR_2);
        assertNotNull(sectionList);
        assertEquals(5, sectionList.size());
    }

    @Test
    @DisplayName("To check single page trf")
    void extractDataFromDocumentDocTypeLUNAR_2_Single_Page() throws Exception {
        var singleWithExtraPageResponse = ExtractUtil.getDocType(TestDataSource
                .getDocumentAnalysisResult("singleWithExtraPageResponse"),
                lunar2TRFHeaderIdentifiers, lunar2TRFFooterIdentifiers, trfIdentifiersLDThreshold, trfMinBlockSize);
        assertNotNull(singleWithExtraPageResponse);
        assertEquals(CommonConstants.LUNAR_2, singleWithExtraPageResponse);
    }
    @Test
    @DisplayName("To check single page trf with no footer match, 1 out of 3 identifiers match, return OTHER doc type")
    void extractDataFromDocumentDocTypeLUNAR_2_SinglePage_PartialIdentifiersNoMatch_NoFooterMatch_ReturnsOther()
            throws Exception {
        var singleWithExtraPageResponse = ExtractUtil.getDocType(TestDataSource
                .getDocumentAnalysisResult("singleWithExtraPageResponse_PartialIdentifiers1"),
                lunar2TRFHeaderIdentifiers, lunar2TRFFooterIdentifiers, trfIdentifiersLDThreshold, trfMinBlockSize);
        assertNotNull(singleWithExtraPageResponse);
        assertEquals(DocType.OTHER.docName, singleWithExtraPageResponse);
    }
    @Test
    @DisplayName("To check single page trf with no footer match, 2 out of 3 identifiers match, return LUNAR2 TRF doc type")
    void extractDataFromDocumentDocTypeLUNAR_2_SinglePage_PartialIdentifiers2Match_NoFooterMatch_ReturnsOther()
            throws Exception {
        var singleWithExtraPageResponse = ExtractUtil.getDocType(TestDataSource
                .getDocumentAnalysisResult("singleWithExtraPageResponse_PartialIdentifiers2"),
                lunar2TRFHeaderIdentifiers, lunar2TRFFooterIdentifiers, trfIdentifiersLDThreshold, trfMinBlockSize);
        assertNotNull(singleWithExtraPageResponse);
        assertEquals(CommonConstants.LUNAR_2, singleWithExtraPageResponse);
    }
    @Test
    @DisplayName("To check single page trf with footer match LD of 2, no identifiers match, return OTHER doc type")
    void extractDataFromDocumentDocTypeLUNAR_2_SinglePage_NoIdentifiersMatch_BadFooterMatch_ReturnsOther()
            throws Exception {
        var singleWithExtraPageResponse = ExtractUtil.getDocType(TestDataSource
                .getDocumentAnalysisResult("singleWithExtraPageResponse_PartialIdentifiers3"),
                lunar2TRFHeaderIdentifiers, lunar2TRFFooterIdentifiers, trfIdentifiersLDThreshold, trfMinBlockSize);
        assertNotNull(singleWithExtraPageResponse);
        assertEquals(DocType.OTHER.docName, singleWithExtraPageResponse);
    }
    @Test
    @DisplayName("To check multiple pages trf")
    void extractDataFromDocumentDocTypeLUNAR_2_Multiple_Page() throws Exception {
        var singleWithExtraPageResponse = ExtractUtil.getDocType(TestDataSource
                .getDocumentAnalysisResult("multiplePagesResponse"),
                lunar2TRFHeaderIdentifiers, lunar2TRFFooterIdentifiers, trfIdentifiersLDThreshold, trfMinBlockSize);
        assertNotNull(singleWithExtraPageResponse);
        assertEquals(DocType.OTHER.docName, singleWithExtraPageResponse);
    }

    @Test
    @DisplayName("To check single last page trf")
    void extractDataFromDocumentDocTypeOther_Single_Last_Page() throws Exception {
        var lastSinglePageTRF = ExtractUtil.getDocType(TestDataSource
                        .getDocumentAnalysisResult("lastSinglePageTRF"),
                lunar2TRFHeaderIdentifiers, lunar2TRFFooterIdentifiers, trfIdentifiersLDThreshold, trfMinBlockSize);
        assertNotNull(lastSinglePageTRF);
        assertEquals(DocType.OTHER.docName, lastSinglePageTRF);
    }
    @Test
    @DisplayName("To check valid reverse page trf")
    void extractDataFromDocumentDocTypeLUNAR_2_Single_Last_Page() throws Exception {
        var lastSinglePageTRF = ExtractUtil.getDocType(TestDataSource
                        .getDocumentAnalysisResult("validReverseTRF"),
                lunar2TRFHeaderIdentifiers, lunar2TRFFooterIdentifiers, trfIdentifiersLDThreshold, trfMinBlockSize);
        assertNotNull(lastSinglePageTRF);
        assertEquals(DocType.LUNAR_2.docName, lastSinglePageTRF);
    }

}