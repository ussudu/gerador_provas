package src.model.services;

import src.model.DAO.ExamDAO;
import src.model.entities.Exam;
import src.model.entities.Question;

import java.util.ArrayList;

public class ExamService {
    private ExamDAO examDAO = new ExamDAO();

    public void cadastrar(Exam exam) {
        if (exam.getSemestre() == null || exam.getSemestre().isEmpty()) {
            throw new IllegalArgumentException("O semestre da prova é obrigatório.");
        }

        examDAO.inserir(exam);
    }

    public ArrayList<Exam> listar() {
        return examDAO.listar();
    }

    public void atualizar(Exam exam) {
        if (exam.getCodigo() <= 0) {
            throw new IllegalArgumentException("Código inválido.");
        }

        examDAO.atualizar(exam);
    }

    public void excluir(int codigo) {
        if (codigo <= 0) {
            throw new IllegalArgumentException("Código inválido.");
        }

        examDAO.excluir(codigo);
    }

    public void adicionarQuestao(Exam exam, Question question) {
        if (question == null) {
            throw new IllegalArgumentException("Questão inválida.");
        }

        exam.addQuestao(question);
    }

    public void removerQuestao(Exam exam, Question question) {
        if (question == null) {
            throw new IllegalArgumentException("Questão inválida.");
        }

        exam.removerQuestao(question);
    }
}