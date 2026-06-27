package model.factory;

import model.entities.DiscursiveQuestion;
import model.entities.MultipleChoiceQuestion;
import model.entities.Question;

public class QuestionFactory {
    public static Question createQuestion(String type) {
        
        if (type == null || type.trim().isEmpty()) {
            throw new IllegalArgumentException("O tipo da questão não pode ser nulo ou vazio ao instanciar.");
        }

        switch (type.toUpperCase()) {
            case "MULTIPLE_CHOICE":
                return new MultipleChoiceQuestion();
                
            case "DISCURSIVE":
                return new DiscursiveQuestion();
                
            default:
                throw new IllegalArgumentException("Tipo de questão desconhecido no banco de dados: " + type);
        }
    }
}