package com.tracker.collectiontracker.util;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.tracker.collectiontracker.model.Category;
import com.tracker.collectiontracker.model.Collectible;
import com.tracker.collectiontracker.model.Question;
import com.tracker.collectiontracker.model.Triplestore;

import lombok.extern.slf4j.Slf4j;

/**
 *
 */
@Slf4j
public class ExportUtil {

    private XSSFWorkbook excel;

    private XSSFSheet sheet;

    // QuestionId, ColumnIndex map
    private Map<Long, Integer> questionColumnMap = new HashMap<>();

    // ImageNr, ColumnIndex map
    private Map<Integer, Integer> imageColumnMap = new HashMap<>();

    private int lastColumnIndex = 2;

    private Row headerRow;

    public ExportUtil() {
        excel = new XSSFWorkbook();
        sheet = excel.createSheet();
    }

    public XSSFWorkbook createExcel(Category category, List<Collectible> collectibles) {
        createHeaderRow(category);
        for (int rowIndex = 1; rowIndex < collectibles.size(); rowIndex++) {
            createCollectibleRow(collectibles.get(rowIndex), rowIndex);
        }
        return excel;
    }

    private void createHeaderRow(Category category) {
        headerRow = sheet.createRow(0);
        createCell(headerRow, 0, "Name");
        createCell(headerRow, 1, "Subcategory");

        for (Question question : category.getQuestions()) {
            createCell(headerRow, lastColumnIndex, question.getName());
            questionColumnMap.put(question.getId(), lastColumnIndex);
            lastColumnIndex++;
        }
    }

    private void createCollectibleRow(Collectible collectible, int rowIndex) {
        Row row = sheet.createRow(rowIndex);
        createCell(row, 0, collectible.getName());
        createCell(row, 1, collectible.getSubcategory().getName());
        for (Triplestore triple : collectible.getTriples()) {
            createCell(row, questionColumnMap.get(triple.getQuestion().getId()), triple.getValue());
        }

        for (int imgNr = 1; imgNr < collectible.getImages().size(); imgNr++) {
            int columnIndex = lastColumnIndex;
            if (imageColumnMap.containsKey(imgNr)) {
                columnIndex = imageColumnMap.get(imgNr);
            } else {
                createCell(headerRow, columnIndex, "img" + imgNr);
                imageColumnMap.put(imgNr, columnIndex);
                lastColumnIndex++;
            }
            createCell(row, columnIndex, collectible.getImages().get(imgNr - 1).getUrl());
        }
    }

    private void createCell(Row row, int columnIndex, String value) {
        sheet.autoSizeColumn(columnIndex);
        Cell cell = row.createCell(columnIndex);
        cell.setCellValue(value);
    }
}
