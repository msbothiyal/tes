package com.guardant.so2c.ocr.textextractor.model;

import com.amazonaws.services.textract.model.Block;
import com.amazonaws.services.textract.model.Geometry;
import lombok.Getter;
import lombok.Setter;

/*
 * Textract mapping related POJO
 * @author msbothiyal
 * @date 22/06/21,11:02 AM
 */
@Getter @Setter
public class SelectionElement {

    private Float confidence;
    private Geometry geometry;
    private String id;
    private String selectionStatus;

    public SelectionElement(Block block) {
        this.confidence = block.getConfidence();
        this.geometry = block.getGeometry();
        this.id = block.getId();
        this.selectionStatus = block.getSelectionStatus();
    }
}
