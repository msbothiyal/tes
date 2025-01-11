package com.guardant.so2c.ocr.textextractor.enums;

import com.fasterxml.jackson.annotation.JsonValue;

/* Document type for file to be processed
 * @author msbothiyal
 * @date 26/07/21,2:58 PM
 */
public enum DocType {

    G360("GUARDANT 360"),
    LUNAR_2("LUNAR2 TRF"),

    OTHER("OTHER");

    @JsonValue
    public final String docName;

    DocType(String name) {
        this.docName = name;
    }

    public static DocType valueOfLabel(String label) {
        for (DocType e : values()) {
            if (e.docName.equals(label)) {
                return e;
            }
        }
        return null;
    }
}
