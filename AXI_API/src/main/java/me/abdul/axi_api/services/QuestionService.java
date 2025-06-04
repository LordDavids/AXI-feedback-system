package me.abdul.axi_api.services;

import me.abdul.axi_api.dtos.QuestionDto;
import me.abdul.axi_api.entities.Question;
import me.abdul.axi_api.exceptions.AccessDeniedException;
import me.abdul.axi_api.exceptions.NotFoundException;
import me.abdul.axi_api.repos.QuestionRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class QuestionService {
    final QuestionRepository questionRepository;

    public QuestionService(QuestionRepository questionRepository) {
        this.questionRepository = questionRepository;
    }

    public List<Question> getAllPublicQuestions() {
        return questionRepository.findAllByIsPublicTrue();
    }

    public Question createQuestion(QuestionDto question) {
        Question newQuestion = new Question();
        newQuestion.setQuestion(question.getQuestion());
        newQuestion.setIsPublic(question.getIsPublic());
        return questionRepository.save(newQuestion);
    }

    public Question updateQuestion(Integer id, QuestionDto question) {
        Question existingQuestion = questionRepository.findById(id).orElseThrow(() -> new NotFoundException("Question not found"));

        if (existingQuestion.getIsPublic()) {
            throw new AccessDeniedException("Form is public and cannot be updated");
        }

        existingQuestion.setQuestion(question.getQuestion());

        return questionRepository.saveAndFlush(existingQuestion);
    }

    public List<Question> searchQuestions(String query) {
        return questionRepository.findAllByQuestionContainingAndIsPublicIsTrue(query);
    }
}
