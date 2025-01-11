package com.guardant.so2c.ocr.textextractor.utility;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/*
 * @author msbothiyal
 * @date 06/10/21,7:41 PM
 */
class CommonUtilityTest {

    @Test
    void isValidEmail() {
        Assertions.assertFalse(CommonUtility.isValidEmail(""));
        Assertions.assertFalse(CommonUtility.isValidEmail("mike"));
        Assertions.assertFalse(CommonUtility.isValidEmail("mike@mike"));
        Assertions.assertFalse(CommonUtility.isValidEmail("rick@@rick.com"));
        Assertions.assertFalse(CommonUtility.isValidEmail("jacke@wick"));

        Assertions.assertTrue(CommonUtility.isValidEmail("mike@gmail.com"));
        Assertions.assertTrue(CommonUtility.isValidEmail("m_i_k_@yahoo.co.in"));
        Assertions.assertTrue(CommonUtility.isValidEmail("mike_rick@gmail.com"));
    }

    @Test
    void isValidUSAStateCode() {
        Assertions.assertFalse(CommonUtility.isValidUSAStateCode("XX"));
        Assertions.assertFalse(CommonUtility.isValidUSAStateCode("_AASD"));
        Assertions.assertFalse(CommonUtility.isValidUSAStateCode("_"));
        Assertions.assertFalse(CommonUtility.isValidUSAStateCode("**"));
        Assertions.assertFalse(CommonUtility.isValidUSAStateCode("DQ"));
        Assertions.assertFalse(CommonUtility.isValidUSAStateCode(""));
        Assertions.assertFalse(CommonUtility.isValidUSAStateCode(null));

        Assertions.assertTrue(CommonUtility.isValidUSAStateCode("NY"));
        Assertions.assertTrue(CommonUtility.isValidUSAStateCode("AZ"));
        Assertions.assertTrue(CommonUtility.isValidUSAStateCode("AL"));
    }

    @Test
    void isValidateJavaDate() {
        Assertions.assertFalse(CommonUtility.isValidDateFormat(null));
        Assertions.assertFalse(CommonUtility.isValidDateFormat(""));
        Assertions.assertFalse(CommonUtility.isValidDateFormat("02/30/2016"));
        Assertions.assertFalse(CommonUtility.isValidDateFormat("12/10/202°"));
        Assertions.assertFalse(CommonUtility.isValidDateFormat("00/00/_"));
        Assertions.assertFalse(CommonUtility.isValidDateFormat("00/00/2000"));
        Assertions.assertFalse(CommonUtility.isValidDateFormat("12-29-2016"));
        Assertions.assertFalse(CommonUtility.isValidDateFormat("12,29,2016"));
        Assertions.assertFalse(CommonUtility.isValidDateFormat("13/29/2016"));
        Assertions.assertFalse(CommonUtility.isValidDateFormat("12/32/2016"));
        Assertions.assertFalse(CommonUtility.isValidDateFormat("00/00/0000"));

        Assertions.assertTrue(CommonUtility.isValidDateFormat("12/29/2016"));
        Assertions.assertTrue(CommonUtility.isValidDateFormat("1/2/1999"));
        Assertions.assertTrue(CommonUtility.isValidDateFormat("02/03/4005"));
        Assertions.assertTrue(CommonUtility.isValidDateFormat("12/01/94"));
    }

    @Test
    void isValidFax() {
        Assertions.assertFalse(CommonUtility.isValidPhone("202 555 0125"));
        Assertions.assertFalse(CommonUtility.isValidPhone(null));
        Assertions.assertFalse(CommonUtility.isValidPhone(""));
        Assertions.assertFalse(CommonUtility.isValidPhone("12-123-1232"));

        Assertions.assertTrue(CommonUtility.isValidPhone("202-555-0125"));
        Assertions.assertTrue(CommonUtility.isValidPhone("123-432-1233"));
    }

    @Test
   void errorMessageBuilder(){
        Assertions.assertNotNull(CommonUtility.errorMessageBuilder(message()));
    }

    private String message(){
        return "{\n" +
                " \t\tasdad,\n" +
                "      \"S3_Bucket_Location\": \"https://so2c-o2r-sfdc-ocr-local.s3.us-west-2.amazonaws.com/sqa/v15-GCDF.pdf\",\n" +
                "      \"Type_Of_Document\": \"LUNAR2 TRF\",\n" +
                "      \"DocumentMode\": \"FAX\",\n" +
                "      \"Fax_Track_Unique_Id\": \"11111111111\"\n" +
                "    }";
    }

    @Test
    void isValidNPI(){
        Assertions.assertTrue(CommonUtility.isValidNPI("1234567890"));
        Assertions.assertFalse(CommonUtility.isValidNPI("12345678A1"));
        Assertions.assertFalse(CommonUtility.isValidNPI(""));
        Assertions.assertFalse(CommonUtility.isValidNPI(null));
    }
    @Test
    void isValidUSAZip(){
        Assertions.assertTrue(CommonUtility.isValidUSAZip("12345"));
        Assertions.assertFalse(CommonUtility.isValidUSAZip("123456"));
        Assertions.assertFalse(CommonUtility.isValidUSAZip("1234X"));
        Assertions.assertFalse(CommonUtility.isValidUSAZip(""));
        Assertions.assertFalse(CommonUtility.isValidUSAZip(null));
    }

    @Test
    void getFormattedDate() {
        Assertions.assertEquals("12/29/2016", CommonUtility.getFormattedDate("12/29/2016"));
        Assertions.assertEquals("02/09/2016", CommonUtility.getFormattedDate("2/9/2016"));
        Assertions.assertEquals("", CommonUtility.getFormattedDate("12/01/94"));
    }
}
