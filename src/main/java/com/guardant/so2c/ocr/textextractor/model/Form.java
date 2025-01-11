package com.guardant.so2c.ocr.textextractor.model;

import com.guardant.so2c.ocr.textextractor.constants.CommonConstants;
import com.guardant.so2c.ocr.textextractor.enums.DocType;
import com.guardant.so2c.ocr.textextractor.utility.CommonUtility;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static com.guardant.so2c.ocr.textextractor.constants.CommonConstants.POINT_FIVE;

/*
 * Textract mapping related POJO
 * @author msbothiyal
 * @date 22/06/21,11:02 AM
 */
@Getter
@Setter
public class Form {
    private List<Field> fields = new ArrayList<>();
    private Map<String, List<Field>> fieldsMap = new LinkedHashMap<>();
    private int rotationInDegree;

    Form(int degree) {
        this.rotationInDegree = degree;
    }

    public void addField(Field field, DocType docType) {
        this.fields.add(field);
        String fieldTxt = field.getKey().getText();
        List<Field> flds = new ArrayList<>();
        if (fieldsMap.containsKey(fieldTxt)) {
            flds = new ArrayList<>(fieldsMap.get(fieldTxt));
            flds.add(field);
            if (docType.equals(DocType.LUNAR_2)) {
                if (flds.size() > 1) // sort only if there are multiple fields
                    CommonUtility.sortByDegree(flds, rotationInDegree);
            } else {
                flds.sort(new SortGuardant360TRF());
            }
            fieldsMap.put(fieldTxt, flds);
        } else {
            flds.add(field);
            fieldsMap.put(fieldTxt, flds);
        }
    }

    public Field getFieldByKey(String str) {

        List<Field> temp = this.fieldsMap.get(str);
        Field field = null;

        if (temp != null && !temp.isEmpty()) {
            field = temp.get(0);
            this.fieldsMap.get(str).remove(field);
        }
        return field;
    }

    public Field getFieldByKey(String str, Line line) {

        List<Field> temp = this.fieldsMap.get(str);
        Field field = null;

        if (temp != null && !temp.isEmpty()) {
            field = temp.get(0);
            //Custom code to handle SO2C-1048, duplicate email case with wrong mapping
            if (isFieldAvailable(line) && temp.size() != 2 &&
                    (line.getGeometry().getBoundingBox().getTop() < POINT_FIVE
                            && field.getKey().getGeometry().getBoundingBox().getTop() > POINT_FIVE)) {
                field = null;
            } else if (isFieldAvailable(line) && temp.size() != 2 && (line.getGeometry().getBoundingBox().getTop()
                    > POINT_FIVE && field.getKey().getGeometry().getBoundingBox().getTop() < POINT_FIVE) ) {
                field = null;
            } else {
                this.fieldsMap.get(str).remove(field);
            }
        }
        return field;
    }

    private boolean isFieldAvailable(Line line) {
        return line.getText().equals(CommonConstants.EMAIL) || line.getText().equals(CommonConstants.STATE) ||
                line.getText().equals(CommonConstants.ZIP);
    }
}
