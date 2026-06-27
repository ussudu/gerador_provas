package model.services;

import model.DAO.UserDAO;
import model.entities.User;
import model.exceptions.RegraNegocioException;

public class UserService {

    private UserDAO userDAO;

    public UserService(UserDAO userDAO) {
        this.userDAO = userDAO;
    }

    public void insert(User user) {
        validate(user);

        if (userDAO.findByEmail(user.getEmail()) != null) {
            throw new RegraNegocioException("Já existe um usuário cadastrado com este e-mail.");
        }

        userDAO.insert(user);
    }

    public void update(User user) {
        if (user == null || user.getIdUser() <= 0) {
            throw new RegraNegocioException("ID do usuário inválido para atualização.");
        }
        validate(user);

        User existente = userDAO.findByEmail(user.getEmail());
        if (existente != null && existente.getIdUser() != user.getIdUser()) {
            throw new RegraNegocioException("Este e-mail já está sendo utilizado por outro usuário.");
        }

        userDAO.update(user);
    }

    public void updatePassword(int idUser, String newPassword) {
        if (idUser <= 0) {
            throw new RegraNegocioException("ID do usuário inválido.");
        }
        if (newPassword == null || newPassword.trim().length() < 6) {
            throw new RegraNegocioException("A nova senha deve conter pelo menos 6 caracteres.");
        }
        userDAO.updatePassword(idUser, newPassword);
    }

    public void inactivate(int idUser) {
        if (idUser <= 0) {
            throw new RegraNegocioException("ID do usuário inválido para inativação.");
        }
        userDAO.inactivate(idUser);
    }

    public User authenticate(String email, String password) {
        if (email == null || password == null || email.trim().isEmpty() || password.trim().isEmpty()) {
            throw new RegraNegocioException("E-mail e senha são obrigatórios para o login.");
        }

        User user = userDAO.authenticate(email, password);
        
        if (user == null) {
            throw new RegraNegocioException("Credenciais inválidas ou usuário inativo. Verifique seu e-mail e senha.");
        }
        return user;
    }

    private void validate(User user) {
        if (user == null) {
            throw new RegraNegocioException("O usuário não pode ser nulo.");
        }
        if (user.getName() == null || user.getName().trim().isEmpty()) {
            throw new RegraNegocioException("O nome do usuário é obrigatório.");
        }
        if (user.getEmail() == null || user.getEmail().trim().isEmpty() || !user.getEmail().contains("@")) {
            throw new RegraNegocioException("Informe um e-mail válido.");
        }
        if (user.getPassword() == null || user.getPassword().trim().length() < 6) {
            throw new RegraNegocioException("A senha deve conter pelo menos 6 caracteres.");
        }
        if (user.getRole() == null) {
            throw new RegraNegocioException("O papel (Role) do usuário deve ser definido.");
        }
    }
}