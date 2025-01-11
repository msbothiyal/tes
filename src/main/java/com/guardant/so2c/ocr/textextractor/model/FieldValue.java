package com.guardant.so2c.ocr.textextractor.model;

import com.amazonaws.services.textract.model.Block;
import com.amazonaws.services.textract.model.Geometry;
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
@Getter
@Setter
public class FieldValue {

    private Block block;
    private Float confidence;
    private Geometry geometry;
    private String id;
    private String text;
    private final List<Object> content = new ArrayList<>();

    public FieldValue(Block block, List<String> ids, Map<String, Block> blockMap) {
        this.block = block;
        this.confidence = block.getConfidence();
        this.geometry = block.getGeometry();
        this.id = block.getId();

        Word w;
        var str = "";
        for (var wordId : ids) {
            var blk = blockMap.get(wordId);
            if (blk.getBlockType().equals("WORD")) {
                w = new Word(blk);
                this.content.add(w);
                str = str.concat(w.getText() + " ");
            } else if (blk.getBlockType().equals("SELECTION_ELEMENT")) {
                var se = new SelectionElement(blk);
                this.content.add(se);
                this.text = se.getSelectionStatus();
            }
        }
        if (!str.isEmpty())
            this.text = str.trim();
    }

    public String toString() {
        return this.text;
    }
}
