package com.quiz.question_service.service;

import com.quiz.question_service.model.Question;
import com.quiz.question_service.model.QuestionWrapper;
import com.quiz.question_service.model.Response;
import com.quiz.question_service.repository.QuestionDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;

@Service
public class QuestionService {

    private static final Logger logger = LoggerFactory.getLogger(QuestionService.class);

    @Autowired
    QuestionDao questionDao;

    public ResponseEntity<List<Question>> getAllQuestions() {
        logger.info("Fetching all questions");
        try {
            List<Question> questions = questionDao.findAll();
            logger.info("Fetched {} questions", questions.size());
            return new ResponseEntity<>(questions, HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Error fetching all questions", e);
            return new ResponseEntity<>(new ArrayList<>(), HttpStatus.BAD_REQUEST);
        }
    }

    public ResponseEntity<List<Question>> getQuestionsByCategory(String category) {
        logger.info("Fetching questions by category: {}", category);
        List<Question> questions = questionDao.findByCategory(category);
        logger.info("Fetched {} questions for category '{}'", questions.size(), category);
        return new ResponseEntity<>(questions, HttpStatus.OK);
    }

    public ResponseEntity<String> addQuestion(Question question) {
        logger.info("Adding new question: {}", question);
        questionDao.save(question);
        logger.info("Question added successfully with ID {}", question.getId());
        return new ResponseEntity<>("success", HttpStatus.CREATED);
    }

    public ResponseEntity<List<Integer>> getQuestionsForQuiz(String categoryName, Integer numQuestions) {
        logger.info("Generating quiz questions for category: {}, count: {}", categoryName, numQuestions);
        List<Integer> questions = questionDao.findRandomQuestionsByCategory(categoryName, numQuestions);
        logger.info("Generated {} question IDs for quiz", questions.size());
        return new ResponseEntity<>(questions, HttpStatus.OK);
    }

    public ResponseEntity<List<QuestionWrapper>> getQuestionsFromId(List<Integer> questionIds) {
        logger.info("Fetching full question details for IDs: {}", questionIds);

        List<QuestionWrapper> wrappers = new ArrayList<>();
        for (Integer id : questionIds) {
            Question q = questionDao.findById(id).orElse(null);
            if (q != null) {
                QuestionWrapper wrapper = new QuestionWrapper();
                wrapper.setId(q.getId());
                wrapper.setQuestionTitle(q.getQuestionTitle());
                wrapper.setOption1(q.getOption1());
                wrapper.setOption2(q.getOption2());
                wrapper.setOption3(q.getOption3());
                wrapper.setOption4(q.getOption4());
                wrappers.add(wrapper);
            } else {
                logger.warn("Question with ID {} not found", id);
            }
        }

        logger.info("Returning {} wrapped questions", wrappers.size());
        return new ResponseEntity<>(wrappers, HttpStatus.OK);
    }

    public ResponseEntity<Integer> getScore(List<Response> responses) {
        logger.info("Calculating score for {} responses", responses.size());

        int right = 0;
        for (Response response : responses) {
            Question q = questionDao.findById(response.getId()).orElse(null);
            if (q != null) {
                if (response.getResponse().equals(q.getCorrectAnswer())) {
                    right++;
                }
            } else {
                logger.warn("Question not found for response ID {}", response.getId());
            }
        }

        logger.info("Score calculated: {}/{}", right, responses.size());
        return new ResponseEntity<>(right, HttpStatus.OK);
    }

    public ResponseEntity<String> callQuizService() {
        logger.info("Calling QuizService /quiz/hello endpoint");

        try {
            HttpClient httpClient = HttpClient.newHttpClient();

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("http://localhost:8081/quiz/hello"))
                    .GET()
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            logger.info("Received response from QuizService: {}", response.body());

            return new ResponseEntity<>(response.body(), HttpStatus.OK);

        } catch (Exception e) {
            logger.error("Error calling QuizService", e);
            return new ResponseEntity<>("Error calling QuizService", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
