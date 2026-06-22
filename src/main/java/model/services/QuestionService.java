package model.services;

import java.util.ArrayList;

import model.DAO.QuestionDAO;
import model.entities.Question;

public class QuestionService {
    private QuestionDAO questionDAO = new QuestionDAO();

    public void cadastrar(Question question) {
        if (question.getEnunciado() == null || question.getEnunciado().isEmpty()) {
            throw new IllegalArgumentException("O enunciado da questão é obrigatório.");
        }

        if (question.getNivelDificuldade() < 1 || question.getNivelDificuldade() > 5) {
            throw new IllegalArgumentException("O nível de dificuldade deve estar entre 1 e 5.");
        }

        questionDAO.inserir(question);
    }

    public ArrayList<Question> listar() {
        return questionDAO.listar();
    }

    public void atualizar(Question question) {
        if (question.getCodigo() <= 0) {
            throw new IllegalArgumentException("Código inválido.");
        }

        questionDAO.atualizar(question);
    }

    public void excluir(int codigo) {
        if (codigo <= 0) {
            throw new IllegalArgumentException("Código inválido.");
        }

        questionDAO.excluir(codigo);
    }
}