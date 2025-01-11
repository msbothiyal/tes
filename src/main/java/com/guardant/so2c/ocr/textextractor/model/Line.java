package com.guardant.so2c.ocr.textextractor.model;

import com.amazonaws.services.textract.model.Block;
import com.amazonaws.services.textract.model.Geometry;
import com.amazonaws.services.textract.model.Relationship;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
/*
 * Textract mapping related POJO
 * @author msbothiyal
 * @date 22/06/21,11:02 AM
 */
@Getter @Setter
public class Line {
    private Block block;
    private Float confidence;
    private Geometry geometry;
    private String id;
    private String text;
    private List<Word> words = new ArrayList<>();


    public Line(Block block, Map<String, Block> blockMap) {
        this.block = block;
        this.confidence = block.getConfidence();
        this.geometry = block.getGeometry();
        this.text = block.getText();
        this.id = block.getId();

        for (Relationship relationship : block.getRelationships()) {
            if (relationship.getType().equals("CHILD")) {
                for (var relId : relationship.getIds()) {
                    if (blockMap.get(relId).getBlockType().equals("WORD")) {
                        words.add(new Word(blockMap.get(relId)));
                    }
                }
            }
        }

    }

    public String toString() {
        var output = "Line\n==========\n";
        output = output + this.text + "\n";

        return output;
    }
}
