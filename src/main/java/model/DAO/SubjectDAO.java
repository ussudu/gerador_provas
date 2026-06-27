package model.DAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

import model.entities.Subject;
import model.entities.Teacher;
import model.entities.User;

public class SubjectDAO {
    
    public SubjectDAO() {
    }

    public void insert(Subject subject) {
        String sql = "INSERT INTO subjects (name, code, topics, teacher_id) VALUES (?, ?, ?, ?)";

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            setStatementParams(stmt, subject);
            
            stmt.executeUpdate();

            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    subject.setIdSubject(rs.getInt(1));
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao inserir disciplina no banco de dados: " + e.getMessage(), e);
        }
    } 

    public void update(Subject subject) {
        String sql = "UPDATE subjects SET name = ?, code = ?, topics = ?, teacher_id = ? WHERE id_subject = ?";

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            setStatementParams(stmt, subject);
            
            stmt.setInt(5, subject.getIdSubject());

            stmt.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao atualizar disciplina: " + e.getMessage(), e);
        }
    }
    
    public void delete(int idSubject) {
        String sql = "DELETE FROM subjects WHERE id_subject = ?";

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idSubject);
            stmt.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Não é possível excluir a disciplina. Ela pode ter questões ou provas vinculadas. Detalhes: " + e.getMessage(), e);
        }
    }

    public List<Subject> findAll() {
        String sql = "SELECT * FROM subjects";
        List<Subject> listaSubjects = new ArrayList<>();

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                listaSubjects.add(instantiateSubject(rs));
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao listar disciplinas: " + e.getMessage(), e);
        }
        
        return listaSubjects;
    }

    public Subject findById(int idSubject) {
        String sql = "SELECT * FROM subjects WHERE id_subject = ?";
        
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, idSubject);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return instantiateSubject(rs);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar disciplina por ID: " + e.getMessage(), e);
        }
        return null;
    }

    public List<Subject> findByTeacher(int teacherId) {
        String sql = "SELECT * FROM subjects WHERE teacher_id = ?";
        List<Subject> listaSubjects = new ArrayList<>();

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, teacherId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    listaSubjects.add(instantiateSubject(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao listar disciplinas do professor: " + e.getMessage(), e);
        }
        
        return listaSubjects;
    }

    // --- MÉTODOS AUXILIARES ---

    private void setStatementParams(PreparedStatement stmt, Subject subject) throws SQLException {
        stmt.setString(1, subject.getName());
        stmt.setString(2, subject.getCode());
        stmt.setString(3, subject.getTopics());
        
        if (subject.getTeacher() != null && subject.getTeacher().getUser() != null) {
            stmt.setInt(4, subject.getTeacher().getUser().getIdUser()); 
        } else {
            stmt.setNull(4, Types.INTEGER);
        }
    }

    private Subject instantiateSubject(ResultSet rs) throws SQLException {
        Subject subject = new Subject();
        subject.setIdSubject(rs.getInt("id_subject"));
        subject.setName(rs.getString("name"));
        subject.setCode(rs.getString("code"));
        subject.setTopics(rs.getString("topics"));
        
        Teacher teacher = new Teacher();
        User user = new User();
        user.setIdUser(rs.getInt("teacher_id"));
        teacher.setUser(user);
        subject.setTeacher(teacher);
        
        return subject;
    }
}