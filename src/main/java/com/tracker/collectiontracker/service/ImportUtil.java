package com.tracker.collectiontracker.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.web.multipart.MultipartFile;

import com.tracker.collectiontracker.model.Category;
import com.tracker.collectiontracker.model.Collectible;
import com.tracker.collectiontracker.model.Question;
import com.tracker.collectiontracker.model.Subcategory;

import lombok.extern.slf4j.Slf4j;

/**
 * Opens an excel file to extract a category with collectibles.
 */
@Slf4j
public class ImportUtil {

    public static final String EXCEL_TYPE = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";

    private final Category category = new Category();

    /**
     * Columnindex + question
     */
    private final HashMap<Integer, Question> questionMap = new HashMap<>();

    private final HashMap<String, Subcategory> subcategoryMap = new HashMap<>();

    private final List<Integer> imageColumnIndexes = new ArrayList<>();

    private Integer nameColumnIndex;

    private Integer subcatColumnIndex;

    public boolean hasExcelFormat(MultipartFile file) {
        return EXCEL_TYPE.equals(file.getContentType());
    }

    public Category extractCategoryFromExcel(MultipartFile file) throws Exception {
        category.setName(FilenameUtils.removeExtension(file.getOriginalFilename()));
        Workbook workbook = new XSSFWorkbook(file.getInputStream());
        Sheet sheet = workbook.getSheetAt(0);
        Iterator<Row> rows = sheet.iterator();

        int rowNumber = 0;
        while (rows.hasNext()) {
            Row currentRow = rows.next();

            // header row
            if (rowNumber == 0) {
                readHeaderRow(currentRow);
                if (nameColumnIndex == null || subcatColumnIndex == null) {
                    // No column found for the name or subcategory, can't process this. 
                    return null;
                }
                rowNumber++;
            } else {
                extractCollectible(currentRow);
            }
        }

        workbook.close();

        return category;
    }

    private void readHeaderRow(Row headerRow) {
        Iterator<Cell> cellsInRow = headerRow.iterator();

        int cellIndex = 0;
        while (cellsInRow.hasNext()) {
            Cell currentCell = cellsInRow.next();
            String value = getCellValue(currentCell);

            if (StringUtils.equalsIgnoreCase(value, "Name")) {
                nameColumnIndex = cellIndex;
            } else if (StringUtils.equalsIgnoreCase(value, "Subcategory")) {
                subcatColumnIndex = cellIndex;
            } else if (value.toLowerCase().matches("img\\d+")) {
                imageColumnIndexes.add(cellIndex);
            } else {
                Question question = new Question();
                question.setName(value);
                question.setDisplayOrder(cellIndex);
                category.addQuestion(question);
                questionMap.put(cellIndex, question);
            }

            cellIndex++;
        }
        log.info("Category extracted from excel: {}", category);
    }

    private void extractCollectible(Row row) {
        Iterator<Cell> cellsInRow = row.iterator();

        Collectible collectible = new Collectible();
        Subcategory subcategory = null;

        while (cellsInRow.hasNext()) {
            Cell currentCell = cellsInRow.next();
            String value = getCellValue(currentCell);
            int columnIndex = currentCell.getColumnIndex();

            if (columnIndex == nameColumnIndex) {
                collectible.setName(value);
            } else if (columnIndex == subcatColumnIndex) {
                subcategory = getSubcategory(value, columnIndex);
            } else if (imageColumnIndexes.contains(columnIndex)) {
                collectible.addImage(value, columnIndex);
            } else if (questionMap.get(columnIndex) != null) {
                collectible.addOrUpdateTriple(value, questionMap.get(columnIndex));
            }

        }

        if (collectible.getName() != null && subcategory != null) {
            // Only with a name and subcat will we create this link that causes it to be saved.
            collectible.setSubcategory(subcategory);
            subcategory.getCollectibles().add(collectible);
        }
    }

    private Subcategory getSubcategory(String value, int index) {
        Subcategory subcategory = subcategoryMap.get(value);
        if (subcategory == null) {
            // New subcategory
            subcategory = Subcategory.builder().name(value).displayOrder(index).build();
            category.addSubcategory(subcategory);
            subcategoryMap.put(value, subcategory);
        }
        return subcategory;
    }

    private String getCellValue(Cell cell) {
        // This gets the cellvalue as string no matter the celltype.
        DataFormatter formatter = new DataFormatter();
        return formatter.formatCellValue(cell);
    }
}

