package model.services;

import java.util.List;

import model.DAO.TeacherDAO;
import model.entities.Teacher;
import model.exceptions.EntidadeNaoEncontradaException;
import model.exceptions.RegraNegocioException;

public class TeacherService {

    private TeacherDAO teacherDAO;
    private UserService userService;

    public TeacherService(TeacherDAO teacherDAO, UserService userService) {
        this.teacherDAO = teacherDAO;
        this.userService = userService;
    }

    public void insert(Teacher teacher) {
        validate(teacher);
        if (teacher.getUser().getIdUser() <= 0) {
            userService.insert(teacher.getUser());
        }

        teacherDAO.insert(teacher);
    }

    public void update(Teacher teacher) {
        if (teacher == null || teacher.getUser() == null || teacher.getUser().getIdUser() <= 0) {
            throw new RegraNegocioException("ID do professor inválido para atualização.");
        }
        validate(teacher);

        teacherDAO.update(teacher);
        userService.update(teacher.getUser());
    }

    public List<Teacher> findAll() {
        return teacherDAO.findAll();
    }

    public Teacher findById(int idTeacher) {
        if (idTeacher <= 0) {
            throw new RegraNegocioException("O ID do professor deve ser maior que zero.");
        }

        Teacher teacher = teacherDAO.findById(idTeacher);
        if (teacher == null) {
            throw new EntidadeNaoEncontradaException("Nenhum professor encontrado com o ID informado.");
        }
        return teacher;
    }
    public void inactivateAdmin(Teacher teacher) {
        if (teacher == null || teacher.getUser() == null || teacher.getUser().getIdUser() <= 0) {
            throw new RegraNegocioException("Professor inválido para inativação.");
        }
        userService.inactivate(teacher.getUser().getIdUser());
    }

    private void validate(Teacher teacher) {
        if (teacher == null) {
            throw new RegraNegocioException("O professor não pode ser nulo.");
        }
        if (teacher.getUser() == null) {
            throw new RegraNegocioException("Os dados de usuário (nome, e-mail) do professor são obrigatórios.");
        }
        if (teacher.getRegistration_number() == null || teacher.getRegistration_number().trim().isEmpty()) {
            throw new RegraNegocioException("A matrícula (Registration Number) do professor é obrigatória.");
        }
    }
}