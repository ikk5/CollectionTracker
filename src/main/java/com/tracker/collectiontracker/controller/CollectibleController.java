package com.tracker.collectiontracker.controller;

import static com.tracker.collectiontracker.controller.AbstractController.ORIGINS;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.tracker.collectiontracker.mapper.CollectibleMapper;
import com.tracker.collectiontracker.mapper.QuestionMapper;
import com.tracker.collectiontracker.model.Collectible;
import com.tracker.collectiontracker.model.Question;
import com.tracker.collectiontracker.model.Subcategory;
import com.tracker.collectiontracker.model.User;
import com.tracker.collectiontracker.repository.CollectibleRepository;
import com.tracker.collectiontracker.repository.SubcategoryRepository;
import com.tracker.collectiontracker.to.CollectibleTO;
import com.tracker.collectiontracker.to.CollectiblesListTO;
import com.tracker.collectiontracker.to.response.MessageResponse;
import com.tracker.collectiontracker.util.TimingUtil;

import lombok.extern.slf4j.Slf4j;

/**
 *
 */
@Slf4j
@CrossOrigin(origins = ORIGINS, maxAge = 3600, allowCredentials = "true")
@RestController
@RequestMapping("/api")
public class CollectibleController extends AbstractController {

    @Autowired
    private CollectibleRepository collectibleRepository;

    @Autowired
    private SubcategoryRepository subcategoryRepository;

