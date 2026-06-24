package model.services;

import java.util.List;

import model.DAO.UserDAO;
import model.entities.User;

public class UserService {

    private UserDAO userDAO;
    private static User usuarioLogado;

    public UserService(UserDAO userDAO) {
        this.userDAO = userDAO;
    }

    public User inserir(User user) {
        validarDadosUsuario(user);
        return userDAO.inserir(user);
    }

    public List<User> listar() {
        return userDAO.listar();
    }

    public void atualizar(User user) {
        if (user.getIdUser() <= 0) {
            throw new IllegalArgumentException("Erro: ID de usuário inválido para atualização.");
        }
        
        validarDadosUsuario(user);
        userDAO.atualizar(user);
    }

    public void desativar(int idUser) {
        if (idUser <= 0) {
            throw new IllegalArgumentException("Erro: O ID fornecido é inválido.");
        }
        userDAO.desativar(idUser);
    }

    public User fazerLogin(String email, String senha) {
        User user = userDAO.buscarPorEmailESenha(email, senha);
        if (user == null) {
            throw new RuntimeException("E-mail ou senha incorretos.");
        }

        if (!user.getStatus()) {
            throw new RuntimeException("Sua conta está desativada. Procure a administração.");
        }
        usuarioLogado = user;
        return user;
    }


    public static User getUsuarioLogado() {
        return usuarioLogado;
    }

    public static void logout() {
        usuarioLogado = null;
    }

    private void validarDadosUsuario(User user) {
        if (user == null) {
            throw new IllegalArgumentException("Erro: Os dados do usuário não podem estar ausentes.");
        }
        if (user.getName() == null || user.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Erro: O nome não pode estar vazio.");
        }
        if (user.getEmail() == null || user.getEmail().trim().isEmpty()) {
            throw new IllegalArgumentException("Erro: O email não pode estar vazio.");
        }
        if (user.getPassword() == null || user.getPassword().trim().isEmpty()) {
            throw new IllegalArgumentException("Erro: A palavra-passe não pode estar vazia.");
        }
        
        if (user.getRole() == null) {
            throw new IllegalArgumentException("Erro: O papel (role) do usuário deve ser definido.");
        }
    }
}