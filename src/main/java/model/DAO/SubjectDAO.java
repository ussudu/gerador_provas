package model.DAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import model.entities.Subject;
import model.entities.Teacher; 
import model.entities.User;

public class SubjectDAO {
    
    public void inserir(Subject subject) {
        String sql = "INSERT INTO subjects (name, teacher_id) VALUES (?, ?)";

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, subject.getName());
            
            stmt.setInt(2, subject.getTeacher().getUser().getIdUser()); 
            
            stmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    } 

    public List<Subject> listar() {
        String sql = "SELECT id_subject, name, teacher_id FROM subjects";
        List<Subject> listaSubjects = new ArrayList<>();

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Subject subject = new Subject();
                subject.setIdSubject(rs.getInt("id_subject"));
                subject.setName(rs.getString("name"));
                
                Teacher teacher = new Teacher();
                User user = new User();
                user.setIdUser(rs.getInt("teacher_id"));
                teacher.setUser(user);
                subject.setTeacher(teacher);
                
                listaSubjects.add(subject);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return listaSubjects;
    }

    public void atualizar(Subject subject) {
        String sql = "UPDATE subjects SET name = ?, teacher_id = ? WHERE id_subject = ?";

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, subject.getName());
            
            stmt.setInt(2, subject.getTeacher().getUser().getIdUser());
            stmt.setInt(3, subject.getIdSubject());

            stmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    public void deletar(int idSubject) {
        String sql = "DELETE FROM subjects WHERE id_subject = ?";

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idSubject);
            stmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}