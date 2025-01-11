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
public class Word {

    private Block block;
    private Float confidence;
    private Geometry geometry;
    private String id;
    private String text;

    public Word(int i, String text, Float confidence) {
        this.id = String.valueOf(i);
        this.text = text==null? "":text;
        this.confidence = confidence;
    }
    public Word(Block block) {
        this.block = block;
        this.confidence = block.getConfidence();
        this.geometry = block.getGeometry();
        this.text = block.getText();
        this.id = block.getId();
    }
}
