package model.services;

import java.util.List;

import model.DAO.AdminDAO;
import model.entities.Admin;
import model.exceptions.RegraNegocioException;

public class AdminService {

    private AdminDAO adminDAO;
    private UserService userService;

    public AdminService(AdminDAO adminDAO, UserService userService) {
        this.adminDAO = adminDAO;
        this.userService = userService;
    }

    public void insert(Admin admin) {
        validate(admin);

        if (admin.getUser().getIdUser() <= 0) {
            userService.insert(admin.getUser());
        }

        adminDAO.insert(admin);
    }

    public void removeAdminPrivilege(int idAdmin) {
        if (idAdmin <= 0) {
            throw new RegraNegocioException("ID do administrador inválido para exclusão.");
        }
        adminDAO.delete(idAdmin);
    }

    public void inactivateAdmin(Admin admin) {
        if (admin == null || admin.getUser() == null || admin.getUser().getIdUser() <= 0) {
            throw new RegraNegocioException("Administrador inválido para inativação.");
        }
        userService.inactivate(admin.getUser().getIdUser());
    }

    public List<Admin> findAll() {
        return adminDAO.findAll();
    }

    private void validate(Admin admin) {
        if (admin == null) {
            throw new RegraNegocioException("O administrador não pode ser nulo.");
        }
        if (admin.getUser() == null) {
            throw new RegraNegocioException("Os dados de usuário do administrador são obrigatórios.");
        }
    }
}