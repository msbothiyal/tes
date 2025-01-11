package com.guardant.so2c.ocr.textextractor.model;

import lombok.*;

import java.util.List;

/*
 * @author msbothiyal
 * @date 22/06/21,1:45 PM
 */
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class Section {
    private String sectionName;
    private Float sectionNameConfidence;
    private List<SubSection> subSections;
    private String sectionValue;
    private Float sectionValueConfidence;
}
