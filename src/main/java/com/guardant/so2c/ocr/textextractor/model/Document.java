package com.guardant.so2c.ocr.textextractor.model;

import com.amazonaws.services.textract.model.Block;
import com.amazonaws.services.textract.model.GetDocumentAnalysisResult;
import com.guardant.so2c.ocr.textextractor.enums.DocType;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
/*
 * Textract mapping related POJO
 * @author msbothiyal
 * @date 22/06/21,11:02 AM
 */
@Getter
@Setter
public class Document {

    private Map<String, Block> totalBlockMap = new HashMap<>();
    private List<Page> pages = new ArrayList<>();

    @Override
    public String toString() {

        var s = "\nDocument\n==========\n";
        for (Page page : this.pages) {
            s = s.concat(page + "\n\n");
        }


        return s;
    }

    public Document(GetDocumentAnalysisResult docResult, DocType docType) {

        List<List<Block>> temp = new ArrayList<>();
        List<Block> blockList = new ArrayList<>();

        for (Block block : docResult.getBlocks()) {
            this.totalBlockMap.put(block.getId(), block);

            if(block.getBlockType().equals("PAGE") && blockList.size()>1) {
                temp.add(blockList);
                blockList = new ArrayList<>();
            }
            blockList.add(block);
        }
        temp.add(blockList);

        for (List<Block> bList: temp) {
            this.pages.add(new Page(bList, this.totalBlockMap, docType));
        }
    }
}
