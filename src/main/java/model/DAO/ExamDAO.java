package model.DAO;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import model.entities.Exam;
import model.entities.Subject;
import model.entities.Teacher;
import model.entities.User;

public class ExamDAO {

    public void inserir(Exam exam) {
        String sql = "INSERT INTO exams (creation_date, semester, subject_id, teacher_id) VALUES (?, ?, ?, ?)";

        try (Connection conexao = ConnectionFactory.getConnection();
             PreparedStatement stmt = conexao.prepareStatement(sql)) {

            if (exam.getCreationDate() != null) {
                stmt.setDate(1, Date.valueOf(exam.getCreationDate()));
            } else {
                stmt.setDate(1, null);
            }

            stmt.setString(2, exam.getSemester());
            stmt.setInt(3, exam.getSubject().getIdSubject());
            stmt.setInt(4, exam.getTeacher().getUser().getIdUser());

            stmt.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao inserir prova no banco: " + e.getMessage(), e);
        }
    }

    public ArrayList<Exam> listar() {
        String sql = "SELECT exam_id, creation_date, semester, subject_id, teacher_id FROM exams";

        ArrayList<Exam> exams = new ArrayList<>();

        try (Connection conexao = ConnectionFactory.getConnection();
             PreparedStatement stmt = conexao.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Exam exam = new Exam();

                exam.setExamId(rs.getInt("exam_id"));

                Date data = rs.getDate("creation_date");
                if (data != null) {
                    exam.setCreationDate(data.toLocalDate());
                }

                exam.setSemester(rs.getString("semester"));

                Subject subject = new Subject();
                subject.setIdSubject(rs.getInt("subject_id"));
                exam.setSubject(subject);

                Teacher teacher = new Teacher();
                User user = new User();
                user.setIdUser(rs.getInt("teacher_id"));
                teacher.setUser(user);
                exam.setTeacher(teacher);

                exams.add(exam);
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao listar provas: " + e.getMessage(), e);
        }

        return exams;
    }

    public void atualizar(Exam exam) {
        String sql = "UPDATE exams SET creation_date = ?, semester = ?, subject_id = ?, teacher_id = ? WHERE exam_id = ?";

        try (Connection conexao = ConnectionFactory.getConnection();
             PreparedStatement stmt = conexao.prepareStatement(sql)) {

            if (exam.getCreationDate() != null) {
                stmt.setDate(1, Date.valueOf(exam.getCreationDate()));
            } else {
                stmt.setDate(1, null);
            }

            stmt.setString(2, exam.getSemester());
            stmt.setInt(3, exam.getSubject().getIdSubject());
            stmt.setInt(4, exam.getTeacher().getUser().getIdUser());
            stmt.setInt(5, exam.getExamId());

            stmt.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao atualizar prova: " + e.getMessage(), e);
        }
    }

    public void excluir(int codigo) {
        String sql = "DELETE FROM exams WHERE exam_id = ?";

        try (Connection conexao = ConnectionFactory.getConnection();
             PreparedStatement stmt = conexao.prepareStatement(sql)) {

            stmt.setInt(1, codigo);
            stmt.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao excluir prova: " + e.getMessage(), e);
        }
    }

    public List<Exam> findBySemestre(String semestre) throws SQLException {
        String sql = "SELECT * FROM exams WHERE semester = ?";
        List<Exam> exams = new ArrayList<>();
        
        try (Connection conexao = ConnectionFactory.getConnection();
             PreparedStatement stmt = conexao.prepareStatement(sql)) {
            stmt.setString(1, semestre);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                Exam exam = new Exam();

                exam.setExamId(rs.getInt("exam_id"));

                Date data = rs.getDate("creation_date");
                if (data != null) {
                    exam.setCreationDate(data.toLocalDate());
                }

                exam.setSemester(rs.getString("semester"));

                Subject subject = new Subject();
                subject.setIdSubject(rs.getInt("subject_id"));
                exam.setSubject(subject);

                Teacher teacher = new Teacher();
                User user = new User();
                user.setIdUser(rs.getInt("teacher_id"));
                teacher.setUser(user);
                exam.setTeacher(teacher);

                exams.add(exam);
            }
            }
        }
        return exams;
    }

    public List<Exam> findByDisciplina(int disciplinaId) throws SQLException {
        String sql = "SELECT * FROM exams WHERE subject_id = ?";
        List<Exam> exams = new ArrayList<>();
        
        try (Connection conexao = ConnectionFactory.getConnection();
             PreparedStatement stmt = conexao.prepareStatement(sql)) {
            stmt.setInt(1, disciplinaId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                Exam exam = new Exam();

                exam.setExamId(rs.getInt("exam_id"));

                Date data = rs.getDate("creation_date");
                if (data != null) {
                    exam.setCreationDate(data.toLocalDate());
                }

                exam.setSemester(rs.getString("semester"));

                Subject subject = new Subject();
                subject.setIdSubject(rs.getInt("subject_id"));
                exam.setSubject(subject);

                Teacher teacher = new Teacher();
                User user = new User();
                user.setIdUser(rs.getInt("teacher_id"));
                teacher.setUser(user);
                exam.setTeacher(teacher);

                exams.add(exam);
            }
            }
        }
        return exams;
    }
    public Exam findById(int id) {
        String sql = "SELECT * FROM exams WHERE exam_id = ?";
        
        try (Connection conexao = ConnectionFactory.getConnection();
             PreparedStatement stmt = conexao.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            
            try (ResultSet rs = stmt.executeQuery()) {

                if (rs.next()) {
                    Exam exam = new Exam();

                exam.setExamId(rs.getInt("exam_id"));

                Date data = rs.getDate("creation_date");
                if (data != null) {
                    exam.setCreationDate(data.toLocalDate());
                }

                exam.setSemester(rs.getString("semester"));

                Subject subject = new Subject();
                subject.setIdSubject(rs.getInt("subject_id"));
                exam.setSubject(subject);

                Teacher teacher = new Teacher();
                User user = new User();
                user.setIdUser(rs.getInt("teacher_id"));
                teacher.setUser(user);
                exam.setTeacher(teacher);
                    return exam;
                }
            }
            
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar a prova por ID: " + e.getMessage(), e);
        }
        
        return null;
    }
    public void vincularQuestao(int examId, int questionId) {
        String sql = "INSERT INTO exam_question (exam_id, question_id) VALUES (?, ?)";

        try (Connection conexao = ConnectionFactory.getConnection();
             PreparedStatement stmt = conexao.prepareStatement(sql)) {

            stmt.setInt(1, examId);
            stmt.setInt(2, questionId);

            stmt.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao vincular a questão à prova no banco de dados: " + e.getMessage(), e);
        }
    }

    public void desvincularQuestao(int examId, int questionId) {
        String sql = "DELETE FROM exam_question WHERE exam_id = ? AND question_id = ?";

        try (Connection conexao = ConnectionFactory.getConnection();
             PreparedStatement stmt = conexao.prepareStatement(sql)) {

            stmt.setInt(1, examId);
            stmt.setInt(2, questionId);

            stmt.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao desvincular a questão da prova no banco de dados: " + e.getMessage(), e);
        }
    }
}