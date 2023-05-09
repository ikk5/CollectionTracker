package com.tracker.collectiontracker.controller;

import static com.tracker.collectiontracker.controller.AbstractController.ORIGINS;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import com.tracker.collectiontracker.model.Category;
import com.tracker.collectiontracker.model.Collectible;
import com.tracker.collectiontracker.model.User;
import com.tracker.collectiontracker.repository.CollectibleRepository;
import com.tracker.collectiontracker.util.ExportUtil;
import com.tracker.collectiontracker.util.TimingUtil;

import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

/**
 *
 */
@Slf4j
@CrossOrigin(origins = ORIGINS, maxAge = 36000, allowCredentials = "true")
@RestController
@RequestMapping("/api")
public class ExportController extends AbstractController {

    @Autowired
    private CollectibleRepository collectibleRepository;

    @GetMapping("/export")
    public ResponseEntity<StreamingResponseBody> exportUserCategories(HttpServletResponse response) {
        try {
            User user = findLoggedInUser();
            log.info("Export collection for {}", user.getUsername());

            Map<String, XSSFWorkbook> excels = new HashMap<>();
            for (Category category : user.getCategories()) {
                long start = TimingUtil.start();
                ExportUtil exportUtil = new ExportUtil();
                List<Collectible> collectibles = collectibleRepository.findCollectiblesBySubcategoryIn(category.getSubcategories());
                if (collectibles.size() < 1000) {
                    log.info("Create excel for category {} with {} collectibles", category.getName(), collectibles.size());
                    excels.put(category.getName(), exportUtil.createExcel(category, collectibles));
                    log.info("Created excel for category {} with {} collectibles in {}ms", category.getName(), collectibles.size(), TimingUtil.duration(start));
                }
            }

            return ResponseEntity.ok()
                    .header("Content-Disposition", "attachment;filename=export.zip")
                    .contentType(MediaType.valueOf("application/zip"))
                    .body(out -> zipExcels(excels, response.getOutputStream()));
        } catch (Exception e) {
            log.error("Error while exporting", e);
            e.printStackTrace();
            return ResponseEntity.badRequest().build();
        }
    }

    private ZipOutputStream zipExcels(Map<String, XSSFWorkbook> excels, ServletOutputStream servletOS) throws IOException {
        ZipOutputStream zipOS = new ZipOutputStream(servletOS);
        for (Map.Entry<String, XSSFWorkbook> xls : excels.entrySet()) {
            zipOS.putNextEntry(new ZipEntry(xls.getKey() + ".xlsx"));
            ByteArrayOutputStream byteOS = new ByteArrayOutputStream();
            xls.getValue().write(byteOS);
            byteOS.writeTo(zipOS);
            zipOS.closeEntry();
        }
        zipOS.close();
        return zipOS;
    }
}
