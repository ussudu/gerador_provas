package model.DAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import model.entities.User;
import model.entities.UserRole;

public class UserDAO {

    public void insert(User user) {
        String sql = "INSERT INTO user (name, email, role, password, status) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            setStatementParams(stmt, user);
            stmt.setString(4, user.getPassword());
            stmt.setBoolean(5, true);

            stmt.executeUpdate();

            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    user.setIdUser(rs.getInt(1));
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao inserir usuário no banco: " + e.getMessage(), e);
        }
    }

    public void update(User user) {
        String sql = "UPDATE user SET name = ?, email = ?, role = ? WHERE id_user = ?";

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            setStatementParams(stmt, user);
            stmt.setInt(4, user.getIdUser());

            stmt.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao atualizar dados do usuário: " + e.getMessage(), e);
        }
    }

    public void updatePassword(int idUser, String newPassword) {
        String sql = "UPDATE user SET password = ? WHERE id_user = ?";

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, newPassword);
            stmt.setInt(2, idUser);
            stmt.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao atualizar senha: " + e.getMessage(), e);
        }
    }

    public void inactivate(int idUser) {
        String sql = "UPDATE user SET status = false WHERE id_user = ?";

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idUser);
            stmt.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao inativar usuário: " + e.getMessage(), e);
        }
    }

    public User findById(int idUser) {
        String sql = "SELECT * FROM user WHERE id_user = ?";
        
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, idUser);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return instantiateUser(rs);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar usuário por ID: " + e.getMessage(), e);
        }
        return null;
    }

    public User findByEmail(String email) {
        String sql = "SELECT * FROM user WHERE email = ?";
        
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, email);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    System.out.println("DEBUG: Encontrei o usuário no banco! ID: " + rs.getInt("id_user")); // <--- ADICIONE ISSO
                    return instantiateUser(rs);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar usuário por E-mail: " + e.getMessage(), e);
        }
        return null;
    }

    public User authenticate(String email, String password) {
        String sql = "SELECT * FROM user WHERE email = ? AND password = ? AND status = true";
        
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, email);
            stmt.setString(2, password);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return instantiateUser(rs);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro na autenticação: " + e.getMessage(), e);
        }
        return null;
    }

    private void setStatementParams(PreparedStatement stmt, User user) throws SQLException {
        stmt.setString(1, user.getName());
        stmt.setString(2, user.getEmail());
        stmt.setString(3, user.getRole().name());
    }

    public static User instantiateUser(ResultSet rs) throws SQLException {
        User user = new User();
        user.setIdUser(rs.getInt("id_user"));
        user.setName(rs.getString("name"));
        user.setEmail(rs.getString("email"));
        user.setPassword(rs.getString("password"));
        user.setRole(UserRole.valueOf(rs.getString("role"))); 
        user.setStatus(rs.getBoolean("status"));
        return user;
    }
}