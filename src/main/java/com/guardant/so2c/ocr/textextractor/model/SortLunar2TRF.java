package com.guardant.so2c.ocr.textextractor.model;

import com.guardant.so2c.ocr.textextractor.constants.CommonConstants;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.Comparator;

/* Comparator for LUNAR 2 doc, to sort duplicates on the basis of vertical coordinates
 * @author msbothiyal
 * @date 26/07/21,3:44 PM
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class SortLunar2TRF {
    public static final Comparator<Field> SORT_BY_0DEGREE = (o1, o2) -> {
        if (null != o1.getValue() && null != o2.getValue()) {
            return o1.getValue().getGeometry().getBoundingBox().getTop().compareTo(
                    o2.getValue().getGeometry().getBoundingBox().getTop());
        }
        return 0;
    };
    public static final Comparator<Field> SORT_BY_90DEGREE = (o1, o2) -> {
        if ((null != o1.getValue() && null != o2.getValue()) && !o1.getKey().getText().equals(CommonConstants.EMAIL)) {
            return o2.getValue().getGeometry().getBoundingBox().getTop().compareTo(
                    o1.getValue().getGeometry().getBoundingBox().getTop());
        }
        return 0;
    };
    public static final Comparator<Field> SORT_BY_180DEGREE = (o1, o2) -> {
        if (null != o1.getValue() && null != o2.getValue()) {
            return o2.getValue().getGeometry().getBoundingBox().getTop().compareTo(
                    o1.getValue().getGeometry().getBoundingBox().getTop());
        }
        return 0;
    };
    public static final Comparator<Field> SORT_BY_270DEGREE = (o1, o2) -> {
        if ((null != o1.getValue() && null != o2.getValue()) && !o1.getKey().getText().equals(CommonConstants.EMAIL)) {
            return o1.getValue().getGeometry().getBoundingBox().getTop().compareTo(
                    o2.getValue().getGeometry().getBoundingBox().getTop());
        }
        return 0;
    };
}