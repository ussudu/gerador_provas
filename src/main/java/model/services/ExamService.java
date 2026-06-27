package model.services;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import model.DAO.ExamDAO;
import model.entities.Exam;
import model.entities.Question;

public class ExamService {

    private ExamDAO examDAO;

    public ExamService(ExamDAO examDAO) {
        this.examDAO = examDAO;
    }

    public void cadastrar(Exam exam) {
        validarDados(exam);
        examDAO.inserir(exam);
    }

    public ArrayList<Exam> listar() {
        return examDAO.listar();
    }

    public void atualizar(Exam exam) {

        if (exam.getExamId() <= 0) {
            throw new IllegalArgumentException("Código da prova inválido.");
        }
        validarDados(exam);

        examDAO.atualizar(exam);
    }

    public void excluir(int codigo) {

        if (codigo <= 0) {
            throw new IllegalArgumentException("Código da prova inválido.");
        }

        examDAO.excluir(codigo);
    }

    public void adicionarQuestao(Exam exam, Question question) {
        if (exam == null || question == null) {
            throw new IllegalArgumentException("Prova ou questão inválida.");
        }

        exam.addQuestao(question);
        if (exam.getExamId() > 0) {
            examDAO.vincularQuestao(exam.getExamId(), question.getQuestionId());
        }
    }

    public void removerQuestao(Exam exam, Question question) {
        if (exam == null || question == null) {
            throw new IllegalArgumentException("Prova ou questão inválida.");
        }

        exam.removerQuestao(question);

        if (exam.getExamId() > 0) {
            examDAO.desvincularQuestao(exam.getExamId(), question.getQuestionId());
        }
    }

    public List<Exam> buscarPorSemestre(String semestre) {
        if (semestre == null || semestre.trim().isEmpty()) {
            throw new IllegalArgumentException("O campo semestre é obrigatório e não pode ser vazio.");
        }

        if (!semestre.matches("\\d{4}\\.\\d")) {
            throw new IllegalArgumentException("O semestre deve seguir o formato padrão (Ex: 2026.1 ou 2026.2).");
        }

        try {
            return examDAO.findBySemestre(semestre);
        } catch (SQLException e) {
            throw new RuntimeException("Erro no sistema ao buscar provas pelo semestre: " + e.getMessage());
        }
    }

    public List<Exam> buscarPorDisciplina(int disciplinaId) {
        if (disciplinaId <= 0) {
            throw new IllegalArgumentException("ID da disciplina inválido. Selecione uma disciplina válida na lista.");
        }

        try {
            return examDAO.findByDisciplina(disciplinaId);
        } catch (SQLException e) {
            throw new RuntimeException("Erro no sistema ao buscar provas pela disciplina: " + e.getMessage());
        }
    }
    public Exam buscarPorId(int id) {
        if (id <= 0) {
            throw new IllegalArgumentException("ID da prova inválido. O ID deve ser maior que zero.");
        }
        
        Exam exam = examDAO.findById(id);
        
        if (exam == null) {
            throw new IllegalArgumentException("Nenhuma prova encontrada com o ID informado.");
        }
        
        return exam;
    }
    private void validarDados(Exam exam)
    {
        if (exam == null) {
            throw new IllegalArgumentException("A prova não pode ser nula.");
        }

        if (exam.getSubject() == null) {
            throw new IllegalArgumentException("A disciplina da prova é obrigatória.");
        }

        if (exam.getSemester() == null || exam.getSemester().trim().isEmpty()) {
            throw new IllegalArgumentException("O semestre da prova é obrigatório.");
        }

        if (exam.getTeacher() == null) {
            throw new IllegalArgumentException("O professor da prova é obrigatório.");
        }
    }
}