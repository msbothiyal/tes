package com.guardant.so2c.ocr.textextractor.model;

import java.util.Comparator;

/* Comparator for G360 doc, to sort duplicates on the basis of horizontal coordinates
 * @author msbothiyal
 * @date 26/07/21,3:44 PM
 */
public class SortGuardant360TRF implements Comparator<Field> {

    @Override
    public int compare(Field o1, Field o2) {
        if (null!=o1.getValue() && null!=o2.getValue() ) {
            return o1.getValue().getGeometry().getBoundingBox().getLeft().compareTo(
                    o2.getValue().getGeometry().getBoundingBox().getLeft());
        }
        return 0;
    }
}
