package model.DAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import model.entities.Admin;
import model.entities.User;
import model.entities.UserRole;

public class AdminDAO {

    private Connection conexao;

    public AdminDAO(Connection conexao) {
        this.conexao = conexao;
    }

    public void inserir(Admin admin) {
        String sql = "INSERT INTO admins (user_id) VALUES (?)";

        try (PreparedStatement stmt = conexao.prepareStatement(sql)) {
            stmt.setInt(1, admin.getUser().getIdUser());
            stmt.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao inserir administrador no banco: " + e.getMessage(), e);
        }
    }

    public List<Admin> listar() {
        // CORREÇÃO: u.status adicionado no SELECT
        String sql = "SELECT u.user_id, u.name, u.email, u.password, u.role, u.status " +
                     "FROM users u INNER JOIN admins a ON u.user_id = a.user_id";
        
        List<Admin> listaAdmins = new ArrayList<>();

        try (PreparedStatement stmt = conexao.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                User user = new User();
                user.setIdUser(rs.getInt("user_id"));
                user.setName(rs.getString("name"));
                user.setEmail(rs.getString("email"));
                user.setPassword(rs.getString("password"));
                
                // CORREÇÃO: Status resgatado do banco
                user.setStatus(rs.getBoolean("status"));
                
                // CORREÇÃO: .toUpperCase() adicionado
                String roleDoBanco = rs.getString("role");
                if (roleDoBanco != null) {
                    user.setRole(UserRole.valueOf(roleDoBanco.toUpperCase())); 
                }
                
                Admin admin = new Admin();
                admin.setUser(user);
                listaAdmins.add(admin);
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao listar administradores: " + e.getMessage(), e);
        }
        
        return listaAdmins;
    }
}