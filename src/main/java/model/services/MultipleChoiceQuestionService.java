package model.services;

import java.util.ArrayList;

import model.DAO.MultipleChoiceQuestionDAO;
import model.entities.MultipleChoiceQuestion;

public class MultipleChoiceQuestionService {
    private MultipleChoiceQuestionDAO multipleChoiceQuestionDAO = new MultipleChoiceQuestionDAO();

    public void cadastrar(MultipleChoiceQuestion question) {
        if (question.getEnunciado() == null || question.getEnunciado().isEmpty()) {
            throw new IllegalArgumentException("O enunciado da questão é obrigatório.");
        }

        if (question.getAlternativas() == null || question.getAlternativas().length < 2) {
            throw new IllegalArgumentException("A questão de múltipla escolha deve ter pelo menos 2 alternativas.");
        }

        if (question.getNivelDificuldade() < 1 || question.getNivelDificuldade() > 5) {
            throw new IllegalArgumentException("O nível de dificuldade deve estar entre 1 e 5.");
        }

        multipleChoiceQuestionDAO.inserir(question);
    }

    public ArrayList<MultipleChoiceQuestion> listar() {
        return multipleChoiceQuestionDAO.listar();
    }

    public void atualizar(MultipleChoiceQuestion question) {
        if (question.getCodigo() <= 0) {
            throw new IllegalArgumentException("Código inválido.");
        }

        multipleChoiceQuestionDAO.atualizar(question);
    }

    public void excluir(int codigo) {
        if (codigo <= 0) {
            throw new IllegalArgumentException("Código inválido.");
        }

        multipleChoiceQuestionDAO.excluir(codigo);
    }
}