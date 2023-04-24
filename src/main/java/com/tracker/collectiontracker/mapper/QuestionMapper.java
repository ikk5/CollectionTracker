package com.tracker.collectiontracker.mapper;

import java.util.List;

import org.apache.commons.lang3.BooleanUtils;

import com.tracker.collectiontracker.model.Datatype;
import com.tracker.collectiontracker.model.Question;
import com.tracker.collectiontracker.to.QuestionTO;

/**
 *
 */
public class QuestionMapper {

    private QuestionMapper() {
    }

    public static List<QuestionTO> mapEntityListToTOs(List<Question> question) {
        return question.stream().map(QuestionMapper::mapEntityToTO).toList();
    }

    public static QuestionTO mapEntityToTO(Question question) {
        return QuestionTO.builder()
                .id(question.getId())
                .question(question.getName())
                .datatype(question.getDatatype().getName())
                .defaultValue(question.getDefaultValue())
                .hidden(BooleanUtils.isTrue(question.getHidden()))
                .listColumn(BooleanUtils.isTrue(question.getListColumn()))
                .filterColumn(BooleanUtils.isTrue(question.getFilterColumn()))
                .displayOrder(question.getDisplayOrder())
                .build();
    }

    public static Question mapTOtoEntity(QuestionTO questionTO) {
        return Question.builder()
                .name(questionTO.getQuestion())
                .datatype(Datatype.getByName(questionTO.getDatatype()))
                .defaultValue(questionTO.getDefaultValue())
                .hidden(BooleanUtils.isTrue(questionTO.getHidden()))
                .listColumn(BooleanUtils.isTrue(questionTO.getListColumn()))
                .filterColumn(BooleanUtils.isTrue(questionTO.getFilterColumn()))
                .displayOrder(questionTO.getDisplayOrder())
                .build();
    }

    public static Question mapTOtoEntityWithId(QuestionTO questionTO) {
        Question question = mapTOtoEntity(questionTO);
        question.setId(questionTO.getId());
        return question;
    }
}
