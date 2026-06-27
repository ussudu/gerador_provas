package model.services;

import java.util.List;
import model.DAO.SubjectDAO;
import model.entities.Subject;
import model.exceptions.RegraNegocioException;

public class SubjectService {

    private SubjectDAO subjectDAO;

    public SubjectService(SubjectDAO subjectDAO) {
        this.subjectDAO = subjectDAO;
    }

    public void insert(Subject subject) {
        validate(subject);
        subjectDAO.insert(subject);
    }

    public void update(Subject subject) {
        if (subject.getIdSubject() <= 0) {
            throw new RegraNegocioException("ID da disciplina inválido para atualização.");
        }
        validate(subject);
        subjectDAO.update(subject);
    }

    public void delete(int idSubject) {
        if (idSubject <= 0) {
            throw new RegraNegocioException("ID inválido para exclusão.");
        }
        // A exclusão chama o DAO. Se houver violação de chave estrangeira (ex: disciplina tem questões),
        // o catch do DAO lançará a RuntimeException que vai ser capturada pela sua View.
        subjectDAO.delete(idSubject);
    }

    public List<Subject> findAll() {
        return subjectDAO.findAll();
    }

    public Subject findById(int idSubject) {
        if (idSubject <= 0) {
            throw new RegraNegocioException("ID inválido para busca.");
        }
        return subjectDAO.findById(idSubject);
    }

    public List<Subject> findByTeacher(int teacherId) {
        if (teacherId <= 0) {
            throw new RegraNegocioException("ID do professor inválido para busca.");
        }
        return subjectDAO.findByTeacher(teacherId);
    }
    
    private void validate(Subject subject) {
        if (subject == null) {
            throw new RegraNegocioException("A disciplina não pode ser nula.");
        }
        
        if (subject.getName() == null || subject.getName().trim().isEmpty()) {
            throw new RegraNegocioException("O nome da disciplina é obrigatório.");
        }
        
        if (subject.getCode() == null || subject.getCode().trim().isEmpty()) {
            throw new RegraNegocioException("O código da disciplina é obrigatório.");
        }
        if (subject.getTopics() == null || subject.getTopics().trim().isEmpty()) {
            throw new RegraNegocioException("Os assuntos da disciplina são obrigatórios.");
        }

        if (subject.getTeacher() == null || 
            subject.getTeacher().getUser() == null || 
            subject.getTeacher().getUser().getIdUser() <= 0) {
            throw new RegraNegocioException("A disciplina deve estar vinculada a um professor válido.");
        }
    }
}