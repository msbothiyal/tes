package com.guardant.so2c.ocr.textextractor.model;

import lombok.*;

/*
 * @author msbothiyal
 * @date 22/06/21,1:45 PM
 */
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class SubSection {
    private String key;
    private Float keyConfidence;
    private String value;
    private Float valueConfidence;
}
