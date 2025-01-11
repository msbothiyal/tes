package com.guardant.so2c.ocr.textextractor.model;

import com.amazonaws.services.textract.model.Block;
import com.amazonaws.services.textract.model.Relationship;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;
/*
 * Textract mapping related POJO
 * @author msbothiyal
 * @date 22/06/21,11:02 AM
 */
@Getter
@Setter
public class Field {

    private FieldKey key;
    private FieldValue value;

    public Field(Block block, Map<String, Block> blockMap) {

        for (Relationship relationship : block.getRelationships()) {
            if (relationship.getType().equals("CHILD")) {
                this.key = new FieldKey(block, relationship.getIds(), blockMap);
            } else if (relationship.getType().equals("VALUE")) {
                for (String id : relationship.getIds()) {
                    getBlockMap(blockMap, id);
                }
            }
        }
    }

    private void getBlockMap(Map<String, Block> blockMap, String id) {
        var blk = blockMap.get(id);
        if (null!=blk.getRelationships() && blk.getEntityTypes().contains("VALUE")) {
                for (Relationship blkRelationship : blk.getRelationships()) {
                    if (blkRelationship.getType().equals("CHILD")) {
                        this.value = new FieldValue(blk, blkRelationship.getIds(), blockMap);
                    }
                }

        }
    }

    public String toString() {
        return "Key:".concat(this.key.getText().concat("Value:").concat(this.value.getText()));
    }
}
