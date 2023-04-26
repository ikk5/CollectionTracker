package com.tracker.collectiontracker.controller;

import static com.tracker.collectiontracker.controller.AbstractController.ORIGINS;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.tracker.collectiontracker.model.Category;
import com.tracker.collectiontracker.repository.CategoryRepository;
import com.tracker.collectiontracker.service.ImportUtil;
import com.tracker.collectiontracker.to.response.MessageResponse;

import lombok.extern.slf4j.Slf4j;

/**
 *
 */
@Slf4j
@CrossOrigin(origins = ORIGINS, maxAge = 3600, allowCredentials = "true")
@RestController
@RequestMapping("/api")
public class ImportController extends AbstractController {

    @Autowired
    private CategoryRepository categoryRepository;

    @PostMapping("/import")
    public ResponseEntity<MessageResponse> createCategoryAndCollectibles(@RequestParam("file") MultipartFile file) {
        String filename = file.getOriginalFilename();
        log.info("received file {}", filename);
        ImportUtil importUtil = new ImportUtil();

        if (importUtil.hasExcelFormat(file)) {
            try {
                Category category = importUtil.extractCategoryFromExcel(file);

                if (category != null) {
                    findLoggedInUser().addCategory(category);
                    Category savedCategory = categoryRepository.save(category);
                    return new ResponseEntity<>(new MessageResponse(filename + " imported", savedCategory.getId()), HttpStatus.OK);
                } else {
                    return new ResponseEntity<>(new MessageResponse("No category made, excel might be empty."), HttpStatus.OK);
                }
            } catch (Exception e) {
                log.error("Fail to parse Excel file: {}", e.getMessage(), e);
                return new ResponseEntity<>(new MessageResponse("Fail to parse Excel file: " + e.getMessage()), HttpStatus.BAD_REQUEST);
            }
        } else {
            return new ResponseEntity<>(new MessageResponse("Incorrect format"), HttpStatus.BAD_REQUEST);
        }
    }
}
