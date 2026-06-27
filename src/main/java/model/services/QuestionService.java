package model.services;

import model.DAO.QuestionDAO;
import model.entities.DiscursiveQuestion;
import model.entities.MultipleChoiceQuestion;
import model.entities.Question;
import model.exceptions.EntidadeNaoEncontradaException;
import model.exceptions.RegraNegocioException;

import java.sql.SQLException;
import java.util.List;

public class QuestionService {

    private QuestionDAO questionDAO;

    public QuestionService(QuestionDAO questionDAO) {
        this.questionDAO = questionDAO;
    }

    public void insert(Question question) {
        validarDados(question);

        try {
            questionDAO.insert(question);
        } catch (SQLException e) {
            throw new RuntimeException("Erro interno ao tentar salvar a questão: " + e.getMessage());
        }
    }

    public void update(Question question) {
        if (question == null || question.getQuestionId() <= 0) {
            throw new RegraNegocioException("ID da questão inválido para atualização.");
        }
        
        validarDados(question);

        try {
            questionDAO.update(question);
        } catch (SQLException e) {
            throw new RuntimeException("Erro interno ao tentar atualizar a questão: " + e.getMessage());
        }
    }

    public void delete(int id) {
        if (id <= 0) {
            throw new RegraNegocioException("O Código da questão é inválido.");
        }

        try {
            questionDAO.delete(id);
        } catch (SQLException e) {
            throw new RuntimeException("Não foi possível excluir a questão. Ela pode estar vinculada a uma prova existente. Detalhes: " + e.getMessage());
        }
    }

    public List<Question> findAll() {
        try {
            return questionDAO.findAll();
        } catch (SQLException e) {
            throw new RuntimeException("Erro interno ao carregar a lista de questões: " + e.getMessage());
        }
    }

    public Question findById(int id) {
        if (id <= 0) {
            throw new RegraNegocioException("O ID para busca deve ser maior que zero.");
        }

        try {
            Question question = questionDAO.findById(id);
            if (question == null) {
                throw new EntidadeNaoEncontradaException("Nenhuma questão encontrada com o ID informado.");
            }
            return question;
        } catch (SQLException e) {
            throw new RuntimeException("Erro interno ao buscar a questão pelo ID: " + e.getMessage());
        }
    }

    public List<Question> findBySubject(int subjectId) {
        if (subjectId <= 0) {
            throw new RegraNegocioException("Selecione uma disciplina válida para realizar a busca.");
        }

        try {
            return questionDAO.findBySubject(subjectId);
        } catch (SQLException e) {
            throw new RuntimeException("Erro interno ao buscar questões por disciplina: " + e.getMessage());
        }
    }

    public List<Question> findByTopic(String topic) {
        if (topic == null || topic.trim().isEmpty()) {
            throw new RegraNegocioException("O tópico para busca não pode estar vazio.");
        }

        try {
            return questionDAO.findByTopic(topic);
        } catch (SQLException e) {
            throw new RuntimeException("Erro interno ao buscar questões por tópico: " + e.getMessage());
        }
    }

    public List<Question> findByDifficulty(String difficulty) {
        if (difficulty == null || difficulty.trim().isEmpty()) {
            throw new RegraNegocioException("Selecione um nível de dificuldade válido para a busca.");
        }

        try {
            model.entities.Difficulty.valueOf(difficulty.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new RegraNegocioException("Nível de dificuldade inválido. Selecione um nível válido na lista.");
        }

        try {
            return questionDAO.findByDifficulty(difficulty.toUpperCase());
        } catch (SQLException e) {
            throw new RuntimeException("Erro interno ao buscar questões por dificuldade: " + e.getMessage());
        }
    }

    private void validarDados(Question question) {
        if (question == null) {
            throw new RegraNegocioException("A questão não pode ser nula.");
        }

        if (question.getStatement() == null || question.getStatement().trim().isEmpty()) {
            throw new RegraNegocioException("O enunciado da questão é obrigatório.");
        }
        if (question.getAnswerKey() == null || question.getAnswerKey().trim().isEmpty()) {
            throw new RegraNegocioException("O gabarito (resposta correta) é obrigatório.");
        }
        if (question.getTopic() == null || question.getTopic().trim().isEmpty()) {
            throw new RegraNegocioException("O tópico da questão é obrigatório.");
        }

        if (question.getDifficulty() == null) {
            throw new RegraNegocioException("O nível de dificuldade é obrigatório.");
        }
        if (question.getSubject() == null || question.getSubject().getIdSubject() <= 0) {
            throw new RegraNegocioException("A questão deve pertencer a uma disciplina válida.");
        }

        if (question instanceof MultipleChoiceQuestion) {
            MultipleChoiceQuestion mcq = (MultipleChoiceQuestion) question;
            if (mcq.getAlternatives() == null || mcq.getAlternatives().size() < 2) {
                throw new RegraNegocioException("Uma questão de múltipla escolha deve conter pelo menos 2 alternativas.");
            }
        } 
        else if (question instanceof DiscursiveQuestion) {
            DiscursiveQuestion dq = (DiscursiveQuestion) question;
            if (dq.getExpectedLines() <= 0) {
                throw new RegraNegocioException("Uma questão discursiva deve ter uma quantidade esperada de linhas maior que zero.");
            }
        }
    }
}