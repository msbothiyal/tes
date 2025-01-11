package com.guardant.so2c.ocr.textextractor.utility;

import com.guardant.so2c.ocr.textextractor.constants.CommonConstants;
import com.guardant.so2c.ocr.textextractor.model.Field;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.log4j.Log4j2;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.guardant.so2c.ocr.textextractor.model.SortLunar2TRF.*;

/*
 * Common utility
 * @author msbothiyal
 * @date 06/10/21,11:02 AM
 */
@Log4j2
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CommonUtility {

    private static final String STATES = "|AL|AK|AS|AZ|AR|CA|CO|CT|DE|DC|FM|FL|GA|GU|HI|ID|IL|IN|IA|KS|KY|LA|ME|MH|MD" +
            "|MA|MI|MN|MS|MO|MT|NE|NV|NH|NJ|NM|NY|NC|ND|MP|OH|OK|OR|PW|PA|PR|RI|SC|SD|TN|TX|UT|VT|VI|VA|WA|WV|WI|WY|";
    private static final String EMAIL_REGEX = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
    private static final String ZIP_REGEX = "^[0-9]{5}(?:-[0-9]{4})?$";
    private static final String PHONE_REGEX = "^(\\d{3}[-]?){2}\\d{4}$";
    private static final String NPI_REGEX = "^\\d{10}$";


    public static boolean isValidEmail(String email) {
        if (isNullOrEmpty(email))
            return false;
        Pattern pat = Pattern.compile(EMAIL_REGEX);//NOSONAR
        return pat.matcher(email).matches();
    }

    public static boolean isValidUSAStateCode(String stateCode) {
        if (isNullOrEmpty(stateCode))
            return false;
        return stateCode.length() == 2 && STATES.contains(stateCode);
    }

    public static boolean isValidUSAZip(String zip) {
        if (isNullOrEmpty(zip))
            return false;
        Pattern pattern = Pattern.compile(ZIP_REGEX);
        Matcher matcher = pattern.matcher(zip);
        return matcher.matches();
    }

    public static boolean isValidDateFormat(String date) {
        if (checkValidDate(date))
            return false;
        else {
            SimpleDateFormat sdfrmt = new SimpleDateFormat("MM/dd/yyyy");
            sdfrmt.setLenient(false);
            try {
                sdfrmt.parse(date);
            } catch (ParseException e) {
                return false;
            }
            return true;
        }
    }

    public static boolean isValidPhone(String phn) {
        if (isNullOrEmpty(phn))
            return false;
        Pattern pattern = Pattern.compile(PHONE_REGEX);
        Matcher matcher = pattern.matcher(phn);
        return matcher.matches();
    }

    public static boolean isValidNPI(String npi) {
        if (isNullOrEmpty(npi))
            return false;
        Pattern pattern = Pattern.compile(NPI_REGEX);
        Matcher matcher = pattern.matcher(npi);
        return matcher.matches();
    }

    public static boolean isNullOrEmpty(String str) {
        return str == null || str.equals(CommonConstants.EMPTY);
    }

    public static String errorMessageBuilder(String message) {
        return "{\n" + '"' + "Efax Message" + '\"' + " : " +
                message + ",\n" +
                '"' + "Error Desc" + '"' + " : " +
                '\"' + "Invalid JSON in EFAX message" + '"' +
                "\n}";
    }

    public static String getFormattedDate(String date) {

        final var original = DateTimeFormatter.ofPattern("M/d/yyyy");
        final var expected = DateTimeFormatter.ofPattern("MM/dd/yyyy");
        String updatedDate;
        try {
            updatedDate = date.matches("([0-9]{2})/([0-9]{2})/([0-9]{4})") ? date : LocalDate.parse(date, original)
                    .format(expected);
        } catch (DateTimeParseException exception) {
            updatedDate = "";
        }
        return updatedDate;

    }

    private static boolean checkValidDate(String date) {

        return isNullOrEmpty(date) || Pattern.compile("[^0-9/]").matcher(date).find();
    }

    public static void sortByDegree(List<Field> flds, int degree) {

        switch (degree) {
            case 90:
                flds.sort(SORT_BY_90DEGREE);
                break;
            case 180:
                flds.sort(SORT_BY_180DEGREE);
                break;
            case 270:
                flds.sort(SORT_BY_270DEGREE);
                break;
            default:
                flds.sort(SORT_BY_0DEGREE);
        }
    }
    public static <T> Collection<T> union(List<T> blocks1, List<T> blocks2) {
        List<T> result = new ArrayList<>(blocks1);
        result.addAll(blocks2);
        return result;
    }
}
