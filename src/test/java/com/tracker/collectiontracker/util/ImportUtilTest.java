package com.tracker.collectiontracker.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import com.tracker.collectiontracker.model.Category;
import com.tracker.collectiontracker.model.Collectible;
import com.tracker.collectiontracker.model.Subcategory;

/**
 * Testklasse voor {@link ImportUtil}.
 */
class ImportUtilTest {

    private ImportUtil importUtil;

    @BeforeEach
    void init() {
        importUtil = new ImportUtil();
    }

    @Test
    void testExtractCategoryFromExcel() throws Exception {
        MultipartFile file = new MockMultipartFile("Games", "Games.xlsx",
                "application/vnd.ms-excel", new ClassPathResource("mini collection.xlsx").getInputStream());

        Category category = importUtil.extractCategoryFromExcel(file);

        assertEquals("Games", category.getName());

        // Assert subcategories and collectibles.
        assertEquals(2, category.getSubcategories().size());
        Subcategory subcat = category.getSubcategories().get(0);
        assertEquals("Switch", subcat.getName());
        assertEquals(1, subcat.getCollectibles().size());

        Collectible superMarioOdyssey = subcat.getCollectibles().get(0);
        assertEquals("Super Mario Odyssey", superMarioOdyssey.getName());

        assertEquals(1, superMarioOdyssey.getImages().size());
        assertEquals("https://i.imgur.com/ABBH3xT.jpeg", superMarioOdyssey.getImages().get(0).getUrl());

        assertEquals(3, superMarioOdyssey.getTriples().size());
        assertEquals("CIB", superMarioOdyssey.getTriples().get(0).getValue());
        assertEquals("Completeness", superMarioOdyssey.getTriples().get(0).getQuestion().getName());
        assertEquals("PAL", superMarioOdyssey.getTriples().get(1).getValue());
        assertEquals("Region", superMarioOdyssey.getTriples().get(1).getQuestion().getName());
        assertEquals("€32.29 (100 found)", superMarioOdyssey.getTriples().get(2).getValue());
        assertEquals("Value on 12/07/2019", superMarioOdyssey.getTriples().get(2).getQuestion().getName());

        Subcategory ps2 = category.getSubcategories().get(1);
        assertEquals("PS2", ps2.getName());
        assertEquals(2, ps2.getCollectibles().size());

        Collectible shadowOfTheColossus = ps2.getCollectibles().get(0);
        assertEquals("Shadow of the Colossus", shadowOfTheColossus.getName());

        assertEquals(3, shadowOfTheColossus.getImages().size());
        assertEquals("https://i.imgur.com/xsyVXya.jpeg", shadowOfTheColossus.getImages().get(0).getUrl());
        assertEquals("https://i.imgur.com/xhc8bgx.jpeg", shadowOfTheColossus.getImages().get(1).getUrl());
        assertEquals("https://i.imgur.com/PehhigZ.jpeg", shadowOfTheColossus.getImages().get(2).getUrl());

        assertEquals(3, shadowOfTheColossus.getTriples().size());
        assertEquals("CB", shadowOfTheColossus.getTriples().get(0).getValue());
        assertEquals("Completeness", shadowOfTheColossus.getTriples().get(0).getQuestion().getName());
        assertEquals("NTSC-U", shadowOfTheColossus.getTriples().get(1).getValue());
        assertEquals("Region", shadowOfTheColossus.getTriples().get(1).getQuestion().getName());
        assertEquals("Special Edition", shadowOfTheColossus.getTriples().get(2).getValue());
        assertEquals("Notes", shadowOfTheColossus.getTriples().get(2).getQuestion().getName());

        Collectible psychonauts = ps2.getCollectibles().get(1);
        assertEquals("Psychonauts", psychonauts.getName());
        assertTrue(psychonauts.getImages().isEmpty());

        assertEquals(2, psychonauts.getTriples().size());
        assertEquals("Loose", psychonauts.getTriples().get(0).getValue());
        assertEquals("Completeness", psychonauts.getTriples().get(0).getQuestion().getName());
        assertEquals("NTSC-J", psychonauts.getTriples().get(1).getValue());
        assertEquals("Region", psychonauts.getTriples().get(1).getQuestion().getName());

        // Assert Questions
        assertEquals(4, category.getQuestions().size());
        assertEquals("Completeness", category.getQuestions().get(0).getName());
        assertEquals("Region", category.getQuestions().get(1).getName());
        assertEquals("Notes", category.getQuestions().get(2).getName());
        assertEquals("Value on 12/07/2019", category.getQuestions().get(3).getName());
    }
}