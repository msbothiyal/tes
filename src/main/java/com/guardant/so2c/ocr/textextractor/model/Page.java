package com.guardant.so2c.ocr.textextractor.model;

import com.amazonaws.services.textract.model.Block;
import com.amazonaws.services.textract.model.Geometry;
import com.guardant.so2c.ocr.textextractor.constants.CommonConstants;
import com.guardant.so2c.ocr.textextractor.enums.DocType;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.util.*;

import static com.guardant.so2c.ocr.textextractor.constants.CommonConstants.POINT_FIVE;

/*
 * Textract mapping related POJO
 * @author msbothiyal
 * @date 22/06/21,11:02 AM
 */
@Getter
@Setter
@Slf4j
public class Page {

    private List<Block> blocks;
    private StringBuilder text;
    private List<Line> lines = new ArrayList<>();
    private Form form;
    private Geometry geometry;
    private String id;
    private final List<Object> content = new ArrayList<>();


    public Page(List<Block> blocks, Map<String, Block> blockMap, DocType docType) {
        this.blocks = blocks;
        this.text = new StringBuilder();
        this.form = new Form(findRotationInDegree(blocks));
        parse(blockMap, docType);
    }

    private int findRotationInDegree(List<Block> blocks) {

        int rotationDegree = 0;
        Optional<Block> resultBlock = blocks.stream().filter(Objects::nonNull)
                .filter(block -> block.getBlockType().equals(CommonConstants.LINE)).findFirst();
        if (resultBlock.isPresent()) {
            double top = resultBlock.get().getGeometry().getBoundingBox().getTop();
            double left = resultBlock.get().getGeometry().getBoundingBox().getLeft();
            if (left > POINT_FIVE && top < POINT_FIVE) {
                log.debug("90 Degree TRF");
                rotationDegree = 90;
            } else if (left > POINT_FIVE && top > POINT_FIVE) {
                log.debug("180 Degree TRF");
                rotationDegree = 180;
            } else if (left < POINT_FIVE && top > POINT_FIVE) {
                log.debug("270 Degree TRF");
                rotationDegree = 270;
            }
        }
        return rotationDegree;
    }

    @Override
    public String toString() {
        StringBuilder s = new StringBuilder("Page\n==========\n");
        for (Object o : this.content) {
            s.append(o).append("\n");
        }

        return s.toString();
    }

    public void parse(Map<String, Block> blockMap, DocType docType) {
        for (Block block : blocks) {
            if (block.getBlockType().equals("PAGE")) {
                this.geometry = block.getGeometry();
                this.id = block.getId();
            } else if (block.getBlockType().equals("LINE")) {
                getBlockType(blockMap, block);
            } else if (block.getBlockType().equals("KEY_VALUE_SET") && block.getEntityTypes().contains("KEY")) {
                var f = new Field(block, blockMap);
                if (null != f.getKey()) {
                    this.form.addField(f, docType);
                    this.content.add(f);
                }
            }
        }
    }

    private void getBlockType(Map<String, Block> blockMap, Block block) {
        var l = new Line(block, blockMap);
        if (l.getText().contains(CommonConstants.DOB) && l.getText().contains(CommonConstants.MEDICAL_RECORD)) {
            l.setText(CommonConstants.DOB);
            this.lines.add(l);
            this.content.add(l);
            this.text.append(l.getText()).append("\n");

            l = new Line(block, blockMap);
            l.setText(CommonConstants.MEDICAL_RECORD);
        }
        if (l.getText().contains(CommonConstants.STATE) && l.getText().contains(CommonConstants.ZIP)) {
            l.setText(CommonConstants.STATE);
            this.lines.add(l);
            this.content.add(l);
            this.text.append(l.getText()).append("\n");

            l = new Line(block, blockMap);
            l.setText(CommonConstants.ZIP);
        }
        this.lines.add(l);
        this.content.add(l);
        this.text.append(l.getText()).append("\n");
    }

    public List<Word> getLinesInReadingOrder() {

        List<Word> wordList = new ArrayList<>();
        var flag = true;

        for (var line : this.lines) {
            if (line.getText().startsWith("8.")) {
                flag = false;
            }
            if (line.getGeometry().getBoundingBox().getLeft() > 0.45 && flag) {
                wordList.add(new Word(1, line.getText(), line.getConfidence()));
            } else {
                wordList.add(new Word(0, line.getText(), line.getConfidence()));
            }
        }
        wordList.sort(Comparator.comparing(Word::getId));
        return wordList;
    }
}
