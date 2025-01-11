package com.guardant.so2c.ocr.textextractor.utility;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.guardant.so2c.ocr.textextractor.constants.CommonConstants;
import com.guardant.so2c.ocr.textextractor.constants.Lunar2TRFConstants;
import com.guardant.so2c.ocr.textextractor.model.DocumentResponse;
import com.guardant.so2c.ocr.textextractor.model.Section;
import com.guardant.so2c.ocr.textextractor.model.SubSection;
import com.guardant.so2c.ocr.textextractor.model.outbound.*;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.text.similarity.LevenshteinDistance;

import java.util.*;
import java.util.function.Predicate;

import static com.guardant.so2c.ocr.textextractor.constants.CommonConstants.*;
import static java.util.stream.Collectors.*;

/*
 * Utility class for JSON to object conversion
 * @author hsahu
 * @date 25/06/21,12:50 PM
 */
@Log4j2
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class JsonMapperUtil {

    /**
     * @param message as json string
     * @param clazz   output type
     * @return clazz output object
     * @throws JsonProcessingException exception
     */
    public static <T> T mapFromJson(String message, Class<T> clazz) throws JsonProcessingException {

        final var objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        return objectMapper.readValue(message, clazz);
    }

    /**
     * @param t as object any input
     * @return json string
     * @throws JsonProcessingException exception
     */
    public static <T> String mapToJson(T t) throws JsonProcessingException {

        final var objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        return objectMapper.writeValueAsString(t);
    }

    /**
     * @param t as object any input
     * @return map object key value pair
     * @throws JsonProcessingException exception
     */
    public static <T> Map<String, Object> jsonToMap(T t) throws JsonProcessingException {

        final var mapper = new ObjectMapper();
        TypeReference<Map<String, Object>> token = new TypeReference<>() {
        };
        return mapper.readValue(mapToJson(t), token);
    }
    /**
     * @param response take as input
     * @return CDMResponse
     */
    public static CDMResponse convertToCDMFormat(DocumentResponse response) {
        final SubSection signature = response.getSectionList().stream().flatMap(section -> section.getSubSections().stream())
                .filter(subSection -> subSection.getKey().equals(MEDICAL_PROFESSIONAL_SIGNATURE))
                .findFirst().orElse(null);
        final var order = new Order();
        final var cdmResponse = CDMResponse.builder().order(order).build();
        //set threshold of 5 to check similar match upto 5
        var ld = new LevenshteinDistance(5);

        for (Section section : response.getSectionList()) {
            if (ld.apply(section.getSectionName(), Lunar2TRFConstants.PATIENT_INFORMATION) >= 0) {
                mapToPatient(section.getSubSections(), order, signature, response.getThreshold());
                log.debug("Mapped Patient Section");
            } else if (section.getSectionName().toUpperCase().contains(Lunar2TRFConstants.INSURANCE)) {
                // TODO This section is added with special contains check as the section name is fetched as complete
                // line and LD algorithm cannot be applied directly. Need to rethink of section identification logic
                mapToBillingInformation(section.getSubSections(), order);
                log.debug("Mapped Billing Section");
            } else if (ld.apply(section.getSectionName(), Lunar2TRFConstants.PROVIDER_INFORMATION) >= 0) {
                mapToOrderingPhysician(section.getSubSections(), order,response.getThreshold());
                log.debug("Mapped Provider Section");
            } else if (ld.apply(section.getSectionName(), Lunar2TRFConstants.ORDER_INFORMATION) >= 0) {
                mapToOrderInformation(section.getSubSections(), order);
                log.debug("Mapped Order Section");
            } else if (ld.apply(section.getSectionName(), Lunar2TRFConstants.BLOOD_SPECIMEN_INFORMATION) >= 0) {
                mapToSpecimenInformation(section.getSubSections(), order);
                log.debug("Mapped Blood specimen Section");
            }
        }
        return cdmResponse;
    }

    private static String checkSignature(Map<String, List<String>> keyValues) {
        return getValue(keyValues, CommonConstants.PATIENT_SIGNATURE);
    }

    private static void mapToSpecimenInformation(List<SubSection> subSections, Order order) {
        Map<String, List<String>> keyValues =
                subSections.parallelStream()
                        .collect(groupingBy(SubSection::getKey, mapping(SubSection::getValue, toList())));
        String patAuthDt = getValue(keyValues, CommonConstants.DATE);
        String collDt = getValue(keyValues, CommonConstants.COLLECTION_DT);
        PatientAuthorization patientAuthorization = PatientAuthorization.builder()
                .patientAuthName(getValue(keyValues, CommonConstants.PATIENT_NAME))
                .patientAuthDate(CommonUtility.isValidDateFormat(patAuthDt) ? CommonUtility.getFormattedDate(patAuthDt) : getEmpty())
                .patientSignaturePresent(isPatientSignaturePresent(checkSignature(keyValues)))
                .build();
        order.setPatientAuthorization(patientAuthorization);
        SpecimenDetails specimenDetails = order.getOrderInformation().getSpecimenDetails();
        specimenDetails.setCollectionDate(CommonUtility.isValidDateFormat(collDt) ? CommonUtility.getFormattedDate(collDt) : getEmpty());
        specimenDetails.setLabName(getValue(keyValues, CommonConstants.LAB_NAME));
        specimenDetails.setColSpecimenPerson(getValue(keyValues, CommonConstants.COL_SPEC_PERSON));
    }

    private static boolean isPatientSignaturePresent(String key) {
        return !key.isBlank();
    }

    private static void mapToOrderingPhysician(List<SubSection> subSections, Order order, Integer threshold) {

        Map<String, List<String>> keyValues =
                subSections.parallelStream()
                        .collect(groupingBy(SubSection::getKey, mapping(SubSection::getValue, toList())));
        String npi = setThresholdFilteredValue(subSections, CommonConstants.NPI, threshold);
        String stateCd = removeSpace(getValue(keyValues, CommonConstants.STATE));
        String email = getValue(keyValues, CommonConstants.EMAIL);
        String phone = removeSpace(getValue(keyValues, CommonConstants.FACILITY_PHN));
        final var identification = Identity.builder()
                .npi(CommonUtility.isValidNPI(npi) ? npi : getEmpty())
                .build();
        final var address = Address.builder()
                .streetAddress(getValue(keyValues, CommonConstants.FACILITY_ADDRESS))
                .city(getValue(keyValues, CommonConstants.CITY))
                .zip(getValue(keyValues, CommonConstants.ZIP))
                .state(CommonUtility.isValidUSAStateCode(stateCd) ? stateCd : getEmpty())
                .country(CommonConstants.US)
                .build();
        ProviderContactInfo providerContactInfo = ProviderContactInfo.builder()
                .providerEmail(CommonUtility.isValidEmail(email) ? email : getEmpty())
                .providerPhoneNumber(CommonUtility.isValidPhone(phone) ? phone : getEmpty())
                .build();

        OrderingPhysician orderingPhysician = order.getOrderInformation().getOrderingPhysician();
        orderingPhysician.setIdentification(identification);
        orderingPhysician.setLastName(getValue(keyValues, CommonConstants.PROV_LAST_NAME));
        orderingPhysician.setFirstName(getValue(keyValues, CommonConstants.PROV_FIRST_NAME));
        orderingPhysician.setFacilityName(getValue(keyValues, CommonConstants.HEALTH_ORG));
        orderingPhysician.setFacilityAcctNumber(removeSpace(getValue(keyValues, CommonConstants.ACCOUNT_ID)));
        orderingPhysician.setProviderAddress(address);
        orderingPhysician.setProviderContactInfo(providerContactInfo);
    }

    private static void mapToBillingInformation(List<SubSection> subSections, Order order) {

        Map<String, List<String>> keyValues =
                subSections.parallelStream()
                        .collect(groupingBy(SubSection::getKey, mapping(SubSection::getValue, toList())));
        InsuredPolicyInfo insuredPolicyInfo = InsuredPolicyInfo.builder()
                .plan(Plan.builder().planType(fetchPlanType(keyValues)).build())
                .policyNumber(getValue(keyValues, CommonConstants.POLICY))
                .groupNumber(getValue(keyValues, CommonConstants.GROUP))
                .build();
        String dob = getValue(keyValues, CommonConstants.INSURED_DOB);
        BillingInformation billingInformation = BillingInformation.builder()
                .insuredPersonalInfo(InsuredPersonalInfo.builder()
                        .firstName(getValue(keyValues, CommonConstants.INSURED_FIRST_NAME))
                        .lastName(getValue(keyValues, CommonConstants.INSURED_LAST_NAME))
                        .dateOfBirth(CommonUtility.isValidDateFormat(dob) ? CommonUtility.getFormattedDate(dob) : getEmpty())
                        .patientRelation(fetchPatientRelation(keyValues))
                        .build())
                .insuredPolicyInfo(insuredPolicyInfo)
                .billingLocation(BillingLocation.builder().locationType(fetchLocationType(keyValues)).build())
                .selfPay(fetchSelfPay(keyValues))
                .build();

        order.setBillingInformation(billingInformation);
    }

    private static void mapToOrderInformation(List<SubSection> subSections, Order order) {
        List<CancerType> cancerTypes = new ArrayList<>();
        cancerTypes.add(CancerType.builder().name(CommonConstants.CANCER_TYPE).build());

        Map<String, List<String>> keyValues =
                subSections.parallelStream()
                        .collect(groupingBy(SubSection::getKey, mapping(SubSection::getValue, toList())));

        TestRequested testRequested = TestRequested.builder().testName(SHIELD_LDT)
                .cancerType(cancerTypes).diagnosisCode(fetchDiagnosisCodes(keyValues))
                .resultDisclosure(checkResultDisclosureStatus(keyValues))
                .build();

        String orderDt = getValue(keyValues, CommonConstants.ORDER_DT);
        OrderInformation orderInformation = order.getOrderInformation();
        orderInformation.setOrderSource(CommonConstants.FAX);
        orderInformation.setTestRequested(testRequested);
        orderInformation.setOrderStatus(CommonConstants.NEW);
        orderInformation.setTestOrderedDate(
                CommonUtility.isValidDateFormat(orderDt) ? CommonUtility.getFormattedDate(orderDt) : getEmpty());
        orderInformation.getSpecimenDetails().setColLocation(fetchColLocation(keyValues));
    }

    private static boolean checkResultDisclosureStatus(Map<String, List<String>> keyValues) {
        Optional<Map.Entry<String, List<String>>> entryOptional = keyValues.entrySet().parallelStream()
                .filter(entrySet -> entrySet.getKey().contains(CHECK_BOX)
                ).findFirst();
        return entryOptional.map(stringListEntry -> stringListEntry.getValue().get(0).equals(CommonConstants.SELECTED))
                .orElse(Boolean.FALSE);
    }

    private static void mapToPatient(List<SubSection> subSections, Order order, SubSection signature, Integer threshold) {

        List<Phone> phoneList = new ArrayList<>();
        Map<String, List<String>> keyValues =
                subSections.parallelStream()
                        .collect(groupingBy(SubSection::getKey, mapping(SubSection::getValue, toList())));

        final var identification =
                Identification.builder()
                        .medicalRecordNumber(getValue(keyValues, CommonConstants.MEDICAL_RECORD))
                        .build();

        final var lastName = setThresholdFilteredValue(subSections, CommonConstants.LAST_NAME, threshold);
        final var firstName = setThresholdFilteredValue(subSections, CommonConstants.FIRST_NAME, threshold);
        final var dateOfBirth = setThresholdFilteredValue(subSections, CommonConstants.DOB, threshold);

        final var stateCd = removeSpace(getValue(keyValues, CommonConstants.STATE));
        final var email = removeSpace(getValue(keyValues, CommonConstants.EMAIL));
        PersonalInfo personalInfo =
                PersonalInfo.builder()
                        .lastName(lastName)
                        .firstName(firstName)
                        .dob(CommonUtility.isValidDateFormat(dateOfBirth) ? CommonUtility.getFormattedDate(dateOfBirth) : getEmpty())
                        .sex(fetchSex(keyValues))
                        .languagePref(fetchLangPref(keyValues))
                        .build();
        final var address =
                Address.builder()
                        .streetAddress(getValue(keyValues, CommonConstants.ADDRESS))
                        .city(getValue(keyValues, CommonConstants.CITY))
                        .state(CommonUtility.isValidUSAStateCode(stateCd) ? stateCd : getEmpty())
                        .zip(getValue(keyValues, CommonConstants.ZIP))
                        .country(CommonConstants.US)
                        .build();
        final var phnNumber = getValue(keyValues, CommonConstants.PHONE);
        if (!phnNumber.isEmpty()) {
            phoneList.add(Phone.builder().phoneType(CommonConstants.MOBILE).phoneNumber(phnNumber).build());
        }

        final var contactData =
                ContactData.builder()
                        .phone(phoneList)
                        .email(CommonUtility.isValidEmail(email) ? email : getEmpty())
                        .patientContactConsent(isPatientContactConsent(checkConsent(signature)))
                        .build();
        final var demographics =
                Demographics.builder()
                        .personalInfo(personalInfo)
                        .address(address)
                        .contactData(contactData)
                        .build();
        order.getPatient().setIdentification(identification);
        order.getPatient().setDemographics(demographics);
    }

    private static String checkConsent(SubSection signature) {
        return Objects.nonNull(signature) ? signature.getValue() : getEmpty();

    }

    private static boolean isPatientContactConsent(String key) {
        return !key.isBlank();
    }

    private static String setThresholdFilteredValue(List<SubSection> subSections, String key, Integer threshold) {
        Optional<SubSection> subSection = subSections.stream().filter(sec -> sec.getKey().equals(key)
                && sec.getValueConfidence() > threshold).findFirst();
        return subSection.isPresent() ? removeSpace(subSection.get().getValue()) : getEmpty();
    }

    private static String fetchLangPref(Map<String, List<String>> keyValues) {
        final var anyMatch = keyValues.entrySet().parallelStream()
                .filter(getLangPredicate())
                .flatMap(entrySet -> entrySet.getValue().stream()).anyMatch(String::isBlank);
        if (anyMatch)
            return getEmpty();

        String lang = "";
        var flag = false;
        if (Objects.nonNull(keyValues.get(CommonConstants.ENGLISH)) && keyValues.get(CommonConstants.ENGLISH).
                get(0).equals(CommonConstants.SELECTED)) {
            flag = true;
            lang = CommonConstants.ENGLISH;

        }
        if (Objects.nonNull(keyValues.get(CommonConstants.SPANISH)) && keyValues.get(CommonConstants.SPANISH)
                .get(0).equals(CommonConstants.SELECTED)) {
            if (flag)
                return getEmpty();
            flag = true;
            lang = CommonConstants.SPANISH;
        }
        if (Objects.nonNull(keyValues.get(CommonConstants.OTHER_COLON)) && !keyValues.get(CommonConstants.OTHER_COLON)
                .get(0).isEmpty()) {
            if (flag)
                return getEmpty();
            lang = keyValues.get(CommonConstants.OTHER_COLON).get(0);
        }
        return lang;
    }

    private static String fetchSex(Map<String, List<String>> keyValues) {
        final var anyMatch = keyValues.entrySet().parallelStream()
                .filter(getSexPredicate())
                .flatMap(entrySet -> entrySet.getValue().stream()).anyMatch(String::isBlank);
        if (anyMatch)
            return getEmpty();
        boolean isMale = false;
        boolean isFemale = false;
        String sex = getEmpty();
        if (Objects.nonNull(keyValues.get(CommonConstants.M)) && keyValues.get(CommonConstants.M).get(0)
                .equals(CommonConstants.SELECTED)) {
            isMale = true;
            sex = CommonConstants.MALE;
        }
        if (Objects.nonNull(keyValues.get(CommonConstants.F)) && keyValues.get(CommonConstants.F).get(0)
                .equals(CommonConstants.SELECTED)) {
            isFemale = true;
            sex = CommonConstants.FEMALE;
        }
        return isMale == isFemale ? CommonConstants.UNKNOWN : sex;
    }

    private static String fetchColLocation(Map<String, List<String>> keyValues) {
        final var anyMatch = keyValues.entrySet().parallelStream()
                .filter(getColLocationPredicate())
                .flatMap(entrySet -> entrySet.getValue().stream()).anyMatch(String::isBlank);
        if (anyMatch)
            return getEmpty();
        var locationType = "";
        var flag = false;
        if (Objects.nonNull(keyValues.get(CommonConstants.IN_OFFICE_CLINIC)) &&
                keyValues.get(CommonConstants.IN_OFFICE_CLINIC).get(0).equals(CommonConstants.SELECTED)) {
            locationType = CommonConstants.IN_OFFICE_CLINIC;
            flag = true;
        }
        if (Objects.nonNull(keyValues.get(CommonConstants.PATIENT_ASSISTANCE)) &&
                keyValues.get(CommonConstants.PATIENT_ASSISTANCE).get(0).equals(CommonConstants.SELECTED)) {
            if (flag)
                return getEmpty();
            locationType = CommonConstants.PATIENT_ASSISTANCE;
            flag = true;
        }
        if (Objects.nonNull(keyValues.get(CommonConstants.QUEST_PATIENT_SERVICE_CENTER)) &&
                keyValues.get(CommonConstants.QUEST_PATIENT_SERVICE_CENTER).get(0).equals(CommonConstants.SELECTED)) {
            if (flag)
                return getEmpty();
            locationType = CommonConstants.QUEST_PATIENT_SERVICE_CENTER;
        }

        return locationType;
    }

    private static String fetchPlanType(Map<String, List<String>> keyValues) {
        final var anyMatch = keyValues.entrySet().parallelStream()
                .filter(getPlanTypePredicate())
                .flatMap(entrySet -> entrySet.getValue().stream()).anyMatch(String::isBlank);
        if (anyMatch)
            return getEmpty();
        var insurance = "";
        var flag = false;
        if (Objects.nonNull(keyValues.get(CommonConstants.MEDICARE)) && keyValues.get(CommonConstants.MEDICARE)
                .get(0).equals(CommonConstants.SELECTED)) {
            insurance = CommonConstants.MEDICARE;
            flag = true;
        }
        if (Objects.nonNull(keyValues.get(CommonConstants.MEDICAID)) && keyValues.get(CommonConstants.MEDICAID)
                .get(0).equals(CommonConstants.SELECTED)) {
            if (flag)
                return getEmpty();
            insurance = CommonConstants.MEDICAID;
        }
        return insurance;
    }

    private static List<DiagnosisCode> fetchDiagnosisCodes(Map<String, List<String>> keyValues) {
        List<DiagnosisCode> diagnosisCodes = new ArrayList<>();
        Map.Entry<String, List<String>> optionalIcdCode = keyValues.entrySet().parallelStream()
                .filter(entry ->
                        entry.getKey().contains("[Z12.11] and rectum [Z12.12]")
                ).findFirst().orElse(null);
        if (Objects.nonNull(optionalIcdCode) && !optionalIcdCode.getValue().isEmpty() &&
                optionalIcdCode.getValue().get(0).equals(CommonConstants.SELECTED)) {
            diagnosisCodes.add(DiagnosisCode.builder().code("Z12.11").build());
            diagnosisCodes.add(DiagnosisCode.builder().code("Z12.12").build());
        }
        Map.Entry<String, List<String>> otherIcdCode = keyValues.entrySet().parallelStream()
                .filter(entry ->
                        entry.getKey().contains(OTHERS_COLON)
                ).findFirst().orElse(null);
        if (Objects.nonNull(otherIcdCode) && !otherIcdCode.getValue().isEmpty() &&
                otherIcdCode.getValue().get(0).equals(CommonConstants.SELECTED)) {
            String[] splitCodes = otherIcdCode.getKey().split(":")[1].split(",");
            for (String splitCode : splitCodes) {
                diagnosisCodes.add(DiagnosisCode.builder().code(splitCode.strip()).build());
            }
        }
        return diagnosisCodes;
    }


    private static String fetchLocationType(Map<String, List<String>> keyValues) {
        final var anyMatch = keyValues.entrySet().parallelStream()
                .filter(getLocationTypePredicate())
                .flatMap(entrySet -> entrySet.getValue().stream()).anyMatch(String::isBlank);
        if (anyMatch)
            return getEmpty();
        String patientStatus = "";
        var flag = false;
        if (Objects.nonNull(keyValues.get(CommonConstants.HOSPITAL_OUTPATIENT)) &&
                keyValues.get(CommonConstants.HOSPITAL_OUTPATIENT).get(0).equals(CommonConstants.SELECTED)) {
            patientStatus = CommonConstants.HOSPITAL_OUTPATIENT;
            flag = true;
        }
        if (Objects.nonNull(keyValues.get(CommonConstants.NON_HOSPITAL_PATIENT)) &&
                keyValues.get(CommonConstants.NON_HOSPITAL_PATIENT).get(0).equals(CommonConstants.SELECTED)) {
            if (flag)
                return getEmpty();
            patientStatus = CommonConstants.NON_HOSPITAL_PATIENT;
        }

        return patientStatus;
    }

    private static Predicate<? super Map.Entry<String, List<String>>> getLocationTypePredicate() {
        return entrySet -> entrySet.getKey()
                .equals(CommonConstants.HOSPITAL_OUTPATIENT) ||
                entrySet.getKey().equals(CommonConstants.NON_HOSPITAL_PATIENT);
    }

    private static String fetchPatientRelation(Map<String, List<String>> keyValues) {
        final var anyMatch = keyValues.entrySet().parallelStream()
                .filter(getPatientRelationPredicate())
                .flatMap(entrySet -> entrySet.getValue().stream()).anyMatch(String::isBlank);
        if (anyMatch)
            return getEmpty();
        String insured = "";
        var flag = false;
        if (Objects.nonNull(keyValues.get(CommonConstants.SELF)) && keyValues.get(CommonConstants.SELF).get(0)
                .equals(CommonConstants.SELECTED)) {
            insured = CommonConstants.SELF;
            flag = true;
        }
        if (Objects.nonNull(keyValues.get(CommonConstants.SPOUSE)) && keyValues.get(CommonConstants.SPOUSE)
                .get(0).equals(CommonConstants.SELECTED)) {
            if (flag)
                return getEmpty();
            flag = true;
            insured = CommonConstants.SPOUSE;
        }
        if (Objects.nonNull(keyValues.get(CommonConstants.CHILD)) && keyValues.get(CommonConstants.CHILD)
                .get(0).equals(CommonConstants.SELECTED)) {
            if (flag)
                return getEmpty();
            flag = true;
            insured = CommonConstants.CHILD;
        }
        if (Objects.nonNull(keyValues.get(CommonConstants.OTHER)) && keyValues.get(CommonConstants.OTHER)
                .get(0).equals(CommonConstants.SELECTED)) {
            if (flag)
                return getEmpty();
            insured = CommonConstants.OTHER;
        }
        return insured;
    }

    private static String removeSpace(String str) {
        return Objects.nonNull(str) ? str.replaceAll("\\s", getEmpty()) : getEmpty();
    }

    private static boolean fetchSelfPay(Map<String, List<String>> keyValues) {
        return keyValues.keySet().stream().filter(key -> key.contains(SELF_PAY)).findFirst()
                .map(key -> keyValues.get(key).get(0).equals(CommonConstants.SELECTED)).orElse(Boolean.FALSE);
    }

    private static Predicate<? super Map.Entry<String, List<String>>> getSexPredicate() {
        return entrySet -> entrySet.getKey()
                .equals(CommonConstants.M) || entrySet.getKey().equals(CommonConstants.F);
    }

    private static Predicate<Map.Entry<String, List<String>>> getPlanTypePredicate() {
        return entrySet -> entrySet.getKey()
                .equals(CommonConstants.MEDICARE) || entrySet.getKey().equals(CommonConstants.MEDICAID);
    }

    private static Predicate<? super Map.Entry<String, List<String>>> getColLocationPredicate() {
        return entrySet -> entrySet.getKey()
                .equals(CommonConstants.IN_OFFICE_CLINIC) || entrySet.getKey().equals(CommonConstants.PATIENT_ASSISTANCE)
                || entrySet.getKey().equals(CommonConstants.QUEST_PATIENT_SERVICE_CENTER);
    }

    private static Predicate<Map.Entry<String, List<String>>> getLangPredicate() {
        return entrySet -> entrySet.getKey()
                .equals(CommonConstants.ENGLISH) || entrySet.getKey().equals(CommonConstants.SPANISH);
    }

    private static Predicate<? super Map.Entry<String, List<String>>> getPatientRelationPredicate() {
        return entrySet -> entrySet.getKey()
                .equals(CommonConstants.SELF) || entrySet.getKey().equals(CommonConstants.SPOUSE) ||
                entrySet.getKey().equals(CommonConstants.CHILD) ||
                entrySet.getKey().equals(CommonConstants.OTHER);
    }

    private static String getValue(Map<String, List<String>> listMap, String key) {
        if (Objects.nonNull(listMap.get(key))) return listMap.get(key).get(0);
        return getEmpty();
    }

    private static String getEmpty() {
        return CommonConstants.EMPTY;
    }

}
