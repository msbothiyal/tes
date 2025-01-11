package com.guardant.so2c.ocr.textextractor.constants;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/*
 * Define generic constants used in projects
 * @author msbothiyal
 * @date 22/06/21,11:02 AM
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CommonConstants {

    public static final String G360_SUBSECTION_REGEX = "^\\d{1,2}(\\. )?[A-Z]*";
    public static final int EIGHT = 8;
    public static final String EMPTY = "";
    public static final String MEDICAL_RECORD = "Medical Record #";
    public static final String LAST_NAME = "Last Name";
    public static final String FIRST_NAME = "First Name";
    public static final String DOB = "DOB (mm/dd/yyyy)";
    public static final String STATE = "State";
    public static final String EMAIL = "Email";
    public static final String CITY = "City";
    public static final String ZIP = "Zip";
    public static final String US = "US";
    public static final String PHONE = "Contact Phone # (Mobile preferred)";
    public static final String MOBILE = "Mobile";
    public static final String SELECTED = "SELECTED";
    public static final String NOT_SELECTED = "NOT_SELECTED";
    public static final String ENGLISH = "English";
    public static final String SPANISH = "Spanish";
    public static final String M = "M";
    public static final String F = "F";
    public static final String MALE = "Male";
    public static final String FEMALE = "Female";
    public static final String SELF_PAY = "Self-pay";
    public static final String SELF = "Self";
    public static final String SPOUSE = "Spouse";
    public static final String CHILD = "Child";
    public static final String OTHER = "Other";
    public static final String OTHER_COLON = "Other:";
    public static final String MEDICARE = "Medicare";
    public static final String MEDICAID = "Medicaid";
    public static final String HOSPITAL_OUTPATIENT = "Hospital Outpatient";
    public static final String NON_HOSPITAL_PATIENT = "Non-hospital Patient";
    public static final String DATE = "Date (mm/dd/yyyy)";
    public static final String COLLECTION_DT = "Collection Date (mm/dd/yyyy)";
    public static final String PATIENT_NAME = "Print Patient Name";
    public static final String LAB_NAME = "Lab/Phlebotomy Company Name";
    public static final String COL_SPEC_PERSON = "Name of Person Collecting Specimen";
    public static final String NPI = "NPI #";
    public static final String FACILITY_PHN = "Facility/Clinic Contact Phone #";
    public static final String FACILITY_ADDRESS = "Facility/Clinic Address";
    public static final String PROV_LAST_NAME = "Provider Last Name";
    public static final String PROV_FIRST_NAME = "Provider First Name";
    public static final String HEALTH_ORG = "Facility/Clinic Name";
    public static final String ACCOUNT_ID = "Account ID (Optional)";
    public static final String POLICY = "Policy #";
    public static final String GROUP = "Group #";
    public static final String INSURED_DOB = "Insured DOB (mm/dd/yyyy)";
    public static final String INSURED_FIRST_NAME = "Insured First Name";
    public static final String INSURED_LAST_NAME = "Insured Last Name";
    public static final String CANCER_TYPE = "CRC";
    public static final String SHIELD_LDT = "Shield LDT";
    public static final String ORDER_DT = "Order Date (mm/dd/yyyy)";
    public static final String FAX = "Fax";
    public static final String NEW = "New";
    public static final String ADDRESS = "Street Address";
    public static final String UNKNOWN = "Unknown";
    public static final String IN_OFFICE_CLINIC = "In Office/Clinic";
    public static final String PATIENT_ASSISTANCE = "Patient needs assistance coordinating blood collection";
    public static final String QUEST_PATIENT_SERVICE_CENTER = "Quest Patient Service Center";
    public static final String OTHERS_COLON = "Other(s): ";
    public static final String CHECK_BOX = "By checking this box";
    public static final String MEDICAL_PROFESSIONAL_SIGNATURE = "Medical Professional Signature";
    public static final String LUNAR_2 = "LUNAR2 TRF";
    public static final Double POINT_FIVE = 0.5;
    public static final String LINE = "LINE";
    public static final String PATIENT_SIGNATURE = "PATIENT SIGNATURE";
    public static final String PATIENT_MEDICAL_RECORD = "PATIENT MEDICAL RECORD #";
    public static final int PAGE_ONE = 1;
    public static final int PAGE_TWO = 2;
}
