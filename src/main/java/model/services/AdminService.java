package model.services;

import java.util.List;
import model.DAO.AdminDAO;
import model.entities.Admin;
import model.entities.User;
import model.entities.UserRole;

public class AdminService {

    private AdminDAO adminDAO;
    private UserService userService; 

    public AdminService(AdminDAO adminDAO, UserService userService) {
        this.adminDAO = adminDAO;
        this.userService = userService;
    }

    public void inserir(Admin admin) {
        if (admin.getUser() == null) {
            throw new IllegalArgumentException("Erro: Os dados do usuário não podem estar ausentes.");
        }

        admin.getUser().setRole(UserRole.ADMIN);

        User usuarioSalvo = userService.inserir(admin.getUser());
        
        admin.setUser(usuarioSalvo);
        adminDAO.inserir(admin);
    }

    public List<Admin> listar() {
        return adminDAO.listar();
    }

    public void atualizar(Admin admin) {
        if (admin.getUser() == null || admin.getUser().getIdUser() <= 0) { 
            throw new IllegalArgumentException("Erro: ID de administrador inválido para atualização.");
        }
        
        userService.atualizar(admin.getUser());
    }

    public void desativar(Admin admin) {
        if (admin == null || admin.getUser() == null || admin.getUser().getIdUser() <= 0) {
            throw new IllegalArgumentException("Erro: Administrador inválido para desativação.");
        }
        
        User logado = UserService.getUsuarioLogado();
        if (logado != null && logado.getIdUser() == admin.getUser().getIdUser()) {
            throw new IllegalStateException("Você não pode desativar a sua própria conta.");
        }

        userService.desativar(admin.getUser().getIdUser());
    }
}