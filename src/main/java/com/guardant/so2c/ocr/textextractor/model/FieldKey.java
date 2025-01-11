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
public class FieldKey {

    private Block block;
    private Float confidence;
    private Geometry geometry;
    private String id;
    private String text;
    private final List<Word> content = new ArrayList<>();

    public FieldKey(Block block, List<String> ids, Map<String, Block> blockMap) {
        this.block = block;
        this.confidence = block.getConfidence();
        this.geometry = block.getGeometry();
        this.id = block.getId();

        Word w;
        var str = "";
        for (String wordId : ids) {
            var blk = blockMap.get(wordId);
            if (blk.getBlockType().equals("WORD")) {
                w = new Word(blk);
                this.content.add(w);
                str = str.concat(w.getText() + " ");
            }
        }
        if (!str.isEmpty())
            this.text = str.trim();

    }

    public String toString() {
        return this.text;
    }
}