    @GetMapping("/collectibles")
    public ResponseEntity<List<CollectibleTO>> getAllCollectibles(@RequestParam(required = false) String name) {
        try {
            log.info("getAllCollectibles called with name: {}", name);
            List<Collectible> collectibles;
            if (name == null) {
                collectibles = new ArrayList<>(collectibleRepository.findAll());
            } else {
                collectibles = new ArrayList<>(collectibleRepository.findByNameContaining(name));
            }

            if (collectibles.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }
            return new ResponseEntity<>(CollectibleMapper.mapEntityListToTOs(collectibles), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping(value = "/collectibles/list", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CollectiblesListTO> getCollectiblesListForSubcategories(@RequestHeader String subcategoryIds) {
        try {
            long start = TimingUtil.start();
            log.info("getCollectibleListForSubcategories called with subcategoryIds: {}", subcategoryIds);
            List<Collectible> collectibles = new ArrayList<>();
            List<Question> questions = new ArrayList<>();
            if (!StringUtils.isEmpty(subcategoryIds)) {
                List<Long> ids = Arrays.stream(subcategoryIds.split(",")).map(Long::valueOf).toList();
                List<Subcategory> subcategories = subcategoryRepository.findSubcategoriesByIdIn(ids);
                log.info("subcategories found: {}", subcategories);
                if (!subcategories.isEmpty()) {
                    questions = subcategories.get(0).getCategory().getQuestions();
                    questions.removeIf(question -> !question.getListColumn());
                    collectibles = new ArrayList<>(collectibleRepository.findCollectiblesBySubcategoryIn(subcategories));
                }
            }
            log.info("Retrieved {} collectibles from db in {}ms", collectibles.size(), TimingUtil.duration(start));
            start = TimingUtil.start();
            if (collectibles.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }
            CollectiblesListTO to = CollectibleMapper.mapEntitiesToCollectibleListTO(collectibles, questions);
            log.info("Mapped collectiblesListTO with {} collectiblesummaries in {}ms", to.getCollectibleSummaries().size(), TimingUtil.duration(start));
            return new ResponseEntity<>(to, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/collectibles/{id}")
    public ResponseEntity<CollectibleTO> getCollectibleById(@PathVariable("id") long id) {
        Optional<Collectible> collectible = collectibleRepository.findById(id);
        if (collectible.isPresent()) {
            CollectibleTO to = CollectibleMapper.mapEntityToTO(collectible.get());
            return new ResponseEntity<>(to, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping("/collectibles")
    public ResponseEntity<MessageResponse> createCollectible(@RequestBody CollectibleTO collectibleTO) {
        try {
            Subcategory subcategory = subcategoryRepository.findById(collectibleTO.getSubcategory().getSubcategoryId()).orElse(null);
            // Remove empty triples.
            collectibleTO.getTriples().removeIf(triplestoreTO -> StringUtils.isBlank(triplestoreTO.getValue()));
            
            Collectible collectible = CollectibleMapper.mapTOtoEntity(collectibleTO, subcategory);
            if (isValidCollectible(collectible)) {
                Collectible savedCollectible = collectibleRepository.save(collectible);
                MessageResponse response = new MessageResponse("This collectible was saved successfully!", savedCollectible.getId());
                return new ResponseEntity<>(response, HttpStatus.CREATED);
            } else {
                return new ResponseEntity<>(new MessageResponse("Collectible invalid"), HttpStatus.NOT_ACCEPTABLE);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(new MessageResponse(e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private boolean isValidCollectible(Collectible collectible) {
        return StringUtils.isNotBlank(collectible.getName()) && collectible.getSubcategory() != null;
    }

    @PutMapping("/collectibles/{id}")
    public ResponseEntity<MessageResponse> updateCollectible(@PathVariable("id") long id, @RequestBody CollectibleTO collectibleTO) {
        try {
            Optional<Collectible> collectibleData = collectibleRepository.findById(id);
            Subcategory subcategory = subcategoryRepository.findById(collectibleTO.getSubcategory().getSubcategoryId()).orElse(null);

            if (collectibleData.isPresent() && subcategory != null) {
                Collectible dbCollectible = collectibleData.get();
                if (!isCollectibleOwnedByLoggedInUser(dbCollectible)) {
                    log.warn("User {} tried to alter a different users' collectible (id {})", findLoggedInUser().getUsername(), id);
                    return new ResponseEntity<>(new MessageResponse("You're not allowed to alter collectibles from other users."), HttpStatus.UNAUTHORIZED);
                }
                dbCollectible.setName(collectibleTO.getName());
                dbCollectible.setSubcategory(subcategory);
                collectibleTO.getTriples().forEach(triple -> dbCollectible.addOrUpdateTriple(triple.getValue(),
                        QuestionMapper.mapTOtoEntityWithId(triple.getQuestion())));

                dbCollectible.clearImages();
                collectibleTO.getImages().forEach(img -> dbCollectible.addImage(img.getUrl(), img.getDisplayOrder()));

                if (isValidCollectible(dbCollectible)) {
                    collectibleRepository.save(dbCollectible);
                    return new ResponseEntity<>(new MessageResponse("This collectible was saved successfully!"), HttpStatus.OK);
                } else {
                    return new ResponseEntity<>(new MessageResponse("Collectible is invalid."), HttpStatus.NOT_ACCEPTABLE);
                }
            } else {
                return new ResponseEntity<>(new MessageResponse("No collecible found with id: " + id), HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(new MessageResponse(e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }

    @DeleteMapping("/collectibles/{id}")
    public ResponseEntity<MessageResponse> deleteCollectible(@PathVariable("id") long id) {
        ResponseEntity<MessageResponse> response;
        try {
            Collectible collectible = collectibleRepository.findById(id).orElse(null);
            if (collectible == null) {
                log.warn("Attempt to delete collectible with id {}, which couldn't be found.", id);
                response = new ResponseEntity<>(HttpStatus.NO_CONTENT);
            } else if (isCollectibleOwnedByLoggedInUser(collectible)) {
                collectibleRepository.delete(collectible);
                response = new ResponseEntity<>(new MessageResponse("Collectible has been deleted."), HttpStatus.OK);
            } else {
                log.warn("User {} tried to delete a different users' collectible (id {})", findLoggedInUser().getUsername(), id);
                response = new ResponseEntity<>(new MessageResponse("You're not allowed to delete collectibles from other users."), HttpStatus.UNAUTHORIZED);
            }
        } catch (Exception e) {
            response = new ResponseEntity<>(new MessageResponse(e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return response;
    }

    /**
     * Deletes all collectibles for the current user.
     */
    @DeleteMapping("/collectibles")
    public ResponseEntity<MessageResponse> deleteAllCollectibles() {
        try {
            User user = findLoggedInUser();
            List<Collectible> collectibles = new ArrayList<>();
            user.getCategories().forEach(cat -> cat.getSubcategories().forEach(subcat -> {
                collectibles.addAll(subcat.getCollectibles());
                subcat.getCollectibles().clear();
            }));
            collectibleRepository.deleteAll(collectibles);
            log.info("{} collectibles deleted.", collectibles.size());
            return new ResponseEntity<>(new MessageResponse(String.format("%s collectibles have been deleted.", collectibles.size())), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(new MessageResponse(e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private boolean isCollectibleOwnedByLoggedInUser(Collectible collectible) {
        User loggedInUser = findLoggedInUser();
        return loggedInUser != null && StringUtils.equals(loggedInUser.getUsername(),
                collectible.getSubcategory().getCategory().getUser().getUsername());
    }
}
