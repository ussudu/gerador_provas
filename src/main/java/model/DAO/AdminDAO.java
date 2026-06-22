package model.DAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import model.entities.Admin;
import model.entities.User;

public class AdminDAO {
    
    public void inserir(User user, Admin admin) {
        String sqlUser = "INSERT INTO users (name, email, password, role) VALUES (?, ?, ?, ?)";
        String sqlAdmin = "INSERT INTO admins (user_id) VALUES (?)";
        Connection conn = null;

        try {
            conn = ConnectionFactory.getConnection();
            conn.setAutoCommit(false); 
            
            PreparedStatement stmtUser = conn.prepareStatement(sqlUser, Statement.RETURN_GENERATED_KEYS);
            stmtUser.setString(1, user.getName());
            stmtUser.setString(2, user.getEmail());
            stmtUser.setString(3, user.getPassword());
            stmtUser.setString(4, "user");
            
            stmtUser.executeUpdate();

            ResultSet rs = stmtUser.getGeneratedKeys();
            int idGerado = 0;
            if (rs.next()) {
                idGerado = rs.getInt(1);
                user.setIdUser(idGerado);
                admin.setId(idGerado);
            }

            PreparedStatement stmtAdmin = conn.prepareStatement(sqlAdmin);
            stmtAdmin.setInt(1, idGerado);
            stmtAdmin.executeUpdate();
            
            conn.commit(); 

            // Fechar os recursos no final do processo
            rs.close();
            stmtUser.close();
            stmtAdmin.close();
            conn.close();

        } catch (SQLException e) {
            e.printStackTrace();
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
        }
    } 


    public List<User> listar() {
        String sql = "SELECT u.user_id, u.name, u.email, u.password, u.role " +
                     "FROM users u INNER JOIN admins a ON u.user_id = a.user_id";
        
        List<User> listaAdmins = new ArrayList<>();

        try {
            Connection conn = ConnectionFactory.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                User user = new User();
                user.setIdUser(rs.getInt("user_id"));
                user.setName(rs.getString("name"));
                user.setEmail(rs.getString("email"));
                user.setPassword(rs.getString("password"));
                
                listaAdmins.add(user);
            }

            // Fechar os recursos
            rs.close();
            stmt.close();
            conn.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return listaAdmins;
    }


    public void atualizar(User user) {
        String sql = "UPDATE users SET name = ?, email = ?, password = ? WHERE user_id = ?";

        try {
            Connection conn = ConnectionFactory.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql);

            stmt.setString(1, user.getName());
            stmt.setString(2, user.getEmail());
            stmt.setString(3, user.getPassword());
            stmt.setInt(4, user.getIdUser());

            stmt.executeUpdate();

            // Fechar os recursos
            stmt.close();
            conn.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    
    public void deletar(int idUser) {
        String sql = "DELETE FROM users WHERE user_id = ?";

        try {
            Connection conn = ConnectionFactory.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql);

            stmt.setInt(1, idUser);
            stmt.executeUpdate();

            // Fechar os recursos
            stmt.close();
            conn.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}