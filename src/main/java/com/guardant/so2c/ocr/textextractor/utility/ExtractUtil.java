package com.guardant.so2c.ocr.textextractor.utility;

import com.amazonaws.services.textract.model.Block;
import com.amazonaws.services.textract.model.GetDocumentAnalysisResult;
import com.guardant.so2c.ocr.textextractor.constants.CommonConstants;
import com.guardant.so2c.ocr.textextractor.enums.DocType;
import com.guardant.so2c.ocr.textextractor.model.*;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.text.similarity.LevenshteinDistance;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.guardant.so2c.ocr.textextractor.constants.CommonConstants.PAGE_ONE;
import static com.guardant.so2c.ocr.textextractor.constants.CommonConstants.PAGE_TWO;

/*
 * Utility class for extraction
 * @author msbothiyal
 * @date 22/06/21,11:02 AM
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Slf4j
public final class ExtractUtil {

    /**
     * @param response extracted information from amazon textract
     * @param docType  type of document to be processed
     * @return extracted data
     */
    public static List<Section> extractDataFromDocument(GetDocumentAnalysisResult response, DocType docType) {

        List<Section> sectionList = new ArrayList<>();
        List<SubSection> subSecList = new ArrayList<>();
        var doc = new Document(response, docType);

        switch (docType) {
            case G360:
                extractedDataFromG360TRF(sectionList, subSecList, doc);
                break;
            case LUNAR_2:
                extractedDataFromLunar2TRF(sectionList, subSecList, doc);
                break;
            default:
                log.debug("FORM IS OF OTHER TYPE");
        }
        return sectionList;
    }

    /**
     * Extract data from guardant 360 form
     *
     * @param sectionList parent section element for response
     * @param subSecList  child section element for response
     * @param doc         document information from textract api
     */
    private static void extractedDataFromLunar2TRF(List<Section> sectionList, List<SubSection> subSecList,
                                                   Document doc) {
        String text;
        log.debug("Extracted data from Lunar2TRF");
        for (Page page : doc.getPages()) {
            for (Line line : page.getLines()) {
                text = line.getText();

                if (isLunar2Section(text)) {
                    subSecList = new ArrayList<>();
                    var sectionData =
                            Section.builder().sectionName(text).sectionNameConfidence(line.getConfidence())
                                    .subSections(subSecList).build();
                    sectionList.add(sectionData);
                } else {
                    var f = page.getForm().getFieldByKey(text, line);
                    if (f != null) {
                        subSecList.add(new SubSection(text, line.getConfidence(), getFieldValue(f.getValue()),
                                getFieldValueConfidence(f.getValue())));
                    }
                }
            }
        }
    }

    /**
     * Extract data from guardant 360 form
     *
     * @param sectionList parent section element for response
     * @param subSecList  child section element for response
     * @param doc         document information from textract api
     */
    private static void extractedDataFromG360TRF(List<Section> sectionList, List<SubSection> subSecList,
                                                 Document doc) {
        String text;
        log.debug("Extracted data from G360TRF");
        for (Page page : doc.getPages()) {
            for (Word word : page.getLinesInReadingOrder()) {
                text = word.getText();

                if (isG360Section(text)) {
                    subSecList = new ArrayList<>();
                    var sectionData =
                            Section.builder().sectionName(text).sectionNameConfidence(word.getConfidence())
                                    .subSections(subSecList).build();
                    sectionList.add(sectionData);
                    var field = page.getForm().getFieldByKey(text);
                    if (field != null) {
                        sectionData.setSectionValue(getFieldValue(field.getValue()));
                        sectionData.setSectionValueConfidence(getFieldValueConfidence(field.getValue()));
                        sectionData.setSubSections(null);
                    }
                } else {
                    var f = page.getForm().getFieldByKey(text);
                    if (f != null) {
                        subSecList.add(new SubSection(text, word.getConfidence(), getFieldValue(f.getValue()),
                                getFieldValueConfidence(f.getValue())));
                    }
                }
            }
        }
    }

    /**
     * @param value FieldValue
     * @return 0 if FieldValue is null else getConfidence
     */
    private static Float getFieldValueConfidence(FieldValue value) {
        return value == null ? 0f : value.getConfidence();
    }

    /**
     * @param value FieldValue
     * @return "" if FieldValue is null else text value
     */
    private static String getFieldValue(FieldValue value) {
        return value == null ? "" : value.toString();
    }

    /**
     * Identify if passed string matches G360 section condition
     *
     * @param input String information
     * @return T/F isG360Section
     */
    private static boolean isG360Section(String input) {
        var rt = false;
        if (input.length() > CommonConstants.EIGHT) {
            var subString = input.substring(0, 6);
            rt = subString.equalsIgnoreCase(subString) &&
                    Character.isDigit(input.charAt(0)) && subString.matches(CommonConstants.G360_SUBSECTION_REGEX);
        }
        return rt;
    }

    /**
     * Identify if passed string matches Lunar2 section condition
     *
     * @param input String information
     * @return T/F isLunar2Section
     */
    private static boolean isLunar2Section(String input) {
        return (!input.isBlank()) ? input.contains("INFORMATION") : Boolean.FALSE;
    }

    public static String getDocType(GetDocumentAnalysisResult response, List<String> lunar2TRFHeaderIdentifiers,
                                    List<String> lunar2TRFFooterIdentifiers, Integer trfIdentifiersLDThreshold,
                                    Integer trfMinBlockSize) {

        //filter TRF pages and remove non TRF pages
        var pageList = removeNonTRFPages(getPageBlockMap(response), lunar2TRFHeaderIdentifiers,
                lunar2TRFFooterIdentifiers, trfIdentifiersLDThreshold, trfMinBlockSize);
        final int size = pageList.size();
        //case of Non-TRF document or multi TRF
        if (CollectionUtils.isEmpty(pageList) || pageList.size() > 2) {
            return DocType.OTHER.name();
        } else {// check if page is page1 of TRF
            boolean isPageOneTRFMatched =
                    isTRFMatchedWithPageIdentifier(pageList.get(0), CommonConstants.FIRST_NAME.toUpperCase(),
                            trfIdentifiersLDThreshold);
            if (isPageOneTRFMatched) {
                return getDocumentType(response, trfIdentifiersLDThreshold, pageList, size);
            } else {
                // case where TRF page is ONLY second page
                if (size == PAGE_ONE) {
                    return DocType.OTHER.name();
                }
                //swap if first page of extracted data is not identified as first page of TRF
                Collections.swap(pageList, 0, 1);
                //validate for valid page 1 , page 2 of TRF
                if (isTRFMatchedWithPageIdentifier(pageList.get(0), CommonConstants.FIRST_NAME.toUpperCase(),
                        trfIdentifiersLDThreshold) &&
                        isTRFMatchedWithPageIdentifier(pageList.get(1), CommonConstants.PATIENT_MEDICAL_RECORD,
                                trfIdentifiersLDThreshold)) {
                    //response blocks is set for valid pages of TRF ONLY
                    response.setBlocks(CommonUtility.union(pageList.get(0), pageList.get(1)));
                    return DocType.LUNAR_2.docName;
                }

            }
        }
        return DocType.OTHER.name();
    }
    private static String getDocumentType(GetDocumentAnalysisResult response, Integer trfIdentifiersLDThreshold,
                                          List<List<Block>> pageList, int size) {
        if (size == PAGE_ONE) {
            //This is case of single page TRF document , response blocks is set for valid page TRF ONLY
            response.setBlocks(pageList.get(0));
            return DocType.LUNAR_2.docName;
        } //validate second page for valid page2 TRF
        else if (size == PAGE_TWO && isTRFMatchedWithPageIdentifier(pageList.get(1),
                CommonConstants.PATIENT_MEDICAL_RECORD, trfIdentifiersLDThreshold)) {
            //response blocks is set for valid pages of TRF ONLY
            response.setBlocks(CommonUtility.union(pageList.get(0), pageList.get(1)));
            return DocType.LUNAR_2.docName;
        }
        return DocType.OTHER.docName;
    }

    private static Map<Integer, List<Block>> getPageBlockMap(GetDocumentAnalysisResult response) {
        //group blocks of response by page
        return response.getBlocks().stream()
                .collect(Collectors.groupingBy(Block::getPage));
    }

    //check page blocks match for page identifiers
    private static boolean isTRFMatchedWithPageIdentifier(List<Block> pageBlocks, String pageIdentifier,
                                                          Integer trfIdentifiersLDThreshold) {
        return isIdentifierMatched(pageBlocks.stream().filter(f -> f.getBlockType().equals("LINE"))
                .map(Block::getText).filter(Objects::nonNull)
                .collect(Collectors.toList()), Stream.of(pageIdentifier), trfIdentifiersLDThreshold, 1);
    }

    private static List<List<Block>> removeNonTRFPages(Map<Integer, List<Block>> pageBlockMap,
                                                       List<String> lunar2TRFHeaderIdentifiers,
                                                       List<String> lunar2TRFFooterIdentifiers,
                                                       Integer trfIdentifiersLDThreshold,
                                                       Integer trfMinBlockSize) {
        return pageBlockMap.values().stream()
                .filter(pageList -> isTRFPage(pageList, lunar2TRFHeaderIdentifiers,
                        lunar2TRFFooterIdentifiers, trfIdentifiersLDThreshold, trfMinBlockSize))
                .collect(Collectors.toList());
    }

    private static boolean isTRFPage(List<Block> pageList, List<String> lunar2TRFHeaderIdentifiers,
                                     List<String> lunar2TRFFooterIdentifiers, Integer trfIdentifiersLDThreshold,
                                     Integer trfMinBlockSize) {
        //Valid TRF page will have block size of atleast trfMinBlockSize
        if (pageList.size() > trfMinBlockSize) {
            //Filter blocks which has block type as line and text as non null
            List<String> filterTextList = pageList.stream().filter(f -> f.getBlockType().equals("LINE"))
                    .map(Block::getText).filter(Objects::nonNull)
                    .collect(Collectors.toList());
            //Case 1 : Check for 'footer release number' match (if match is found , its valid TRF)
            // Filter last 20 elements to fetch footer release number
            // LD threshold set as trfIdentifiersLDThreshold to handle multiple versions of TRF like R3,R4,R5 etc
            // List of footer identifiers are sent if any match its identified as TRF
            if (isIdentifierMatched(
                    //Take last 20 blocks to check for footer match
                    filterTextList
                            .subList(filterTextList.size() - 20, filterTextList.size()),
                    lunar2TRFFooterIdentifiers.stream(), trfIdentifiersLDThreshold, 1)) {
                return true;
            } else //Case 2 : Check for header match for any 2 out of 3 with LD of 2
                return isIdentifierMatched(filterTextList, lunar2TRFHeaderIdentifiers.stream(),
                        trfIdentifiersLDThreshold, 2);
        }
        return false;
    }
    //TRF Identifiers logic
    private static boolean isIdentifierMatched(List<String> blockStream, Stream<String> identifiers,
                                               int trfIdentifiersLDThreshold, int minMatchCount) {

        //set threshold of LD algorithm to check similar match upto trfIdentifiersLDThreshold
        final var ld = new LevenshteinDistance(trfIdentifiersLDThreshold);
        long filterCount = identifiers.filter(getIdentifierPredicate(blockStream, ld)).count();
        return filterCount >= minMatchCount;
    }

    //filter first 30 elements ONLY
    private static Predicate<String> getIdentifierPredicate(List<String> blockStream, LevenshteinDistance ld) {
        return l -> blockStream.stream().limit(30).anyMatch(
                s -> ld.apply(s.toUpperCase(), l) >= 0
        );
    }
}
