package com.guardant.so2c.ocr.textextractor.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/*
 * Document response after extraction
 * @author msbothiyal
 * @date 20/09/21,11:02 AM
 */
@Getter
@Setter
@AllArgsConstructor
public class DocumentResponse {
    private List<Section> sectionList;
    private Integer threshold;

}
