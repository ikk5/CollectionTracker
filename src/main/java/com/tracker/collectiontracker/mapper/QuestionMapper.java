package com.tracker.collectiontracker.mapper;

import java.util.List;

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
                .build();
    }

    public static Question mapTOtoEntity(QuestionTO questionTO) {
        return Question.builder()
                .name(questionTO.getQuestion())
                .datatype(Datatype.getByName(questionTO.getDatatype()))
                .defaultValue(questionTO.getDefaultValue())
                .build();
    }
}
