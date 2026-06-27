package model.services;

import java.util.List;

import model.DAO.SubjectDAO;
import model.entities.Subject;

public class SubjectService {
    
    private SubjectDAO subjectDAO;

    public SubjectService() {
        this.subjectDAO = new SubjectDAO();
    }

    public void inserir(Subject subject) {
        if (subject.getName() == null || subject.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Erro: O nome da disciplina não pode estar vazio.");
        }
        
        if (subject.getTeacher() == null) {
            throw new IllegalArgumentException("Erro: É obrigatório associar um Professor à disciplina.");
        }
        
        if (subject.getTeacher().getUser().getIdUser() <= 0) { 
            throw new IllegalArgumentException("Erro: O Professor associado é inválido."); 
        }

        subjectDAO.inserir(subject);
    }

    public List<Subject> listar() {
        return subjectDAO.listar();
    }

    public void atualizar(Subject subject) {
        if (subject.getIdSubject() <= 0) {
            throw new IllegalArgumentException("Erro: ID da disciplina inválido para atualização.");
        }

        if (subject.getName() == null || subject.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Erro: O nome da disciplina não pode ficar vazio.");
        }

        subjectDAO.atualizar(subject);
    }

    public void deletar(int idSubject) {
        if (idSubject <= 0) {
            throw new IllegalArgumentException("Erro: O ID da disciplina fornecido é inválido.");
        }

        subjectDAO.deletar(idSubject);
    }
}